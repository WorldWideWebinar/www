from fastapi import FastAPI,WebSocket,WebSocketDisconnect
# from fastapi.logger import logger
import logging
logging.basicConfig(level=logging.INFO)

import websockets
import argparse

import websockets.connection
import deepl

import json
import threading
import asyncio
import time

from stomp_test import StompClient

app = FastAPI()

SAMPLING_RATE = 16000

@app.get("/")
async def root():
    return {"message": "Hello World"}

@app.websocket("/stream")
async def stream_data(websocket:WebSocket):
    await websocket.accept()
    stt_server_uri = "ws://localhost:9090"
    
    server_socket = await websockets.connect(stt_server_uri)
    client_details = await websocket.receive_json()
    
    meetingId = client_details["meetingId"]
    client_details["uid"] = meetingId
    client_details["language"] = None
    client_details["tgt_lang"] = "EN"
    client_details["model"] = "large-v3"
    client_details["task"] = "transcribe"
    client_details["use_vad"] = False
    clientSTT = StompClient(meeting_id = meetingId)
    # asyncio.create_task(clientSTT.create_connection())
    tr = threading.Thread(target=clientSTT.create_connection)
    tr.daemon = True
    tr.start()
    
    # src_lang = client_details["language"] = client_details["language"].lower()
    src_lang = client_details["language"]
    tgt_lang = client_details["tgt_lang"]
    asyncio.create_task(connect_from_ai_to_client(server_socket,websocket, src_lang, tgt_lang, clientSTT))
    await server_socket.send(json.dumps(client_details))
    
    try:
        while True:
            audio_data = await websocket.receive_bytes()
            logging.debug(f"audio data type is : {type(audio_data)}")
            await server_socket.send(audio_data)

    except Exception as e:
        import traceback
        traceback.print_exception(e)
    finally :
        await server_socket.close()
        await websocket.close()
        tr.join()


async def connect_from_ai_to_client(server_socket : WebSocket, websocket : WebSocket, src_lang : str , tgt_lang : str, client):
    transcript = []
    last_segment = None
    last_received_segment = None
    startTime = time.time()
    logging.info(f"startTime is : {startTime}")
    try:
        while True:
            recv = await server_socket.recv()
            message = json.loads(recv)
            logging.debug(f"get message form ai server : {message}")
            transcribed = []
            if "segments" in message.keys():
                segments = message["segments"]
                text = []
                for i, seg in enumerate(segments):
                    logging.info(f"start : from {seg['start']}" )
                    seg['start'] = "{:.3f}".format(float(seg['start']) + startTime)
                    logging.info(f"start : to {seg['start']}" )
                    logging.info(f"end : from {seg['end']}" )
                    seg['end'] = "{:.3f}".format(float(seg['end']) + startTime)
                    logging.info(f"end : to {seg['end']}" )
                client.on_send(segments[-3:])
            await websocket.send_json(message)
    except Exception as e:
        await server_socket.close()
        await client.close()
        logging.error(f"Error on connection_from_ai_to_client : {e}")

          
     

        

