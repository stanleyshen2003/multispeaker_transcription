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
import google.generativeai as palm


'''
!!!! write your code under here !!!!
'''
class Voice_process_agent():
    '''
    init:
        separate_model_name: the model you want to use
        need_load: whether you have a local file of model
    '''
    def __init__(self, verification_model_name = "spkrec-ecapa-voxceleb", need_load = True):
        self.maxPeople = 5
        self.verification_model = self.load_verify_model(verification_model_name, need_load)
        self.r = sr.Recognizer()
        palm.configure(api_key="AIzaSyDykyFNxABqKKM-is6WjyQYMgCY5JYFc6g")

        ''' 
        store all the audio files here
        format: list of tuple(audio, index)
        index is 0 to 4, if a new audio identified and exceed people limit, index = -1
        '''
        self.voice_record = []
        self.now_processing = None
        self.output_record = None
        self.voice_tmp = None
        self.text_tmp = None

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
        print(len(binary))
        waveform, _ = torchaudio.load(io.BytesIO(binary))
        return waveform

    def language_model(self):
        # only for testing
        # self.text_tmp = ['Hello my name', 0]
        # self.output_record = ['Bob i\'m happy', 1]
        # same = False
        #
        same = self.voice_tmp[1] == self.output_record[1]
        if same:
            print("in same")
            self.data = self.voice_tmp[0] + self.data
            self.transcript(self.data, self.output_record[1])
            # print(self.output_record)
            self.voice_tmp = [self.data, self.output_record[1]]
        else:
            response = palm.generate_text(prompt = "move part of second text to first to make first sound reasonable, the text is sequential and ouput only two result string split with #, here are inputs: " + self.text_tmp[0] + " , " + self.output_record[0])
            print(response.result)
            result = response.result.split("#")
            tmp = self.output_record[1]
            self.output_record = [result[0][3:], self.text_tmp[1]]
            self.text_tmp = [result[1][3:], tmp]
            self.voice_tmp = [self.data, tmp]


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
        # print(voice1.shape, voice2.shape)
        _, prediction = self.verification_model.verify_batch(voice1, voice2, threshold=0.3)
        # print(prediction.item(), score)
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
        # print(self.now_processing.shape)
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
        # response = palm.generate_text(prompt = 'please make this sentence more grammerly correct, add less changes as possible and return only the result sentence: ' + self.output_record[0])
        result_list = [{'text':self.output_record[0], 'name':'user'+str(self.output_record[1])}]
        print(result_list)
        result = json.dumps(result_list, indent=4)
        with open('output.json', 'w') as output:
            output.write(result)
        if self.text_tmp == None:
            self.text_tmp = self.output_record
        self.output_record = None
        return result

    def process(self, data):
        '''
        The pipeline of voice processing.
        Called to process the binary data.
        input: binary data
        output: JSON file
        '''
        if data == None:
            with open('./saved.bin', 'rb') as f:
                self.data = f.read()
        else: 
            self.data = data[44:]
        if self.voice_tmp == None:
            self.voice_tmp = [data, 1]
        # data = open(data, "rb").read()
        who = self.separate_user(data)
        if who > self.maxPeople:
            self.output_record['out of record', -1]
            del self.voice_record[-1]
        else:
            self.transcript(data, who)
        # print(who)

        # if self.text_tmp != None and self.output_record[1] != -1:
        #     self.language_model()
        if self.output_record != None:
            return self.to_json()


if __name__ == "__main__":
    agent = Voice_process_agent(need_load=False)
    agent.process(None)
    # print(agent.generated_text[5][9:])
    # print(agent.generated_text[6][9:])
    # agent.process('save_data/save.wav')
    # agent.process('../backup/sound/barren.wav')
    # agent.process('../backup/sound/grace2.wav')
    # agent.process('backup/sound/barren.wav')
    # print(len(agent.voice_record))