USE hzsca;

INSERT INTO cat_traits(
    cat_id,
    personality,
    activity_level,
    affection_level,
    child_friendly,
    pet_friendly,
    shedding_level,
    nocturnal,
    space_need_level,
    beginner_friendly,
    special_care_needed,
    environment_preference,
    update_time
)
SELECT
    a.AnimalID,
    CASE (a.AnimalID % 4)
        WHEN 0 THEN 'CUDDLY'
        WHEN 1 THEN 'INDEPENDENT'
        WHEN 2 THEN 'PLAYFUL'
        ELSE 'ANY'
    END AS personality,
    1 + (a.AnimalID % 5) AS activity_level,
    1 + ((a.AnimalID + 1) % 5) AS affection_level,
    CASE WHEN (a.AnimalID % 10) = 0 THEN FALSE ELSE TRUE END AS child_friendly,
    CASE WHEN (a.AnimalID % 7) = 0 THEN FALSE ELSE TRUE END AS pet_friendly,
    1 + ((a.AnimalID + 2) % 5) AS shedding_level,
    CASE WHEN (a.AnimalID % 6) = 0 THEN TRUE ELSE FALSE END AS nocturnal,
    1 + (a.AnimalID % 3) AS space_need_level,
    CASE WHEN (a.AnimalID % 5) = 0 THEN FALSE ELSE TRUE END AS beginner_friendly,
    CASE WHEN a.Age IS NOT NULL AND a.Age >= 10 THEN TRUE ELSE FALSE END AS special_care_needed,
    CASE (a.AnimalID % 3)
        WHEN 0 THEN 'QUIET'
        WHEN 1 THEN 'LIVELY'
        ELSE 'ANY'
    END AS environment_preference,
    NOW()
FROM animals a
WHERE a.Species = '猫'
ON DUPLICATE KEY UPDATE
    personality = VALUES(personality),
    activity_level = VALUES(activity_level),
    affection_level = VALUES(affection_level),
    child_friendly = VALUES(child_friendly),
    pet_friendly = VALUES(pet_friendly),
    shedding_level = VALUES(shedding_level),
    nocturnal = VALUES(nocturnal),
    space_need_level = VALUES(space_need_level),
    beginner_friendly = VALUES(beginner_friendly),
    special_care_needed = VALUES(special_care_needed),
    environment_preference = VALUES(environment_preference),
    update_time = NOW();
