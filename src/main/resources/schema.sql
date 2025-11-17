
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
    todoId INT AUTO_INCREMENT PRIMARY KEY,
    pNo INT NOT NULL,
    createdTime DATETIME DEFAULT CURRENT_TIMESTAMP,
    priority INT NOT NULL,
    state VARCHAR(64) NOT NULL,
    deadline DATE NOT NULL,
    managerId INT NOT NULL,
    name VARCHAR(64) NOT NULL,

    -- 외래 키 설정
    CONSTRAINT fk_todo_project FOREIGN KEY (pNo)
        REFERENCES project(pNo)
        ON DELETE CASCADE,

    CONSTRAINT fk_todo_manager FOREIGN KEY (managerId)
        REFERENCES users(userId)
        ON DELETE CASCADE
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
*/