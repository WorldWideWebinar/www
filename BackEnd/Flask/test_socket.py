from fastapi.testclient import TestClient
from fastapi.websockets import WebSocket
from main import app
import threading

def printRecv(ws):
    data = ws.recive_text()
    print(data)


def test_client():
    client = TestClient(app)    
    with client.websocket_connect("/stream/ko/ko") as ws:
        threading.Thread(target=printRecv, args=(ws)).start()
        with open("resource\\news.mp3",'rb') as f:
            while True:
                data = f.read(4096)
                if not data : break



        
