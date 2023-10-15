from speechbrain.pretrained import SepformerSeparation as separator
import torchaudio
import os
import numpy as np
from hyperpyyaml import load_hyperpyyaml

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
!!!! write your code here !!!!
'''
class Voice_process_agent():
    '''
    init:
        separate_model_name: the model you want to use
        need_load: whether you have a local file of model
    '''
    def __init__(self, separate_model_name = "sepformer-wsj02mix", need_load = True):
        self.maxPeople = 5
        self.model = self.loadmodel(separate_model_name, need_load)
        ''' 
        store all the audio files here
        format: list of tuple(audio, index)
        index is 0 to 4, if a new audio identified and exceed people limit, index = -1
        '''
        self.voice_record = []
        self.now_processing = []


    def loadmodel(self, model_name, need_load):
        """
        input:
            model_name(string): the location of your model.
            need_load(bool): whether you have to load from hugging face library
        output:
            model: a model that can call model.separate_file(path=your.wav)
        """
        url = 'speechbrain/' + model_name if need_load else 'pretrained_models/' + model_name
        model = separator.from_hparams(source=url, savedir='pretrained_models/'+model_name)
        return model
    
    def determine_identical(self,voice1 , voice2):
        pass
    
    def separate_files(self, file_name, save_separate):
        result = self.model.separate_file(path=file_name)
        
        if save_separate:
            for i in range(np.array(result.shape)[-1]):
                fileout = "source" + str(i) + ".wav"
                torchaudio.save(fileout, result[:, :, i].detach().cpu(), 8000)
            return

        for i in range(np.array(result.shape)[-1]):        
            result[:,:,i] = result[:,:,i].detach().cpu()
            found = False
            for j in len(self.voice_record):
                same = self.determine_identical(result[:,:,i], self.voice_record[j][0])
                if same:
                    self.now_processing.append((result[:,:,i],j))
                    found = True
                    break
            if not found and len(self.voice_record) < self.maxPeople:
                self.now_processing.append((result[:,:,i],len(self.voice_record)))
                self.voice_record.append((result[:,:,i],len(self.voice_record)))
            elif not found:
                self.now_processing.append((result[:,:,i],-1))
                
                
                



# model = separator.from_hparams(source='pretrained_models/sepformer-wsj02mix', savedir='pretrained_models/sepformer-wsj02mix')

# # for custom file, change path
# est_sources = model.separate_file(path='test_mixture.wav') 


# for i in range(np.array(est_sources.shape)[-1]):
#     fileout = "source" + str(i) + ".wav"
#     torchaudio.save(fileout, est_sources[:, :, i].detach().cpu(), 8000)

if __name__ == "__main__":
    agent = Voice_process_agent(need_load=False)
    agent.separate_files("test_mixture.wav", save_separate=True)
