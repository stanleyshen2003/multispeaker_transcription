from speechbrain.pretrained import SepformerSeparation as separator
from speechbrain.pretrained import SpeakerRecognition
import torchaudio
import os
import numpy as np
from hyperpyyaml import load_hyperpyyaml
import json
import torch
from speechbrain.pretrained import EncoderDecoderASR
import speech_recognition as sr
import io 

'''
not used
'''
# class myseparator(separator):
#     def from_hparams(source):
#         with open(source) as fin:
#             hparams = load_hyperpyyaml(fin, {})
#         return cls(hparams["modules"],hparams,NULL)

# print(os.path.abspath("pretrained_models/sepformer-wsj02mix/hyperparams.yaml"))
# model = myseparator.from_hparams(source='pretrained_models/sepformer-wsj02mix/hyperparams.yaml')


'''
!!!! write your code under here !!!!
'''
class Voice_process_agent():
    '''
    init:
        separate_model_name: the model you want to use
        need_load: whether you have a local file of model
    '''
    def __init__(self, separate_model_name = "sepformer-wsj02mix", verification_model_name = "spkrec-ecapa-voxceleb", need_load = True):
        self.maxPeople = 5
        self.verification_model = self.load_verify_model(verification_model_name, need_load)
        ''' 
        store all the audio files here
        format: list of tuple(audio, index)
        index is 0 to 4, if a new audio identified and exceed people limit, index = -1
        '''
        self.voice_record = []
        self.now_processing = []
        self.output_record = []

    
    def load_verify_model(self, model_name, need_load):
        """
        input:
            model_name(string): the location of your model.
            need_load(bool): whether you have to load from hugging face library
        output:
            model: a model that can call model.separate_file(path=your.wav)
        """
        url = 'speechbrain/' + model_name if need_load else 'pretrained_models/' + model_name
        model = SpeakerRecognition.from_hparams(source=url, savedir='pretrained_models/'+model_name)
        return model
    
    def bin_to_tensor(self, binary):
        # with open("source0.wav", "rb") as file:
        #     binary = file.read()
        waveform, sample_rate = torchaudio.load(io.BytesIO(binary))
        return waveform




    def transcript(self, binary):
        r = sr.Recognizer()
        # binary file would be altered with binary file_name(if can)
        binary = io.BytesIO(binary)
        data = sr.AudioFile(binary)

        with data as source:
            audio = r.record(source)
        try:
            s = r.recognize_google(audio)
            self.output_record.append([s,0])
            print("Text: "+s)
        except Exception as e:
            print("Exception: "+str(e))


    def determine_identical(self, voice1, voice2):
        #voice1, voice2 = voice1.unsqueeze(0).unsqueeze(0), voice2.unsqueeze(0).unsqueeze(0)  # Add a batch dimension
        voice1, voice2 = voice1[:,:16000], voice2[:,:16000]
        score, prediction = self.verification_model.verify_batch(voice1, voice2, threshold=0.5)
        return prediction.item()
    
    # def separate_files(self, file_name, save_separate = True):
    #     result = self.separate_model.separate_file(path=file_name)
        
    #     if save_separate:
    #         for i in range(np.array(result.shape)[-1]):
    #             fileout = "source" + str(i) + ".wav"
    #             torchaudio.save(fileout, result[:, :, i].detach().cpu(), 8000)
            
    #     for i in range(np.array(result.shape)[-1]):        
    #         result[:,:,i] = result[:,:,i].detach().cpu()
    #         found = False
    #         for j in range(len(self.voice_record)):
    #             same = self.determine_identical(result[:,:,i], self.voice_record[j][0])
    #             if same:
    #                 self.now_processing.append((result[:,:,i],j))
    #                 found = True
    #                 break
    #         if not found and len(self.voice_record) < self.maxPeople:
    #             self.now_processing.append((result[:,:,i],len(self.voice_record)))
    #             self.voice_record.append((result[:,:,i],len(self.voice_record)))
    #         elif not found:
    #             self.now_processing.append((result[:,:,i],-1))
    
    def deletenow(self):
        self.now_processing = []

    
    def to_json(self):
        result_list = []
        for record in self.output_record:
            result_list.append({'txt':record[0], 'who':record[1]})
        result = json.dumps(result_list, indent=4)
        with open('ouput.json', 'w') as output:
            output.write(result)


    def split(self, input_data, seconds, output_prefix):
        #wav_file = io.BytesIO(input_data)
        with wave.open(input_data, 'rb') as wav_file:
            # Get the frame rate and total frames
            frame_rate = wav_file.getframerate()
            total_frames = wav_file.getnframes()

            # Calculate the number of frames for the specified duration
            frames_per_second = frame_rate * seconds
            frames_per_chunk = int(frames_per_second)

            # Read and write chunks of audio
            for i in range(0, total_frames, frames_per_chunk):
                wav_file.setpos(i)
                frames = min(frames_per_chunk, total_frames - i)
                chunk_data = wav_file.readframes(frames)

            # Write the chunk to a new file
                output_file = f"{output_prefix}_{i // frame_rate}s.wav"
                with wave.open(output_file, 'wb') as output_wav:
                    output_wav.setnchannels(wav_file.getnchannels())
                    output_wav.setsampwidth(wav_file.getsampwidth())
                    output_wav.setframerate(frame_rate)
                    output_wav.writeframes(chunk_data)

    def process(self, path):
        file = open(path, "rb").read()
        tensor_file = self.bin_to_tensor(file)
        text = self.transcript(file)
        self.to_json()


                
                
                



# model = separator.from_hparams(source='pretrained_models/sepformer-wsj02mix', savedir='pretrained_models/sepformer-wsj02mix')

# # for custom file, change path
# est_sources = model.separate_file(path='test_mixture.wav') 


# for i in range(np.array(est_sources.shape)[-1]):
#     fileout = "source" + str(i) + ".wav"
#     torchaudio.save(fileout, est_sources[:, :, i].detach().cpu(), 8000)

if __name__ == "__main__":
    agent = Voice_process_agent(need_load=True)
    #agent.separate_files("src/test4.wav", save_separate=True)
    #agent.transcript()
    agent.process('python/Server_/received_song.wav')
    print(len(agent.voice_record))

# verification = SpeakerRecognition.from_hparams(source="speechbrain/spkrec-ecapa-voxceleb", savedir="pretrained_models/spkrec-ecapa-voxceleb")
# score, prediction = verification.verify_files("Keven.wav", "Stanely_2.wav")

# print(prediction, score)