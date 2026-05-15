CREATE TABLE users (
    user_id   VARCHAR(32)  PRIMARY KEY,
    username  VARCHAR(100) NOT NULL
);

CREATE TABLE task_lists (
    list_id   BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id   VARCHAR(32)  NOT NULL,
    list_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    UNIQUE (user_id, list_name)
);

CREATE TABLE tasks (
    task_id     BIGINT                        AUTO_INCREMENT PRIMARY KEY,
    list_id     BIGINT                        NOT NULL,
    task_text   VARCHAR(512)                  NOT NULL,
    priority    ENUM('LOW','MEDIUM','HIGH')   NOT NULL DEFAULT 'MEDIUM',
    task_status ENUM('PENDING','FINISHED')    NOT NULL DEFAULT 'PENDING',
    due_date    DATE                          DEFAULT NULL,
    created_at  DATETIME                      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT tasks_ibfk_1 FOREIGN KEY (list_id) REFERENCES task_lists (list_id) ON DELETE CASCADE
);