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
    def __init__(self, separate_model_name, need_load):
        self.maxPeople = 5
        self.model = self.loadmodel(separate_model_name, need_load)
        self.voice_record = []


    def loadmodel(self, model_name, load_need):
        """
        input:
            model_name(string): the location of your model.
            load_need(bool): whether you have to load from hugging face library
        output:
            model: a model that can call model.separate_file(path=your.wav)
        """
        url = 'speechbrian/' + model_name if load_need else 'pretrained_models/' + model_name
        model = separator.from_hparams(source=url, savdir='pretrained_models/'+model_name)
        return model
    



model = separator.from_hparams(source='pretrained_models/sepformer-wsj02mix', savedir='pretrained_models/sepformer-wsj02mix')

# for custom file, change path
est_sources = model.separate_file(path='test_mixture.wav') 


for i in range(np.array(est_sources.shape)[-1]):
    fileout = "source" + str(i) + ".wav"
    torchaudio.save(fileout, est_sources[:, :, i].detach().cpu(), 8000)