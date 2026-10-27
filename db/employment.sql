-- 高校学生就业信息与去向分析平台 数据库脚本
CREATE DATABASE IF NOT EXISTS kfszy DEFAULT CHARACTER SET utf8mb4;
USE kfszy;

DROP TABLE IF EXISTS application;
DROP TABLE IF EXISTS employment;
DROP TABLE IF EXISTS job;
DROP TABLE IF EXISTS enterprise;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password CHAR(32) NOT NULL,
  role VARCHAR(20) NOT NULL COMMENT 'ADMIN/STUDENT/ENTERPRISE',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1正常 0禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '用户表';

CREATE TABLE student (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL UNIQUE,
  student_no VARCHAR(30),
  name VARCHAR(50) NOT NULL,
  gender VARCHAR(10),
  college VARCHAR(100),
  major VARCHAR(100),
  class_name VARCHAR(50),
  grade VARCHAR(20),
  phone VARCHAR(20),
  email VARCHAR(100)
) COMMENT '学生表';

CREATE TABLE enterprise (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  industry VARCHAR(100),
  address VARCHAR(200),
  contact VARCHAR(50),
  phone VARCHAR(20),
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED'
) COMMENT '企业表';

CREATE TABLE job (
  id INT PRIMARY KEY AUTO_INCREMENT,
  enterprise_id INT NOT NULL,
  title VARCHAR(100) NOT NULL,
  salary VARCHAR(50),
  city VARCHAR(50),
  description TEXT,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '招聘岗位表';

CREATE TABLE application (
  id INT PRIMARY KEY AUTO_INCREMENT,
  job_id INT NOT NULL,
  student_id INT NOT NULL,
  summary VARCHAR(500),
  status VARCHAR(20) NOT NULL DEFAULT 'SUBMITTED' COMMENT 'SUBMITTED/ACCEPTED/REJECTED',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_job_student (job_id, student_id)
) COMMENT '投递记录表';

CREATE TABLE employment (
  id INT PRIMARY KEY AUTO_INCREMENT,
  student_id INT NOT NULL UNIQUE,
  type VARCHAR(20) NOT NULL COMMENT 'EMPLOYED/FURTHER_STUDY/ENTREPRENEUR/UNEMPLOYED',
  company VARCHAR(100),
  position VARCHAR(100),
  salary INT,
  city VARCHAR(50),
  remark VARCHAR(500),
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '毕业去向表';

-- 初始数据（密码均为 123456 的 MD5）
INSERT INTO `user` (username, password, role) VALUES ('admin', MD5('admin123'), 'ADMIN');
INSERT INTO `user` (username, password, role) VALUES ('huawei', MD5('123456'), 'ENTERPRISE');
INSERT INTO `user` (username, password, role) VALUES ('tencent', MD5('123456'), 'ENTERPRISE');
INSERT INTO `user` (username, password, role) VALUES ('byd', MD5('123456'), 'ENTERPRISE');
INSERT INTO enterprise (user_id, name, industry, address, contact, phone, status) VALUES
(2, '华为技术有限公司', '信息技术', '深圳市龙岗区坂田华为基地', '李经理', '0755-28780808', 'APPROVED'),
(3, '腾讯科技(深圳)有限公司', '信息技术', '深圳市南山区科技园腾讯大厦', '王经理', '0755-86013388', 'APPROVED'),
(4, '比亚迪股份有限公司', '汽车制造', '深圳市坪山区比亚迪路3009号', '赵经理', '0755-89888888', 'APPROVED');

INSERT INTO `user` (username, password, role) VALUES
('s01', MD5('123456'), 'STUDENT'), ('s02', MD5('123456'), 'STUDENT'),
('s03', MD5('123456'), 'STUDENT'), ('s04', MD5('123456'), 'STUDENT'),
('s05', MD5('123456'), 'STUDENT'), ('s06', MD5('123456'), 'STUDENT'),
('s07', MD5('123456'), 'STUDENT'), ('s08', MD5('123456'), 'STUDENT'),
('s09', MD5('123456'), 'STUDENT'), ('s10', MD5('123456'), 'STUDENT');

INSERT INTO student (user_id, student_no, name, gender, college, major, class_name, grade, phone, email) VALUES
(5,  '20220101', '张伟',   '男', '计算机学院', '软件工程',   '软件2201', '2022', '13800000001', 'zhangwei@stu.edu.cn'),
(6,  '20220102', '李娜',   '女', '计算机学院', '软件工程',   '软件2201', '2022', '13800000002', 'lina@stu.edu.cn'),
(7,  '20220103', '王强',   '男', '计算机学院', '计算机科学', '计科2201', '2022', '13800000003', 'wangq@stu.edu.cn'),
(8,  '20220104', '刘敏',   '女', '计算机学院', '计算机科学', '计科2201', '2022', '13800000004', 'liumin@stu.edu.cn'),
(9,  '20220105', '陈磊',   '男', '计算机学院', '人工智能',   '智科2201', '2022', '13800000005', 'chenlei@stu.edu.cn'),
(10, '20220106', '赵芳',   '女', '经济管理学院', '会计学',   '会计2201', '2022', '13800000006', 'zhaofang@stu.edu.cn'),
(11, '20220107', '孙涛',   '男', '经济管理学院', '工商管理', '工管2201', '2022', '13800000007', 'suntao@stu.edu.cn'),
(12, '20220108', '周颖',   '女', '经济管理学院', '金融学',   '金融2201', '2022', '13800000008', 'zhouying@stu.edu.cn'),
(13, '20220109', '吴斌',   '男', '外国语学院', '英语',       '英语2201', '2022', '13800000009', 'wubin@stu.edu.cn'),
(14, '20220110', '郑爽',   '女', '外国语学院', '日语',       '日语2201', '2022', '13800000010', 'zhengshuang@stu.edu.cn');

INSERT INTO job (enterprise_id, title, salary, city, description, status) VALUES
(1, 'Java开发工程师', '12000-15000元/月', '深圳', '负责后端服务开发与维护，熟悉Spring框架，本科及以上学历。', 'APPROVED'),
(2, '前端开发工程师', '10000-14000元/月', '深圳', '负责Web前端页面开发，熟悉Vue/React框架。', 'APPROVED'),
(1, '算法工程师', '20000-30000元/月', '北京', '负责机器学习算法研发，硕士及以上学历优先。', 'APPROVED'),
(3, '嵌入式软件开发工程师', '9000-12000元/月', '深圳', '负责车载软件系统开发，熟悉C/C++。', 'APPROVED'),
(2, '测试工程师', '8000-10000元/月', '成都', '负责软件功能测试与自动化测试。', 'PENDING'),
(3, '市场专员', '6000-8000元/月', '长沙', '负责区域市场推广与客户维护。', 'APPROVED');

INSERT INTO application (job_id, student_id, summary, status) VALUES
(1, 1, '熟悉Java、Spring框架，有实习经验，通过CET-6。', 'SUBMITTED'),
(2, 2, '熟练掌握Vue3、JavaScript，有两个完整项目经验。', 'ACCEPTED');

INSERT INTO employment (student_id, type, company, position, salary, city, remark) VALUES
(1, 'EMPLOYED', '华为技术有限公司', 'Java开发工程师', 13000, '深圳', '通过校招签约'),
(2, 'EMPLOYED', '腾讯科技(深圳)有限公司', '前端开发工程师', 12000, '深圳', NULL),
(3, 'EMPLOYED', '比亚迪股份有限公司', '嵌入式开发工程师', 10000, '深圳', NULL),
(4, 'FURTHER_STUDY', '武汉大学', '计算机技术硕士', NULL, '武汉', '考研录取'),
(5, 'EMPLOYED', '字节跳动', '后端开发工程师', 15000, '北京', NULL),
(6, 'EMPLOYED', '招商银行', '软件开发岗', 11000, '上海', NULL),
(7, 'FURTHER_STUDY', '中山大学', '会计专硕', NULL, '广州', NULL),
(8, 'ENTREPRENEUR', '杭州某网络科技有限公司', '合伙人', 8000, '杭州', '自主创业，主营跨境电商'),
(9, 'EMPLOYED', '中国建设银行', '柜员', 7000, '长沙', NULL),
(10, 'UNEMPLOYED', NULL, NULL, NULL, NULL, '备考公务员中');
