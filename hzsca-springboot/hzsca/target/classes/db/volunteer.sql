USE hzsca;

CREATE TABLE IF NOT EXISTS volunteer_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    applicant_type VARCHAR(10) NOT NULL,
    applicant_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NULL,
    age INT NULL,
    experience TEXT NULL,
    motivation TEXT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    apply_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    audit_time DATETIME NULL,
    auditor_id BIGINT NULL,
    audit_reason VARCHAR(255) NULL,
    KEY idx_vol_applicant (applicant_type, applicant_id),
    KEY idx_vol_status (status),
    KEY idx_vol_apply_time (apply_time)
);

CREATE TABLE IF NOT EXISTS activity_participation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id INT NOT NULL,
    participant_type VARCHAR(10) NOT NULL,
    participant_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    join_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    audit_time DATETIME NULL,
    auditor_id BIGINT NULL,
    audit_reason VARCHAR(255) NULL,
    UNIQUE KEY uk_activity_participant (activity_id, participant_type, participant_id),
    KEY idx_activity_id (activity_id)
);

ALTER TABLE wx_user ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'user';

ALTER TABLE activity_participation ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PENDING';
ALTER TABLE activity_participation ADD COLUMN audit_time DATETIME NULL;
ALTER TABLE activity_participation ADD COLUMN auditor_id BIGINT NULL;
ALTER TABLE activity_participation ADD COLUMN audit_reason VARCHAR(255) NULL;

ALTER TABLE adoption_request
    ADD COLUMN applicant_type VARCHAR(10) NOT NULL DEFAULT 'WX',
    ADD COLUMN applicant_id BIGINT NOT NULL DEFAULT 0;

ALTER TABLE adoption_request
    MODIFY wx_user_id BIGINT NULL;

UPDATE adoption_request
SET applicant_type = 'WX',
    applicant_id = wx_user_id
WHERE (applicant_id = 0 OR applicant_id IS NULL);

ALTER TABLE rescue_report
    ADD COLUMN reporter_type VARCHAR(10) NOT NULL DEFAULT 'WX',
    ADD COLUMN reporter_id BIGINT NOT NULL DEFAULT 0;

ALTER TABLE rescue_report
    MODIFY wx_user_id BIGINT NULL;

UPDATE rescue_report
SET reporter_type = 'WX',
    reporter_id = wx_user_id
WHERE (reporter_id = 0 OR reporter_id IS NULL);
