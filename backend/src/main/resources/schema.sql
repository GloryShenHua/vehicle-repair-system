CREATE TABLE users (
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   username VARCHAR(100) NOT NULL UNIQUE,
   password VARCHAR(255) NOT NULL,
   role ENUM ('USER','MECHANIC','ADMIN') NOT NULL,
   full_name VARCHAR(100),
   phone VARCHAR(30),
   email VARCHAR(100),
   specialty ENUM ('PAINT','WELD','MECHANIC') NULL,
   hourly_rate DECIMAL(8,2) NULL,
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vehicles (
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   owner_id BIGINT NOT NULL,
   plate_no VARCHAR(20) NOT NULL UNIQUE,
   model VARCHAR(60),
   year_made SMALLINT,
   vin VARCHAR(50),
   FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE work_orders (
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   user_id BIGINT NOT NULL,
   vehicle_id BIGINT NOT NULL,
   status ENUM ('PENDING','IN_PROGRESS','COMPLETED') DEFAULT 'PENDING',
   feedback VARCHAR(500),
   total_cost DECIMAL(10,2),
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   completed_at TIMESTAMP NULL,
   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
   FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE
);

CREATE TABLE work_order_mechanics (
   work_order_id BIGINT,
   mechanic_id BIGINT,
   accepted TINYINT DEFAULT 0,
   PRIMARY KEY (work_order_id, mechanic_id),
   FOREIGN KEY (work_order_id) REFERENCES work_orders(id) ON DELETE CASCADE,
   FOREIGN KEY (mechanic_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE work_hours (
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   work_order_id BIGINT NOT NULL,
   mechanic_id BIGINT NOT NULL,
   hours DECIMAL(6,2) NOT NULL,
   hourly_rate DECIMAL(8,2) NOT NULL,
   FOREIGN KEY (work_order_id) REFERENCES work_orders(id) ON DELETE CASCADE,
   FOREIGN KEY (mechanic_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE materials (
   id BIGINT PRIMARY KEY AUTO_INCREMENT,
   work_order_id BIGINT NOT NULL,
   name VARCHAR(100),
   quantity INT,
   unit_price DECIMAL(8,2),
   FOREIGN KEY (work_order_id) REFERENCES work_orders(id) ON DELETE CASCADE
);

DELIMITER //
CREATE TRIGGER trg_calc_cost
    AFTER UPDATE ON work_orders
    FOR EACH ROW
BEGIN
    IF NEW.status = 'COMPLETED' AND OLD.status <> 'COMPLETED' THEN
    UPDATE work_orders SET total_cost = (
        (SELECT IFNULL(SUM(quantity*unit_price),0) FROM materials WHERE work_order_id = NEW.id) +
        (SELECT IFNULL(SUM(hours*hourly_rate),0) FROM work_hours WHERE work_order_id = NEW.id)
        )
    WHERE id = NEW.id;
END IF;
END //
DELIMITER ;
