import asyncio
import websockets
import soundfile as sf
import numpy as np
from io import BytesIO


SAMPLING_RATE = 16000  # 예시로 사용

async def stream_audio(file_path, uri):
    async with websockets.connect(uri) as websocket:
        with open(file_path,'rb') as file:
            while True:
                data = file.read(4096)
                if not data:
                    break
                data = np.frombuffer(data).tobytes()
                await websocket.send(data)
                response = await websocket.recv()
                print(f"Received: {response}")

async def stream_text(uri):
    async with websockets.connect(uri) as websocket:
            while True:
                data = input("input : ")
                await websocket.send(data)
                response = await websocket.recv()
                print(f"Received: {response}")



if __name__ == "__main__":
    file_path = "resource/news.mp3"  # 여기에 오디오 파일 경로를 지정하세요
    uri = "ws://localhost:8000/stream/ko/en"
    asyncio.run(stream_audio(file_path, uri))

    # uri = "ws://localhost:8000/translate/1/kor_Hang/eng_Latn"  
    # asyncio.run(stream_text(uri))


