from fastapi import FastAPI,WebSocket,WebSocketDisconnect
# from fastapi.logger import logger
import logging
logging.basicConfig(level=logging.DEBUG)

import websockets
import argparse

import websockets.connection
import whisper_online
import soundfile
import librosa
import numpy as np
import io

import uuid
import json
import translator
import threading
import asyncio
app = FastAPI()

parser = argparse.ArgumentParser()
SAMPLING_RATE = 16000
whisper_online.add_shared_args(parser)
parser.add_argument('--tgt_lan', '--target_language', type=str, default='en')

@app.get("/")
async def root():
    return {"message": "Hello World"}

# @app.websocket("/stream/{src_lang}/{tgt_lang}")
# async def stream_data(tgt_lang: str, websocket: WebSocket,src_lang = 'auto'):
#     await websocket.accept()
#     # make model prepared when websocket connected
#     asr, online = whisper_online.new_asr_factory(tgt_lang=tgt_lang,src_lan=src_lang)
#     logging.info("made whisper online model")
#     # TODO : make warmup file
#     try:
#         while True:
#             # TODO : make continuous reciving data
#             out = []
#             while sum(len(x) for x in out) < SAMPLING_RATE:
#                 raw_bytes = await websocket.receive()
#                 if not raw_bytes:
#                     break
#                 sf = soundfile.SoundFile(io.BytesIO(raw_bytes), channels=1,endian="LITTLE",samplerate=SAMPLING_RATE, subtype="PCM_16",format="RAW")
#                 audio, _ = librosa.load(sf,sr=SAMPLING_RATE,dtype=np.float32)
#                 out.append(audio)
#             if not out:
#                 return None
#             out = np.concatenate(out)
#             online.insert_audio_chunk(out)
#             out = online.process_iter()
#             if out[0] is None:
#                 continue
#             else:
#                 websocket.send_text(out[2])
#     except:
#         await websocket.close()

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

@app.websocket("/stream/{src_lang}/{tgt_lang}")
async def stream_data(src_lang : str, tgt_lang, websocket:WebSocket):
    await websocket.accept()
    stt_server_uri = "ws://localhost:9090"
    
    server_socket = await websockets.connect(stt_server_uri)
    client_details = await websocket.receive_json()
    tr = threading.Thread(target=connect_from_ai_to_client,args=[server_socket,websocket])
    tr.start()
    # print(client_details)
    # await server_socket.send(json.dumps(
    #     {
    #             "uid": str(uuid.uuid4()),
    #             "language": src_lang,
    #             "task": "transcribe",
    #             "model": "large-v3",
    #             "use_vad": True
    #         }
    # ))
    asyncio.create_task(connect_from_ai_to_client(server_socket,websocket))
    await server_socket.send(json.dumps(client_details))
    
    try:
        while True:
            audio_data = await websocket.receive_bytes()
            # logging.debug(f"audio data is : {audio_data}")
            # logging.debug(f"audio data type is : {type(audio_data)}")
            await server_socket.send(audio_data)

    except Exception as e:
        print(e)
        import traceback
        traceback.print_exception(e)
    finally :
        await websocket.close()
        await server_socket.close()


async def connect_from_ai_to_client(server_socket : WebSocket, websocket : WebSocket):
    try:
        while True:
            recv = await server_socket.recv()
            logging.debug(f"get message form ai server : {recv}")
            message = json.loads(recv)
            await websocket.send_json(message)
    except Exception as e:
        logging.error(f"Error on connection_from_ai_to_client : {e}")

          
     

        

