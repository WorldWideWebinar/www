"""
Author: srinivas.kumarr

Python client for interacting with a server via STOMP over websockets.
"""
import thread
import thread.cli
import websocket
import stomper
import json
import logging
# Since we are Using SockJS fallback on the server side we are directly subscribing to Websockets here.
# Else the url up-till notifications would have been sufficient.

# ws://localhost:5000/api/stomp/stt/382/uf1wtl2a/websocket
ws_uri = "wss://{}/api/stomp/stt"

class StompClient(object):
  """Class containing methods for the Client."""

  #Do note that in this case we use jwt_token for authentication hence we are 
  #passing the same in the headers, else we can pass encoded passwords etc. 
  def __init__(self, meeting_id, server_ip="i11a501.p.ssafy.io", destinations=[]):
    """
    Initializer for the class.

    Args:
      jwt_token(str): JWT token to authenticate.
      server_ip(str): Ip of the server.
      port_number(int): port number through which we want to make the
                        connection.
      destinations(list): List of topics which we want to subscribe to.

    """
    # self.headers = {"Authorization": "Bearer " + jwt_token}
    self.headers = {}
    # import random, string
    # server_id = random.randint(0,999)
    # server_id = f"{server_id:03}"
    # session_id = ''.join(random.choices(string.ascii_uppercase +
    #                          string.digits, k=8))
    # self.ws_uri = ws_uri.format(server_ip,server_id,session_id)
    self.meetingId = meeting_id
    self.msg = {}
    self.ws_uri = ws_uri.format(server_ip)
    self.destinations = destinations
  
  def on_open(self, ws):
    """
    Handler when a websocket connection is opened.

    Args:
      ws(Object): Websocket Object.

    """
    #Iniitial CONNECT required to initialize the server's client registries.
    # conn = stomper.connect(heartbeats=(10000,10000))
    ws.send("CONNECT\naccept-version:1.1,1.0\nheart-beat:10000,10000\n\n\u0000")
    # ws.send(conn)
    
    
    # Subscribing to all required desitnations. 
      

  def create_connection(self):
    """
    Method which starts of the long term websocket connection.
    """

    ws = websocket.WebSocketApp(self.ws_uri,
                                on_message=lambda ws, message: self.on_msg(ws,message),
                                on_error=lambda ws,err : self.on_error(ws,err),
                                on_close=lambda ws, close_satatus_code, close_msg : self.on_closed(ws,close_satatus_code,close_msg),
                                on_open=lambda ws : self.on_open(ws))
    
    self.ws = ws
    # Run until interruption to client or server terminates connection. 
    ws.run_forever()

  def on_msg(self, ws, msg):
    """
    Handler for receiving a message.

    Args:
      msg(str): Message received from stomp watches.

    """
    if( msg == "\n") :
      ws.send("\n")
      return
    frame = stomper.Frame()
    unpacked_msg = stomper.Frame.unpack(frame, msg)

  def on_error(self, ws, err):
    """
    Handler when an error is raised.

    Args:
      err(str): Error received.

    """
    logging.error(f"The Error is: {err}")
    ws.close()

  def on_closed(self, ws, close_status_code, close_msg):
    """
    Handler when a websocket is closed, ends the client thread.
    """
    print("The websocket connection is closed.")
    thread.Thread.kill
  def on_send(self, input):
    self.msg["meetingId"] = 3
    self.msg["content"] = input
    sending = json.dumps({
        "meetingId" : self.meetingId,
        "segments" : input
    })
    sending = stomper.send(f"/pub/meetingSTT.{self.meetingId}",sending,None,"application/json")
    logging.debug("sending message : " + sending)
    self.ws.send(sending)

  def close(self):
    self.ws.close()