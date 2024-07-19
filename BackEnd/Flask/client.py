import json
import logging
import time

def handle_status_messages(message_data):
        """Handles server status messages."""
        status = message_data["status"]
        if status == "WAIT":
            logging.debug(f"[INFO]: Server is full. Estimated wait time {round(message_data['message'])} minutes.")

        elif status == "ERROR":
            logging.error(f"Message from Server: {message_data['message']}")

        elif status == "WARNING":
            logging.warning(f"Message from Server: {message_data['message']}")



def on_message(ws, message):
    """
    Callback function called when a message is received from the server.

    It updates various attributes of the client based on the received message, including
    recording status, language detection, and server messages. If a disconnect message
    is received, it sets the recording status to False.

    Args:
        ws (websocket.WebSocketApp): The WebSocket client instance.
        message (str): The received message from the server.

    """
    message = json.loads(message)

    # if "status" in message.keys():
    #     self.handle_status_messages(message)
    #     return

    if "message" in message.keys() and message["message"] == "DISCONNECT":
        logging.info("Server disconnected due to overtime.")


    if "message" in message.keys() and message["message"] == "SERVER_READY":
        # self.last_response_received = time.time()
        # self.recording = True
        # self.server_backend = message["backend"]
        server_backend = message["backend"]
        logging.info(f"Server Running with backend {server_backend}")
        ws.send(json.dumps({
            "message" : "made backend model"
        }))
        return 

    if "language" in message.keys():
        language = message.get("language")
        lang_prob = message.get("language_prob")
        logging.info(
            f"Server detected language {language} with probability {lang_prob}"
        )
        return

    if "segments" in message.keys():
        segments = message["segments"]
        """Processes transcript segments."""
        text = []
        for i, seg in enumerate(segments):
            if not text or text[-1] != seg["text"]:
                text.append(seg["text"])
                if i == len(segments) - 1:
                    self.last_segment = seg
                elif (self.server_backend == "faster_whisper" and
                        (not self.transcript or
                        float(seg['start']) >= float(self.transcript[-1]['end']))):
                    self.transcript.append(seg)
        # update last received segment and last valid response time
        if self.last_received_segment is None or self.last_received_segment != segments[-1]["text"]:
            self.last_response_received = time.time()
            self.last_received_segment = segments[-1]["text"]

        # Truncate to last 3 entries for brevity.
        text = text[-3:]
        # utils.clear_screen()
        ws.send(
            json.dumps({
                "type"    : "transcription",
                "message" : text
            })
        )
        return 
