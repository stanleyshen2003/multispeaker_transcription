## Discription
Project done in [MCHackthon 2023](https://2023.meichuhackathon.org/). \
Rank: 🥉 in the Google group\
Topic: [Accessibility](https://github.com/stanleyshen2003/hackathon/blob/main/Google_2023_topic.pdf)

## Motivation
How can deaf people participate in a converation? Recent apps [live transcribe](https://play.google.com/store/apps/details?id=com.google.audio.hearing.visualization.accessibility.scribe&hl=en_US) perform well on translating single person transcription, and enabled people of hearing loss to know the world better. However, it is not designed to differentiate between different speakers, making it hard to transcribe a conversation between multiple users (as shown in the .pdf). Thus, we build an application that can differentiate between different speaker's voice and show the transcribed text in a readible way.

## Feature
- identify the user who is speaking
- transcript the audio and display in the app
- support Chinese / English translation
- running python program as server

## How to use
### server
``` bash
git clone https://github.com/stanleyshen2003/hackathon.git
cd hackathon/server
pip install -r requirements.txt
python3 server.py
```

We recommend you to run the server by 
- macOS 
- ubuntu
- VM
- docker
We use [PALM](https://github.com/google/generative-ai-python) to make the segmently transcibed audio sound more reasonable. You can enter your api key when starting the server to enable the service(optional).


### Android app
Download the [chat.apk](https://github.com/stanleyshen2003/multispeaker_transcription/blob/main/chat.apk) and run it on your Android phone. Connect to the server with the setting page.\
To use the translate function, you have to open your android studio and place your API key in "data/Translator.java".

## Demo
See [demo](https://github.com/stanleyshen2003/multispeaker_transcription/blob/main/Multi%20Speaker%20Transcription.pdf)


## Some common bugs
- APP - SDK location not found: set your SDK file path to environment variable $ANDROID_HOME.
- APP - delete by itself: mainly because you turned off the server before you stop recording, please stop the recording first.
- APP - gradle issues: you can find the gradle version in our project, we tested it on Android API 29,30,33 and it works.
- Server - sox not found: go install sox.
- Server - WinError [Permission denied]: this error occurred because the speechbrain library tried to move model and load model in its directory. This is the reason why we did not use windows to run the server.
- Network not connected: we can not help with that, please set the correct ip and port.

## Future work
- Improve performance: In this project, we verify the speacker every 3 seconds due to computation limitation. If we had stronger CPU/GPU, we can cut it into smaller sizes, this will likely improve the performance.
- Improve models: Since we only have a few days for this competition, we can only this use pretrained model. When we test the models, it seems that the transctiption perform poorly on Taiwan accent (cause they are trained on normal American accents...), and the verification model perform better on women (maybe because of the dataset).  
- Other ideas: Voice verification is a hard task, but it is not when we have image input. In our opinion, voice is hard to differentiate even if the agent is human. Maybe we can have a camera on and record the tick. 

