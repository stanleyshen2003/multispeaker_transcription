from speechbrain.pretrained import SpeakerRecognition
import torchaudio
import json
import speech_recognition as sr
import io 
import google.generativeai as palm

class Voice_process_agent():
    '''
    init:
        separate_model_name: the model you want to use
        need_load: whether you have a local file of model
    '''
    def __init__(self, verification_model_name = "spkrec-ecapa-voxceleb", need_load = True, apiKey = None):
        self.maxPeople = 5
        self.verification_model = self.load_verify_model(verification_model_name, need_load)
        self.r = sr.Recognizer()
        palm.configure(api_key=apiKey)

        ''' 
        store all the audio files here
        format: list of tuple(audio, index)
        index is 0 to 4, if a new audio identified and exceed people limit, index = -1
        '''
        self.voice_record = []
        self.now_processing = None
        self.output_record = None
        self.history = None

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
        '''
        input: binary data
        output: corresponding tensor
        '''
        waveform, _ = torchaudio.load(io.BytesIO(binary))
        return waveform

    def language_model(self):
        '''
        if the two consecutive sentences belong to the same user
        concat them and send it to the language model
        '''
        if self.history[1] == self.output_record[1] and self.output_record[1] == -1: 
            response = palm.generate_text(prompt = "concat following two sentence with some modify to make it more reasonable and output only one sentence without any other message: " + self.output_record[0] + " , " + self.history[0])
            result = response.result
            self.output_record = [result, self.output_record[1]]


    def transcript(self, binary, who):
        '''
        transcribe a binary data to text
        input: binary data & index of person
        output: None (store the record in self.output_record)
        '''
        binary = io.BytesIO(binary)
        data = sr.AudioFile(binary)

        with data as source:
            audio = self.r.record(source)
        try:
            s = self.r.recognize_google(audio)
            self.output_record = [s, who]
            print("Text: "+s)
        except Exception as e:
            print("Exception: Can't Recognize")
            del self.voice_record[-1]
            self.output_record = ["###", -1]

    def determine_identical(self, voice1, voice2):
        '''
        given voice converted to tensor, determine whether they're from the same person
        input: voice tensor * 2
        output: True/False
        '''
        _, prediction = self.verification_model.verify_batch(voice1, voice2, threshold=0.5)
        return prediction.item()

    def separate_user(self, data):
        '''
        identify voice index
        create a record if not in self.voice_record
        return the index of person
        input: binary data
        output: index of the person
        '''
        self.now_processing = self.bin_to_tensor(data)
        if len(self.voice_record) == 0:
            self.voice_record.append((self.now_processing, len(self.voice_record)+1))
            return 1
        for item in self.voice_record:
            voice = item[0]
            pred = self.determine_identical(self.now_processing, voice)
            if pred:
                return item[1]
        self.voice_record.append((self.now_processing, len(self.voice_record)+1))
        return len(self.voice_record)

    def to_json(self):
        '''
        input: None (use self.output_record)
        output: JSON file
        '''
        result_list = []
        if self.history != None:
            result_list = [{'text':self.history[0], 'name':'user'+str(self.history[1])}]
        
        result_list.append({'text':self.output_record[0], 'name':'user'+str(self.output_record[1])})
        result = json.dumps(result_list, indent=4)
        with open('output.json', 'w') as output:
            output.write(result)
        if self.history != None:
            self.output_record = [result_list[1]['text'], int(result_list[1]['name'][4:])]
        return result

    def process(self, data):
        '''
        The pipeline of voice processing.
        Called to process the binary data.
        input: binary data
        output: JSON file
        '''
        if self.output_record != None and self.output_record[0]!="###":
            self.history = self.output_record
        # data = open(data, "rb").read()

        who = self.separate_user(data)
        if who > self.maxPeople:
            self.output_record = ['out of record, reset maximum record size if wanted', -1]
            del self.voice_record[-1]
        else:
            self.transcript(data, who)
        if self.history != None:
            self.language_model()
        return self.to_json()


if __name__ == "__main__":
    agent = Voice_process_agent(need_load=False)
    agent.process(None)
