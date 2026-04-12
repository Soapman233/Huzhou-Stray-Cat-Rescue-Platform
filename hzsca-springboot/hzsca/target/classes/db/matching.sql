USE hzsca;

CREATE TABLE IF NOT EXISTS user_adoption_profile (
    user_id BIGINT PRIMARY KEY,
    residence_city VARCHAR(50) NOT NULL,
    housing_type VARCHAR(20) NOT NULL,
    home_size_level TINYINT NOT NULL DEFAULT 2,
    has_children BOOLEAN NOT NULL DEFAULT FALSE,
    has_other_pets BOOLEAN NOT NULL DEFAULT FALSE,
    allergy BOOLEAN NOT NULL DEFAULT FALSE,
    max_shedding_level TINYINT NOT NULL DEFAULT 5,
    daily_companionship_hours TINYINT NOT NULL DEFAULT 0,
    work_schedule VARCHAR(20) NOT NULL DEFAULT 'DAY',
    budget_level TINYINT NOT NULL DEFAULT 2,
    experience_level TINYINT NOT NULL DEFAULT 0,
    personality_preference VARCHAR(20) NOT NULL DEFAULT 'ANY',
    accept_special_care BOOLEAN NOT NULL DEFAULT FALSE,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NULL DEFAULT NULL,
    CONSTRAINT fk_user_adoption_profile_user FOREIGN KEY (user_id) REFERENCES wx_user(id)
);

CREATE TABLE IF NOT EXISTS cat_traits (
    cat_id INT PRIMARY KEY,
    personality VARCHAR(20) NOT NULL DEFAULT 'ANY',
    activity_level TINYINT NOT NULL DEFAULT 3,
    affection_level TINYINT NOT NULL DEFAULT 3,
    child_friendly BOOLEAN NOT NULL DEFAULT TRUE,
    pet_friendly BOOLEAN NOT NULL DEFAULT TRUE,
    shedding_level TINYINT NOT NULL DEFAULT 3,
    nocturnal BOOLEAN NOT NULL DEFAULT FALSE,
    space_need_level TINYINT NOT NULL DEFAULT 2,
    beginner_friendly BOOLEAN NOT NULL DEFAULT TRUE,
    special_care_needed BOOLEAN NOT NULL DEFAULT FALSE,
    environment_preference VARCHAR(20) NOT NULL DEFAULT 'ANY',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NULL DEFAULT NULL,
    CONSTRAINT fk_cat_traits_cat FOREIGN KEY (cat_id) REFERENCES animals(AnimalID)
);

CREATE TABLE IF NOT EXISTS match_weights (
    weight_key VARCHAR(50) PRIMARY KEY,
    weight_value DECIMAL(6,3) NOT NULL
);

INSERT INTO match_weights(weight_key, weight_value)
VALUES
('activity', 1.000),
('affection', 0.600),
('shedding', 1.200),
('space', 1.000),
('beginner', 1.000),
('personality', 1.000)
ON DUPLICATE KEY UPDATE weight_value = weight_value;
