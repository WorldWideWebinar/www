import { defineStore } from 'pinia'
import axiosInstance from '@/axios'

// export const useMessageStore = defineStore('message', {
//   state: () => ({
//     messages: [],
//   }),
//   actions: {
//     addMessage(message) {
//       this.messages.push(message);
//     },
//     setMessages(newMessages) {
//       this.messages = newMessages;
//     },
//     clearMessages() {
//       this.messages = [];
//     }
//   },
//   getters: {
//     getMessagesByTeamId: (state) => (teamId) => {
//       return state.messages.filter(message => message.team_id === teamId);
//     }
//   }

// });
export const useSTTStore = defineStore('stt', {
  state: () => ({
    transcript: [],
    translated: [],
    last_received_segment: null,
    last_received_translated: null,
    target_language: null,
    ws: WebSocket
  }),
  actions: {
    last_segment : null,

    openConnection(token, meeting_id){
      this.ws = new WebSocket("https://i11a501.p.ssafy.io/api/stomp/stt");
      const stompClient = Stomp.over(this.ws)

      stompClient.connect(
        {
          Authorization: `Bearer ${token}`
        },
        ()=> {
          stompClient.subscribe(
            `/exchange/meetingSTT.exchange/meetingSTT.key${meeting_id}`,
            (message) => {
              console.log('Received : ', message)
              const messageBody = JSON.parse(message.body);
              this.processSegments(messageBody["segments"]);
            }
          );
        },
        (error) => {
          console.error("Connection error: " + error);
          this.ws.close()
        }
      );
    },
    processSegments(segments) {
        for (let i = 0; i < segments.length; i++) {
          if(i === segments.length - 1) {
            this.last_segment = segments[i]
          } else {
            if(this.transcript.length === 0 ||
              (segments[i].start >= this.transcript[this.transcript.length - 1].end)){
              this.transcript.push({
                start : segments[i].start,
                end : segments[i].end,
                text : segments[i].text,
              })
              this.translateText(segments[i].text, this.transcript.slice(0,-1).map(seg => seg.text).join('\n'))
                .then((text) => this.translated.push(text));
            }
          }
        }
        if(this.last_received_segment !== this.last_segment){
          this.last_received_segment = this.last_segment
          this.translateText(this.last_segment.text,this.transcript.slice(0,-1).map(seg => seg.text).join('\n'))
            .then(text => this.last_received_translated = text)
        }
      },
    closeConnection(){
      if(this.ws.readyState === WebSocket.OPEN){
        this.ws.close();
      }
    },
    async translateText(text , context) {
      const queryParams = {
        CountryCode: this.target_language,
        SourceText: text,
        Context: context
      };
      const response =  await axiosInstance.get(`api/translation`,{
        params: queryParams
      })
      return response.data.result.text;
    }
  },

  })