USE jnu-sf;

CREATE TABLE USER (
    user_id BIGINT(20) NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id)
) ENGINE=InnoDB;

CREATE TABLE PROJECT (
    project_id BIGINT(20) NOT NULL AUTO_INCREMENT,
    user_id BIGINT(20) NOT NULL,
    project_name VARCHAR(255) NOT NULL,
    sprint_count TINYINT NULL,
    manager VARCHAR(255) NOT NULL,
    PRIMARY KEY (project_id),
    FOREIGN KEY (user_id) REFERENCES USER (user_id)
) ENGINE=InnoDB;

CREATE TABLE SUMMARY (
    summary_id BIGINT(20) NOT NULL AUTO_INCREMENT,
    project_id BIGINT(20) NOT NULL,
    summary_content VARCHAR(255) NOT NULL,
    last_updated DATETIME(6) NOT NULL,
    PRIMARY KEY (summary_id),
    FOREIGN KEY (project_id) REFERENCES PROJECT (project_id)
) ENGINE=InnoDB;

CREATE TABLE SPRINT (
    sprint_id BIGINT(20) NOT NULL AUTO_INCREMENT,
    project_id BIGINT(20) NOT NULL,
    sprint_name VARCHAR(255) NOT NULL,
    start_date DATETIME(6) NOT NULL,
    end_date DATETIME(6) NOT NULL,
    PRIMARY KEY (sprint_id),
    FOREIGN KEY (project_id) REFERENCES PROJECT (project_id)
) ENGINE=InnoDB;

CREATE TABLE CARD (
    card_id BIGINT(20) NOT NULL AUTO_INCREMENT,
    sprint_id BIGINT(20) NOT NULL,
    card_content VARCHAR(255) NOT NULL,
    card_participants VARCHAR(255) NOT NULL,
    card_status VARCHAR(255) NOT NULL,
    PRIMARY KEY (card_id),
    FOREIGN KEY (sprint_id) REFERENCES SPRINT (sprint_id)
) ENGINE=InnoDB;

CREATE TABLE RETROSPECT (
    retro_id BIGINT(20) NOT NULL AUTO_INCREMENT,
    sprint_id BIGINT(20) NOT NULL,
    summary VARCHAR(255) NOT NULL,
    PRIMARY KEY (retro_id),
    FOREIGN KEY (sprint_id) REFERENCES SPRINT (sprint_id)
) ENGINE=InnoDB;

CREATE TABLE KPT (
    retro_id BIGINT(20) NOT NULL,
    keep VARCHAR(255) NULL,
    problem VARCHAR(255) NULL,
    try VARCHAR(255) NULL,
    PRIMARY KEY (retro_id),
    FOREIGN KEY (retro_id) REFERENCES RETROSPECT (retro_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE FOUR_LS (
    retro_id BIGINT(20) NOT NULL,
    liked VARCHAR(255) NULL,
    learned VARCHAR(255) NULL,
    lacked VARCHAR(255) NULL,
    logged_for VARCHAR(255) NULL,
    PRIMARY KEY (retro_id),
    FOREIGN KEY (retro_id) REFERENCES RETROSPECT (retro_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE CSS (
    retro_id BIGINT(20) NOT NULL,
    continue VARCHAR(255) NULL,
    stop VARCHAR(255) NULL,
    start VARCHAR(255) NULL,
    PRIMARY KEY (retro_id),
    FOREIGN KEY (retro_id) REFERENCES RETROSPECT (retro_id) ON DELETE CASCADE
) ENGINE=InnoDB;
