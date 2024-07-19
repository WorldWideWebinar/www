from fastapi import FastAPI,WebSocket,WebSocketDisconnect

import argparse
import whisper_online
import soundfile
import librosa
import numpy as np
import io

import translator

app = FastAPI()

parser = argparse.ArgumentParser()
SAMPLING_RATE = 16000
whisper_online.add_shared_args(parser)
parser.add_argument('--tgt_lan', '--target_language', type=str, default='en')

@app.get("/")
async def root():
    return {"message": "Hello World"}

@app.websocket("/stream/{src_lang}/{tgt_lang}")
async def stream_data(tgt_lang: str, websocket: WebSocket,src_lang = 'auto'):
    await websocket.accept()
    # make model prepared when websocket connected
    asr, online = whisper_online.new_asr_factory(tgt_lang=tgt_lang,src_lan=src_lang)
    print("made whisper online model")
    # TODO : make warmup file
    try:
        while True:
            # TODO : make continuous reciving data
            out = []
            while sum(len(x) for x in out) < SAMPLING_RATE:
                raw_bytes = await websocket.receive()
                if not raw_bytes:
                    break
                sf = soundfile.SoundFile(io.BytesIO(raw_bytes), channels=1,endian="LITTLE",samplerate=SAMPLING_RATE, subtype="PCM_16",format="RAW")
                audio, _ = librosa.load(sf,sr=SAMPLING_RATE,dtype=np.float32)
                out.append(audio)
            if not out:
                return None
            out = np.concatenate(out)
            online.insert_audio_chunk(out)
            out = online.process_iter()
            if out[0] is None:
                continue
            else:
                websocket.send_text(out[2])
    except:
        await websocket.close()

@app.websocket("/translate/{group_id}/{src_lang}/{tgt_lang}")
async def translate_data(group_id : int, tgt_lang: str, websocket: WebSocket,src_lang = 'auto'):
    await websocket.accept()
    curTr = translator.translator_factory(tgt_lang,src_lang)
    try:
        while True :
            text = await websocket.receive_text()
            result = curTr.translate(text)
            print(result)
            await websocket.send_text(result)
    except:
        await websocket.close()
