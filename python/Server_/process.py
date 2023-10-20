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
    
    def deletenow(self):
        self.now_processing = []

    
    def to_json(self):
        result_list = []
        for record in self.output_record:
            result_list.append({'txt':record[0], 'who':record[1]})
        result = json.dumps(result_list, indent=4)
        with open('ouput.json', 'w') as output:
            output.write(result)
        return result


    def process(self, data):
        tensor_file = self.bin_to_tensor(data)
        text = self.transcript(data)
        return self.to_json()


                
                
                


if __name__ == "__main__":
    agent = Voice_process_agent(need_load=True)
    agent.process('Server_/received_song.bin')
    print(len(agent.voice_record))
