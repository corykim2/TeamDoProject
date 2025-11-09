-- 1. User 테이블
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    emailAddress VARCHAR(64) NOT NULL,
    password VARCHAR(64) NOT NULL,
    userName VARCHAR(64) NOT NULL,
    organization VARCHAR(64),
    isDelete BOOLEAN NOT NULL
);

-- 2. Team 테이블
CREATE TABLE team (
    teamCode INT PRIMARY KEY AUTO_INCREMENT,
    leaderId INT,
    FOREIGN KEY (leaderId) REFERENCES users(id)
);

-- 3. Project 테이블
CREATE TABLE project (
    pNo INT PRIMARY KEY AUTO_INCREMENT,
    pName VARCHAR(64) NOT NULL,
    progressRate DOUBLE NOT NULL,
    teamCode INT,
    FOREIGN KEY (teamCode) REFERENCES team(teamCode)
);

-- 4. Todo 테이블
CREATE TABLE todo (
    todoId INT PRIMARY KEY AUTO_INCREMENT, -- 고유 식별자 (기본 키)
    pNo INT NOT NULL,                      -- Project 외래 키
    priority INT NOT NULL,
    state VARCHAR(64) NOT NULL,
    deadline DATE NOT NULL,
    managerId INT NOT NULL,                -- User 외래 키 (관리자 ID)
    name VARCHAR(64) NOT NULL,              -- 할 일 이름 (NOT NULL 권고)
    FOREIGN KEY (pNo) REFERENCES project(pNo),
    FOREIGN KEY (managerId) REFERENCES users(id)
);

-- 5. TeamMembership 테이블
CREATE TABLE team_membership (
    teamCode INT,
    userId INT,
    PRIMARY KEY (teamCode, userId),
    FOREIGN KEY (teamCode) REFERENCES team(teamCode),
    FOREIGN KEY (userId) REFERENCES users(id)
);

CREATE TABLE invite_code(
    inviteId INT PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    state VARCHAR(64) NOT NULL,
    pNo INT,
    FOREIGN KEY (pNo) REFERENCES project(pNo)
);

CREATE TABLE session(
    sId VARCHAR(64) PRIMARY KEY,
    uId INT NOT NULL,
    createdAt TIMESTAMP NOT NULL,
    expiresAt TIMESTAMP NOT NULL,
    FOREIGN KEY (uId) REFERENCES users(id)
);