-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS globalcc;

-- 데이터베이스 선택
USE globalcc;

-- 사용자 테이블
CREATE TABLE IF NOT EXISTS user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(320) NOT NULL, -- 이메일 주소는 최대 320자를 가질 수 있습니다.
    password VARCHAR(255) NOT NULL,
    language VARCHAR(255) DEFAULT 'eng', -- 기본 값으로 저장
    profile_image VARCHAR(2048), -- 프로필 이미지 URL 저장
    last_team_id INT,
    last_meeting_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 팀 테이블
CREATE TABLE IF NOT EXISTS team (
    team_id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT NOT NULL, -- 방장
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES user(user_id)
);

-- 사용자-팀 관계 테이블
CREATE TABLE IF NOT EXISTS user_team (
    user_group_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    team_id INT NOT NULL,
    last_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 마지막으로 접근한 시간
    admission BOOLEAN DEFAULT FALSE, -- 입장 여부
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (team_id) REFERENCES team(team_id)
);

-- 미팅 테이블
CREATE TABLE IF NOT EXISTS meeting (
    meeting_id INT AUTO_INCREMENT PRIMARY KEY,
    team_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    start_at TIMESTAMP,
    end_at TIMESTAMP,
    content TEXT, -- 긴 내용을 저장할 수 있도록 TEXT 타입 사용
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (team_id) REFERENCES team(team_id)
);

-- 사용자-미팅 관계 테이블
CREATE TABLE IF NOT EXISTS user_meeting (
    user_meeting_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    meeting_id INT NOT NULL,
    attend BOOLEAN DEFAULT FALSE, -- 입장 여부
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (meeting_id) REFERENCES meeting(meeting_id)
);

-- 미팅 파일 테이블
CREATE TABLE IF NOT EXISTS meeting_file (
    meeting_file_id INT AUTO_INCREMENT PRIMARY KEY,
    meeting_id INT NOT NULL,
    file_url VARCHAR(2048) NOT NULL, -- 파일 URL 저장
    upload_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (meeting_id) REFERENCES meeting(meeting_id)
);

-- 미팅 STT 테이블
CREATE TABLE IF NOT EXISTS meeting_stt (
    meeting_stt_id INT AUTO_INCREMENT PRIMARY KEY,
    meeting_id INT NOT NULL,
    content TEXT NOT NULL, -- 긴 내용을 저장할 수 있도록 TEXT 타입 사용
    FOREIGN KEY (meeting_id) REFERENCES meeting(meeting_id)
);

-- 채팅 테이블
CREATE TABLE IF NOT EXISTS chat (
    chat_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    team_id INT NOT NULL,
    content_type ENUM('text', 'image', 'file'),
    content TEXT NOT NULL, -- URL 또는 긴 텍스트를 저장할 수 있도록 TEXT 타입 사용
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES user(user_id),
    FOREIGN KEY (team_id) REFERENCES team(team_id)
);

-- 테이블 수정

-- 팀 별로 이미지 정보 저장
ALTER TABLE team ADD COLUMN emoji TEXT;
ALTER TABLE team MODIFY COLUMN emoji TEXT NOT NULL;
-- 암호화 위해 사이즈 변경
ALTER TABLE user MODIFY COLUMN password VARCHAR(512) NOT NULL;
-- meeting 테이블에 session_id 추가
ALTER TABLE meeting ADD COLUMN session_id VARCHAR(255);


-- user 테이블
ALTER TABLE user 
DROP COLUMN last_team_id,
DROP COLUMN last_meeting_id;

CREATE TABLE IF NOT EXISTS user_detail (
    user_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    last_team_id INT,
    last_meeting_id INT,
    total_meeting_time INT,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

ALTER TABLE user CHANGE id uid VARCHAR(255) NOT NULL;

-- 팀 삭제시 user_team 정보도 함께 삭제되도록 키 설정 변경

-- 기존 외래 키 제약 조건 삭제
ALTER TABLE user_team DROP FOREIGN KEY user_team_ibfk_2;

-- 외래 키 제약 조건에 `ON DELETE CASCADE` 추가
ALTER TABLE user_team
    ADD CONSTRAINT user_team_ibfk_2
        FOREIGN KEY (team_id) REFERENCES team (team_id)
            ON DELETE CASCADE;
