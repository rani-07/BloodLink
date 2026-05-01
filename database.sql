-- ============================================
--  BloodLink Database Schema
--  Run this in MySQL before starting the app
-- ============================================

CREATE DATABASE IF NOT EXISTS bloodlink;
USE bloodlink;

-- ── USERS TABLE ──────────────────────────────
CREATE TABLE IF NOT EXISTS users (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  first_name  VARCHAR(50)  NOT NULL,
  last_name   VARCHAR(50)  NOT NULL,
  email       VARCHAR(100) NOT NULL UNIQUE,
  password    VARCHAR(255) NOT NULL,         -- BCrypt hashed
  phone       VARCHAR(15),
  role        ENUM('donor','seeker','hospital','admin') DEFAULT 'donor',
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ── DONORS TABLE ─────────────────────────────
CREATE TABLE IF NOT EXISTS donors (
  id              INT AUTO_INCREMENT PRIMARY KEY,
  user_id         INT NOT NULL,
  blood_group     ENUM('A+','A-','B+','B-','AB+','AB-','O+','O-') NOT NULL,
  age             INT,
  gender          ENUM('Male','Female','Other'),
  weight          DECIMAL(5,2),
  city            VARCHAR(100),
  state           VARCHAR(100),
  pin_code        VARCHAR(10),
  available       BOOLEAN DEFAULT TRUE,
  last_donation   DATE,
  next_eligible   DATE,
  total_donations INT DEFAULT 0,
  created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ── BLOOD REQUESTS TABLE ──────────────────────
CREATE TABLE IF NOT EXISTS blood_requests (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  requester_id  INT,
  blood_group   ENUM('A+','A-','B+','B-','AB+','AB-','O+','O-') NOT NULL,
  hospital_name VARCHAR(200),
  location      VARCHAR(200),
  contact_phone VARCHAR(15),
  units_needed  INT DEFAULT 1,
  urgency       ENUM('critical','urgent','normal') DEFAULT 'urgent',
  notes         TEXT,
  status        ENUM('active','fulfilled','cancelled') DEFAULT 'active',
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (requester_id) REFERENCES users(id) ON DELETE SET NULL
);

-- ── DONATION HISTORY TABLE ────────────────────
CREATE TABLE IF NOT EXISTS donation_history (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  donor_id    INT NOT NULL,
  request_id  INT,
  hospital    VARCHAR(200),
  city        VARCHAR(100),
  donated_on  DATE NOT NULL,
  FOREIGN KEY (donor_id)  REFERENCES donors(id) ON DELETE CASCADE,
  FOREIGN KEY (request_id) REFERENCES blood_requests(id) ON DELETE SET NULL
);

-- ── CONTACT REQUESTS TABLE ───────────────────
CREATE TABLE IF NOT EXISTS contact_requests (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  donor_id    INT NOT NULL,
  sender_name VARCHAR(100),
  sender_phone VARCHAR(15),
  hospital    VARCHAR(200),
  urgency     VARCHAR(50),
  message     TEXT,
  status      ENUM('pending','accepted','declined') DEFAULT 'pending',
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (donor_id) REFERENCES donors(id) ON DELETE CASCADE
);

-- ── SAMPLE DATA ───────────────────────────────
INSERT INTO users (first_name, last_name, email, password, phone, role) VALUES
('Admin',  'BloodLink', 'admin@bloodlink.in',  '$2a$10$examplehashedpassword1', '9000000001', 'admin'),
('Rahul',  'Mehta',     'rahul@email.com',     '$2a$10$examplehashedpassword2', '9876543210', 'donor'),
('Priya',  'Sharma',    'priya@email.com',     '$2a$10$examplehashedpassword3', '9123456789', 'donor'),
('Amit',   'Patel',     'amit@email.com',      '$2a$10$examplehashedpassword4', '9988776655', 'donor');

INSERT INTO donors (user_id, blood_group, age, gender, weight, city, state, available, total_donations) VALUES
(2, 'A+', 28, 'Male',   72.0, 'Mumbai',    'Maharashtra', TRUE,  7),
(3, 'O+', 24, 'Female', 58.0, 'Mumbai',    'Maharashtra', TRUE,  3),
(4, 'B+', 32, 'Male',   80.0, 'Pune',      'Maharashtra', TRUE,  12);

INSERT INTO blood_requests (blood_group, hospital_name, location, contact_phone, units_needed, urgency) VALUES
('O-',  'Kokilaben Hospital',  'Andheri West, Mumbai', '02230999999', 2, 'critical'),
('AB-', 'Apollo Hospitals',    'Navi Mumbai',          '02267603000', 1, 'critical'),
('B-',  'Sunrise Childrens',   'Delhi',                '01143210000', 1, 'urgent');
