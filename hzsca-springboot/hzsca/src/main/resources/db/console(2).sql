-- 创建管理员表
CREATE TABLE Admins (
    id INT AUTO_INCREMENT PRIMARY KEY,  -- 管理员ID，自动递增，主键
    username VARCHAR(50) NOT NULL,        -- 管理员用户名，不能为空
    password VARCHAR(255) NOT NULL,        -- 管理员密码，不能为空
    name VARCHAR(50) NOT NULL,            -- 昵称
    role ENUM('admin', 'user') NOT NULL DEFAULT 'user', -- 权限
    avatar VARCHAR(255)                      -- 用户头像
);

-- 创建普通用户表
CREATE TABLE Users (
    UserID INT AUTO_INCREMENT PRIMARY KEY,  -- 用户ID，自动递增，主键
    Username VARCHAR(50) NOT NULL,        -- 用户名，不能为空
    Password VARCHAR(255) NOT NULL,        -- 密码，不能为空
    Phone VARCHAR(15),              -- 电话号码
    name VARCHAR(50) NOT NULL,            -- 昵称
    Gender INT NOT NULL, -- 性别
    Profile TEXT,                        -- 个人简介
    role ENUM('admin', 'user', 'guest') NOT NULL DEFAULT 'user', -- 权限
    avatar VARCHAR(255) NULL                     -- 用户头像
);

-- 创建领养人表
CREATE TABLE Adopters (
    AdopterID INT AUTO_INCREMENT PRIMARY KEY,  -- 领养人ID，自动递增，主键
    UserID INT NOT NULL,                        -- 关联的普通用户ID
    Name VARCHAR(50) NOT NULL,                -- 领养人姓名，不能为空
    Age INT,                                  -- 领养人年龄
    IDCardNumber VARCHAR(18) NOT NULL,         -- 领养人身份证号码，不能为空
    Phone VARCHAR(15) NOT NULL,               -- 领养人电话号码，不能为空
    Email VARCHAR(100),                       -- 领养人电子邮件地址
    Address VARCHAR(255) NOT NULL,            -- 领养人地址，不能为空
    BirthDate DATE,                           -- 领养人出生日期
    FOREIGN KEY (UserID) REFERENCES Users(UserID)  -- 外键，关联普通用户表
);

-- 创建动物表
CREATE TABLE Animals (
    AnimalID INT AUTO_INCREMENT PRIMARY KEY,  -- 动物ID，自动递增，主键
    Name VARCHAR(50) NOT NULL,                -- 动物名字，不能为空
    Species VARCHAR(50) NOT NULL,             -- 动物种类，不能为空
    Breed VARCHAR(50),                        -- 动物品种
    Age FLOAT,                                  -- 动物年龄
    Gender ENUM('雄性', '雌性', '未知') NOT NULL,  -- 动物性别，不能为空
    HealthStatus VARCHAR(100),                -- 动物健康状况
    Description TEXT,                         -- 动物详细描述
    ImageURL VARCHAR(255),                    -- 动物图片URL
    Source VARCHAR(100)                        -- 动物来源
);

-- 创建领养评估表
CREATE TABLE AdoptionAssessment (
    AssessmentID INT AUTO_INCREMENT PRIMARY KEY,  -- 评估ID，自动递增，主键
    AdopterID INT NOT NULL,                        -- 关联的领养人ID
    Name VARCHAR(50) NOT NULL,                    -- 领养人的姓名
    FamilyIncome DECIMAL(10, 2),                  -- 家庭收入
    HasOwnedPets BOOLEAN,                         -- 是否养过小动物
    Smoker BOOLEAN,                               -- 家中是否有人吸烟
    Asthma BOOLEAN,                               -- 家中是否有人哮喘
    Allergy BOOLEAN,                              -- 动物毛过敏
    LivingSpace VARCHAR(100),                     -- 居住空间
    PetFriendly BOOLEAN,                          -- 是否宠物友好
    FOREIGN KEY (AdopterID) REFERENCES Adopters(AdopterID)  -- 外键，关联领养人表
);

-- 创建活动管理表
CREATE TABLE Events (
    EventID INT AUTO_INCREMENT PRIMARY KEY,  -- 活动ID，自动递增，主键
    Location VARCHAR(255) NOT NULL,           -- 活动地点，不能为空
    Time DATETIME NOT NULL,                   -- 活动时间，不能为空
    Content TEXT NOT NULL                     -- 活动内容，不能为空
);

-- 创建义工申请表
CREATE TABLE VolunteerApplications (
    ApplicationID INT AUTO_INCREMENT PRIMARY KEY,  -- 申请ID，自动递增，主键
    Name VARCHAR(50) NOT NULL,                      -- 义工姓名，不能为空
    Gender ENUM('男', '女', '其他') NOT NULL,       -- 义工性别，不能为空
    Age INT NOT NULL,                               -- 义工年龄，不能为空
    Phone VARCHAR(15) NOT NULL,                     -- 义工电话号码，不能为空
    AvailableTime VARCHAR(255) NOT NULL              -- 义工空闲时间，不能为空
);



INSERT
INTO Admins
VALUES (1,'admin',123,'管理员1','admin','https://example.com/avatar.jpg');

INSERT
INTO users
VALUES (1,'user1',123456,18854673428,'u1',1,'无','user','https://example.com/avatar.jpg'),
       (2,'user2',123456,13748972389,'u2',2,'无','user','https://example.com/avatar.jpg'),
       (3,'user3',123456,13768970956,'u3',1,'无','user','https://example.com/avatar.jpg'),
       (4,'user4',123456,18857943689,'u4',2,'无','user','https://example.com/avatar.jpg'),
       (5,'user5',123456,18848792376,'u5',1,'无','user','https://example.com/avatar.jpg'),
       (6,'user6',123456,19932449988,'u6',2,'无','user','https://example.com/avatar.jpg'),
       (7,'user7',123456,18829006789,'u7',1,'无','user','https://example.com/avatar.jpg'),
       (8,'user8',123456,13788906799,'u8',2,'无','user','https://example.com/avatar.jpg'),
       (9,'user9',123456,15099615688,'u9',1,'无','user','https://example.com/avatar.jpg'),
       (10,'user10',123456,15022895543,'u10',2,'无','user','https://example.com/avatar.jpg');

INSERT INTO Animals (AnimalID, Name, Species, Breed, Age, Gender, HealthStatus, Description, ImageURL, Source) VALUES
(1, '奶糖', '猫', '美国短毛猫', 1.0, '雄性', '健康', '无', '/cats/1.jpeg', '流浪'),
(2, '古甜甜', '猫', '家猫', 1.0, '雄性', '健康', '无', '/cats/2.jpeg', '流浪'),
(3, 'Sumi', '猫', '狸花猫', 0.5, '雄性', '健康', '无', '/cats/3.jpeg', '家养'),
(4, '小圆', '猫', '土耳其梵猫', 3.0, '雄性', '健康', '无', '/cats/4.jpeg', '流浪'),
(5, '球球', '猫', '日本短尾猫', 4.0, '雄性', '健康', '无', '/cats/5.jpeg', '流浪'),
(6, '豆豆', '猫', '美国短毛猫', 3.0, '雄性', '良好', '无', '/cats/6.jpeg', '流浪'),
(7, '芳珠珠', '猫', '狸花猫', 3.0, '雄性', '良好', '无', '/cats/7.jpeg', '家养'),
(8, '果冻', '猫', '孟买猫', 5.0, '雄性', '良好', '无', '/cats/8.jpeg', '流浪'),
(9, '古羊羊', '猫', '美国短毛猫', 1.0, '雄性', '良好', '无', '/cats/9.jpeg', '家养'),
(10, '棉花', '猫', '美国硬毛猫', 4.0, '雄性', '良好', '无', '/cats/10.jpeg', '流浪'),
(11, '古湘湘', '猫', '挪威森林猫', 1.0, '雄性', '健康', '无', '/cats/11.jpeg', '家养'),
(12, '小丸子', '猫', '美国短毛猫', 3.0, '雄性', '健康', '无', '/cats/12.jpeg', '流浪'),
(13, '奶昔', '猫', '家猫', 0.4, '雄性', '健康', '无', '/cats/13.jpeg', '流浪'),
(14, '棉花糖', '猫', '狸花猫', 4.0, '雄性', '健康', '无', '/cats/14.jpeg', '流浪'),
(15, 'Koharu', '猫', '美国短毛猫', 0.5, '雄性', '健康', '无', '/cats/15.jpeg', '家养'),
(16, '芳失失', '猫', '狸花猫', 2.0, '雄性', '健康', '无', '/cats/16.jpeg', '家养'),
(17, '暖暖', '猫', '美国短毛猫', 5.0, '雄性', '健康', '无', '/cats/17.jpeg', '流浪'),
(18, '软软', '猫', '狸花猫', 0.3, '雄性', '健康', '无', '/cats/18.jpeg', '流浪'),
(19, '糯糯', '猫', '橘猫', 3.0, '雄性', '健康', '无', '/cats/19.jpeg', '流浪'),
(20, '柔柔', '猫', '美国短毛猫', 0.3, '雌性', '健康', '无', '/cats/20.jpeg', '流浪'),
(21, '闪电', '猫', '孟加拉猫', 0.3, '雌性', '健康', '无', '/cats/21.jpeg', '流浪'),
(22, '风风', '猫', '孟加拉猫', 1.0, '雌性', '健康', '无', '/cats/22.jpeg', '流浪'),
(23, '跳跳', '猫', '狸花猫', 2.0, '雌性', '良好', '无', '/cats/23.jpeg', '流浪'),
(24, '乐乐', '猫', '孟加拉猫', 1.0, '雌性', '良好', '无', '/cats/24.jpeg', '流浪'),
(25, '闹闹', '猫', '美国短毛猫', 1.0, '雌性', '良好', '无', '/cats/25.jpeg', '流浪'),
(26, '二哈', '猫', '玳瑁猫', 1.0, '雌性', '健康', '无', '/cats/26.jpeg', '流浪'),
(27, '兜兜', '猫', '中国狸花猫', 2.0, '雌性', '健康', '无', '/cats/27.jpeg', '流浪'),
(28, '皮皮', '猫', '家猫', 2.0, '雌性', '健康', '无', '/cats/28.jpeg', '流浪'),
(29, '泡芙', '猫', '美国短毛猫', 3.0, '雌性', '健康', '无', '/cats/29.jpeg', '流浪'),
(30, '毛毛', '猫', '狸花猫', 1.0, '雌性', '健康', '无', '/cats/30.jpeg', '流浪'),
(31, '墨韵', '猫', '美国短毛猫', 1.0, '雌性', '健康', '无', '/cats/31.jpeg', '流浪'),
(32, '怪兽', '猫', '美国短毛猫', 1.0, '雌性', '健康', '无', '/cats/32.jpeg', '流浪'),
(33, '墨香', '猫', '狮子猫', 1.0, '雌性', '健康', '无', '/cats/33.jpeg', '流浪'),
(34, '墨宝', '猫', '挪威森林猫', 1.0, '雌性', '健康', '无', '/cats/34.jpeg', '流浪'),
(35, 'Falcon', '猫', '挪威森林猫', 0.5, '雌性', '良好', '无', '/cats/35.jpeg', '家养'),
(36, '粽子', '猫', '家猫', 0.3, '雌性', '良好', '无', '/cats/36.jpeg', '流浪'),
(37, '云朵', '猫', '孟买猫', 0.3, '雌性', '良好', '无', '/cats/37.jpeg', '流浪'),
(38, '糖糖', '猫', '豹猫', 2.0, '雌性', '良好', '无', '/cats/38.jpeg', '流浪');