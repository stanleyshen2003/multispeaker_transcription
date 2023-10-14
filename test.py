from speechbrain.pretrained import SepformerSeparation as separator
import torchaudio
import os
import numpy as np
from hyperpyyaml import load_hyperpyyaml
class myseparator(separator):
    def from_hparams(source):
        with open(source) as fin:
            hparams = load_hyperpyyaml(fin, {})
        return cls(hparams["modules"],hparams,NULL)

# print(os.path.abspath("pretrained_models/sepformer-wsj02mix/hyperparams.yaml"))
# model = myseparator.from_hparams(source='pretrained_models/sepformer-wsj02mix/hyperparams.yaml')
model = separator.from_hparams(source='pretrained_models/sepformer-wsj02mix', savedir='pretrained_models/sepformer-wsj02mix')

# for custom file, change path
est_sources = model.separate_file(path='test_mixture.wav') 


for i in range(np.array(est_sources.shape)[-1]):
    fileout = "source" + str(i) + ".wav"
    torchaudio.save(fileout, est_sources[:, :, i].detach().cpu(), 8000)