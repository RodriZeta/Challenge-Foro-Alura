INSERT INTO profiles (name) VALUES ('USER') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO profiles (name) VALUES ('ADMIN') ON DUPLICATE KEY UPDATE name = name;

INSERT INTO courses (name) VALUES ('Java') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO courses (name) VALUES ('Spring') ON DUPLICATE KEY UPDATE name = name;
INSERT INTO courses (name) VALUES ('Docker') ON DUPLICATE KEY UPDATE name = name;
