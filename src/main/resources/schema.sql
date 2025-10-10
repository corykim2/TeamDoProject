-- 1. User 테이블
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email_address VARCHAR(64) NOT NULL,
    password VARCHAR(64) NOT NULL,
    user_name VARCHAR(64) NOT NULL,
    organization VARCHAR(64),
    is_delete BOOLEAN NOT NULL
);

-- 2. Team 테이블
CREATE TABLE team (
    team_code INT PRIMARY KEY AUTO_INCREMENT,
    leader_id INT,
    FOREIGN KEY (leader_id) REFERENCES users(id)
);

-- 3. Project 테이블
CREATE TABLE project (
    p_no INT PRIMARY KEY AUTO_INCREMENT,
    p_name VARCHAR(64) NOT NULL,
    progress_rate DOUBLE NOT NULL,
    team_code INT,
    FOREIGN KEY (team_code) REFERENCES team(team_code)
);

-- 4. Todo 테이블
CREATE TABLE todo (
    p_no INT PRIMARY KEY,
    priority int NOT NULL,
    state VARCHAR(64) NOT NULL,
    deadline DATE NOT NULL,
    manager VARCHAR(64) NOT NULL,
    name VARCHAR(64) NOT NULL,
    FOREIGN KEY (p_no) REFERENCES project(p_no)
);

-- 5. TeamMembership 테이블
CREATE TABLE team_membership (
    team_code INT,
    user_id INT,
    PRIMARY KEY (team_code, user_id),
    FOREIGN KEY (team_code) REFERENCES team(team_code),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE invite_code(
    invite_id INT PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    state VARCHAR(64) NOT NULL,
    p_no INT,
    FOREIGN KEY (p_no) REFERENCES project(p_no)
);

CREATE TABLE session(
    s_id VARCHAR(64) PRIMARY KEY,
    u_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    FOREIGN KEY (u_id) REFERENCES users(id)
);