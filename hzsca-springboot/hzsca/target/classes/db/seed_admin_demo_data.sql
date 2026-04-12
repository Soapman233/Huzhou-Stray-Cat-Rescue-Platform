USE hzsca;

INSERT IGNORE INTO wx_user (id, openid, unionid, nickname, avatar, gender, city, province, country, last_login_time)
VALUES
(9001, 'demo_openid_9001', NULL, '小明', '', 1, '湖州', '浙江', '中国', NOW()),
(9002, 'demo_openid_9002', NULL, '小红', '', 2, '杭州', '浙江', '中国', NOW()),
(9003, 'demo_openid_9003', NULL, '小李', '', 1, '上海', '上海', '中国', NOW());

INSERT IGNORE INTO users (UserID, Username, Password, Name, Phone, Gender, Profile, Avatar, Role)
VALUES
(8001, 'demo_user_8001', '123456', '网页用户A', '13800000001', 1, '喜欢参与公益活动', '', 'user'),
(8002, 'demo_user_8002', '123456', '网页用户B', '13800000002', 2, '有多次救助经历', '', 'volunteer'),
(8003, 'demo_user_8003', '123456', '网页用户C', '13800000003', 1, '希望参与线下活动', '', 'user');

UPDATE wx_user SET role = 'volunteer' WHERE id = 9002;

INSERT IGNORE INTO adoption_request
(id, animal_id, wx_user_id, status, apply_time, audit_time, auditor_id, audit_reason, score, risk_level, risk_reasons)
VALUES
(90001, 1, 9001, 'PENDING',  '2024-09-05 10:12:00', NULL, NULL, NULL, 72, 'LOW',    '["资料完整","回访意愿强"]'),
(90002, 2, 9001, 'REJECTED', '2024-09-18 16:05:00', '2024-09-20 09:30:00', 1, '家庭过敏风险较高', 55, 'MEDIUM', '["过敏风险","陪伴时间不足"]'),
(90003, 3, 9002, 'APPROVED', '2024-10-03 11:20:00', '2024-10-06 14:10:00', 1, '通过审核', 88, 'LOW', '["养猫经验丰富","环境稳定"]'),
(90004, 4, 9002, 'CANCELED', '2024-10-21 09:45:00', NULL, NULL, NULL, 60, 'LOW', '["主动撤销"]'),
(90005, 5, 9003, 'PENDING',  '2024-11-02 13:05:00', NULL, NULL, NULL, 65, 'MEDIUM', '["需补充家庭信息"]'),
(90006, 6, 9003, 'APPROVED', '2024-11-18 18:40:00', '2024-11-20 10:00:00', 1, '通过审核', 82, 'LOW', '["预算充足","可接受回访"]'),
(90007, 7, 9001, 'REJECTED', '2024-12-01 08:30:00', '2024-12-03 15:20:00', 1, '住所空间不足', 48, 'HIGH', '["居住空间不足","同住宠物冲突"]'),
(90008, 8, 9002, 'PENDING',  '2024-12-10 20:15:00', NULL, NULL, NULL, 70, 'LOW', '["待进一步沟通"]');

INSERT IGNORE INTO adoption_request
(id, animal_id, wx_user_id, applicant_type, applicant_id, status, apply_time, score, risk_level, risk_reasons)
VALUES
(90009, 1, NULL, 'WEB', 8002, 'PENDING', '2024-12-18 11:00:00', 80, 'LOW', '["演示：网页志愿者用户申请"]');

INSERT IGNORE INTO rescue_report
(id, wx_user_id, lat, lng, address, description, image_urls, urgency, status, handler_id, handle_result, create_time, update_time)
VALUES
(91001, 9001, 30.8700, 120.0900, '湖州市吴兴区某小区', '发现受伤猫咪，需要救助', '', 5, 'OPEN', NULL, NULL, '2024-10-12 09:30:00', NULL),
(91002, 9002, 30.2800, 120.1500, '杭州市西湖区某公园', '疑似流浪幼猫，天气变冷', '', 4, 'IN_PROGRESS', 1, NULL, '2024-11-06 14:00:00', '2024-11-06 15:10:00'),
(91003, 9003, 31.2300, 121.4700, '上海市某街道', '流浪猫聚集点需要投喂与绝育', '', 3, 'CLOSED', 1, '已安排志愿者跟进', '2024-11-22 19:20:00', '2024-11-25 10:00:00'),
(91004, 9001, 30.9000, 120.1100, '湖州市某停车场', '猫咪疑似生病，精神萎靡', '', 4, 'OPEN', NULL, NULL, '2024-12-08 08:40:00', NULL);

INSERT IGNORE INTO rescue_report
(id, wx_user_id, reporter_type, reporter_id, lat, lng, address, description, image_urls, urgency, status, handler_id, handle_result, create_time, update_time)
VALUES
(91005, NULL, 'WEB', 8002, 30.8800, 120.1000, '网页用户上报点', '演示：网页端志愿者上报救助信息', '', 3, 'OPEN', NULL, NULL, '2024-12-19 13:30:00', NULL);

INSERT IGNORE INTO volunteer_application
(id, applicant_type, applicant_id, name, phone, age, experience, motivation, status, apply_time, audit_time, auditor_id, audit_reason)
VALUES
(92001, 'WX',  9001, '小明', '13800000011', 20, '参与校园投喂与临时救助', '希望参与更多线下救助与活动', 'PENDING',  '2024-12-05 10:00:00', NULL, NULL, NULL),
(92002, 'WX',  9002, '小红', '13800000012', 22, '已参与多次送养与回访', '愿意长期参与并协助组织', 'APPROVED', '2024-11-10 09:00:00', '2024-11-12 10:30:00', 1, '通过审核'),
(92003, 'WEB', 8001, '网页用户A', '13800000001', 24, '参与校内公益志愿活动', '希望参加协会活动并学习救助', 'PENDING',  '2024-12-12 14:20:00', NULL, NULL, NULL),
(92004, 'WEB', 8002, '网页用户B', '13800000002', 26, '多年流浪猫救助经验', '愿意承担活动现场协调', 'APPROVED', '2024-10-18 16:40:00', '2024-10-20 11:15:00', 1, '通过审核');

INSERT IGNORE INTO activity_participation
(id, activity_id, participant_type, participant_id, join_time)
VALUES
(93001, 1, 'WX',  9002, '2024-11-15 10:00:00'),
(93002, 1, 'WEB', 8002, '2024-11-15 10:05:00'),
(93003, 2, 'WX',  9002, '2024-12-01 09:20:00');

UPDATE activity_participation SET status = 'APPROVED', audit_time = NOW(), auditor_id = 1, audit_reason = '演示数据' WHERE id IN (93001, 93002);
UPDATE activity_participation SET status = 'PENDING' WHERE id IN (93003);
