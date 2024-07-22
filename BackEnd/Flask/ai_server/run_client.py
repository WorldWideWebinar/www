from whisper_live.client import TranscriptionClient

client = TranscriptionClient(
  "localhost",
  8000,
  lang="ko",
  translate=False,
  model="large-v3",
  use_vad=False,
  save_output_recording=True,                         # Only used for microphone input, False by Default
  output_recording_filename="./output_recording.wav"  # Only used for microphone input
)

try : 
    #for mic input
    # client()

    # for file
    client("news.mp3")
except KeyboardInterrupt :
    exit()


