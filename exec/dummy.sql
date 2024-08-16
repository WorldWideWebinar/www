INSERT INTO user (uid, name, email, password, language, profile_image)
VALUES
    ('yeonsoo', '연수', 'ys@naver.com', '1234', 'kor', 'https://i11a501.p.ssafy.io/api/images/default_profile.png'),
    ('sb', 'subin', 'sb@naver.com', '1234', 'eng', 'https://i11a501.p.ssafy.io/api/images/default_profile.png'),
    ('jy', 'Ju', 'teamleader@naver.com', '1234', 'eng', 'https://i11a501.p.ssafy.io/api/images/default_profile.png'),
    ('sj', 'seonjae', 'ssss@naver.com', '1234', 'fra', 'https://i11a501.p.ssafy.io/api/images/default_profile.png'),
    ('park', 'jun', 'pppp@naver.com', '1234', 'kor', 'https://i11a501.p.ssafy.io/api/images/default_profile.png'),
    ('ys', '용수', 'yyyy@naver.com', '1234', 'kor', 'https://i11a501.p.ssafy.io/api/images/default_profile.png');
('admin', '관리자', 'admin@naver.com', '1234', 'kor', 'https://i11a501.p.ssafy.io/api/images/default_profile.png');
('ssafy', '사용자', 'ssafy@naver.com', '1234', 'kor', 'https://i11a501.p.ssafy.io/api/images/default_profile.png');


INSERT INTO team (owner_id, name, emoji)
VALUES
    (2, 'Backend', '🚀'),
    (5, 'Frontend', '🌟'),
    (3, 'GloablCC', '💡');

INSERT INTO user_team (user_id, team_id, last_time, admission)
VALUES
    (1, 1, NOW(), TRUE),  -- 연수 -> Backend
    (2, 1, NOW(), TRUE),  -- subin -> Backend
    (3, 1, NOW(), TRUE);  -- Ju -> Backend

INSERT INTO user_team (user_id, team_id, last_time, admission)
VALUES
    (6, 2, NOW(), TRUE),  -- 용수 -> Frontend
    (5, 2, NOW(), TRUE),  -- jun -> Frontend
    (4, 2, NOW(), TRUE);  -- seonjae -> Frontend

INSERT INTO user_team (user_id, team_id, last_time, admission)
VALUES
    (1, 3, NOW(), FALSE),  -- 연수 -> GlobalCC
    (2, 3, NOW(), FALSE),  -- subin -> GlobalCC
    (3, 3, NOW(), FALSE),  -- Ju -> GlobalCC
    (4, 3, NOW(), FALSE),  -- seonjae -> GlobalCC
    (5, 3, NOW(), FALSE),  -- jun -> GlobalCC
    (6, 3, NOW(), FALSE);  -- 용수 -> GlobalCC


INSERT INTO meeting (team_id, name, start_at, end_at, content, session_id, detail)
VALUES
    (1, '백엔드 회의', '2024-08-16 09:00:00', '2024-08-14 16:00:00', '백엔드 팀 회의입니다.', NULL, 'Backend project discussion');
INSERT INTO meeting (team_id, name, start_at, end_at, content, session_id, detail)
VALUES
    (2, '프론트엔드 회의', '2024-08-16 09:00:00', '2024-08-16 16:00:00', '프론트엔드 팀 회의입니다.', NULL, 'Frontend project discussion');
INSERT INTO meeting (team_id, name, start_at, end_at, content, session_id, detail)
VALUES
    (3, '글로벌 CC 회의', '2024-08-16 09:00:00', '2024-08-16 16:00:00', '글로벌 CC 팀 회의입니다.', NULL, 'GlobalCC project discussion');


INSERT INTO user_meeting (user_id, meeting_id, attend)
VALUES
    (1, 1, TRUE),
    (2, 1, TRUE),
    (3, 1, TRUE);
INSERT INTO user_meeting (user_id, meeting_id, attend)
VALUES
    (6, 2, TRUE),
    (5, 2, TRUE),
    (4, 2, TRUE);
INSERT INTO user_meeting (user_id, meeting_id, attend)
VALUES
    (1, 3, TRUE),
    (2, 3, TRUE),
    (3, 3, TRUE),
    (4, 3, TRUE),
    (5, 3, TRUE),
    (6, 3, TRUE);