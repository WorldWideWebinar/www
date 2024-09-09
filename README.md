<a href="https://github.com/orgs/Py-Sun/repositories">
    <img src="https://github.com/WorldWideWebinar/www/blob/master/FrontEnd/front_pjt/src/assets/img/main_logo.png" align="right" height="90" />
</a>



# WWW
> 다국어 실시간 번역 웹 어플리케이션

<img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=flat&logo=Spring&logoColor=white"/> <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white"/> <img src="https://img.shields.io/badge/Vue.js-4FC08D?style=flat&logo=vue.js&logoColor=white"/>
<img src="https://img.shields.io/badge/AMAZONEC2-FF9900?style=flat&logo=amazonec2&logoColor=white"/>
<img src="https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white"/>
<img src="https://img.shields.io/badge/Jenkins-D24939?style=flat&logo=jenkins&logoColor=white"/>




---


## 💡 프로젝트 소개

#### 🛠 핵심 기능

* WebRTC(Openvidu)를 활용한 화상 회의 서비스

* 그래픽 서버에 탑재한 AI 모듈을 활용한 STT 자막/번역 서비스

* 각 팀별 메신저 기능 서비스

* AI를 활용한 요약 회의록 제공 서비스

#### 📢 기능 시연

![webrtc 시연](./WebRTC시연.jpg)
* 화면 공유, 각자의 음성/화상 공유 가능한 회의

![채팅 시연](./채팅기능.PNG)
* 팀 선택 후, 팀원간 대화 가능한 메신저

![회의록](./회의록.PNG)
* AI 기반으로 회의 전문을 요약해 제공하는 회의록


---


## 📌 아키텍처/ERD/사용 기술

### 아키텍처

![아키텍처](./아키텍처.PNG)

### ERD

![erd](https://github.com/user-attachments/assets/4f82acc1-f90d-4db2-aba2-7144fb0b7ae4)

### 사용 기술 스택

* FE: Vue3, node.js, HTML, CSS

* BE: Spring, Redis, MySQL, Stomp

* AI: FastAPI, Whisper

* WebRTC: RabbitMQ, Openvidu

* CI/CD: Nginx, Docker, Jenkins, EC2

---

## 👩‍💻 팀원 소개

* 이주영: 팀장, FastAPI 서버를 이용한 STT 자막/번역 처리 AI, BE/FE 전반 보조, CI/CD 와 인프라 전반

* 박준영: FE 리더, Axios 및 Storage 설계, Openvidu FE 컴포넌트, nginx이용 SSL인증서 발급

* 김수빈: BE 리더, RabbitMQ를 활용한 메시지 브로커 설계, DB 설계, Meeting CRUD, AI 요약 회의록 기능

* 이선재: FE, 메인 웹 디자인, Team 삭제/탈퇴 기능, 컴포넌트 템플릿 빌드

* 주연수: BE, Openvidu 서버 세팅/배포, User CRUD/Team CRUD, DB 설계, 팀 페이지 관리

* 권용수: FE, UCC 담당, 채팅 socket 연결, 채팅용 컴포넌트/소통

