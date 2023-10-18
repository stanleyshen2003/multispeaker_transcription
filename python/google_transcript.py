import speech_recognition as sr
r = sr.Recognizer()

hellow=sr.AudioFile('src_sound/test2.wav')
with hellow as source:
    audio = r.record(source)
try:
    s = r.recognize_google(audio)
    print("Text: "+s)
except Exception as e:
    print("Exception: "+str(e))