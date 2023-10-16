import torchaudio
from speechbrain.pretrained import SpeakerRecognition

# Your audio data variable (assuming it's already loaded)
audio_data1, _ = torchaudio.load("source0.wav", num_frames=16000)
audio_data2, _ = torchaudio.load("source1.wav", num_frames=16000)

# Initialize the SpeakerRecognition model
verification = SpeakerRecognition.from_hparams(
    source="speechbrain/spkrec-ecapa-voxceleb",
    savedir="pretrained_models/spkrec-ecapa-voxceleb"
)
audio_data1.unsqueeze(0)
audio_data2.unsqueeze(0)
print("Tensor Shape:", audio_data1.shape)
print("Data Type:", audio_data1.dtype)
print("Device:", audio_data1.device)
print("Requires Gradient:", audio_data1.requires_grad)
print("Number of Elements:", audio_data1.numel())
print("Data Values:")
print(audio_data1)
# Verify if the two audio samples come from the same person
#score, prediction = verification.verify_files("source0.wav", "source1.wav")
score, prediction = verification.verify_batch(audio_data1, audio_data2, threshold=0.5)

# Print the result
print("Prediction:", prediction)
print("Score:", score)
