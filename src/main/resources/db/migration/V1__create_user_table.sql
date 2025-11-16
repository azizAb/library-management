CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    fullname VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'VIEWER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_locked BOOLEAN NOT NULL DEFAULT FALSE,
    lock_until TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Insert default super admin (password: Admin123!)
INSERT INTO users (fullname, username, email, password, role, enabled, account_locked)
VALUES ('Super Administrator', 'admin', 'admin@library.com', 
        '$2a$10$oy.rKJT/Rv0XTqKWBNdM/Oyz65E5lDCXoiu9jKAIwmB08bdX1Xu3u', 
        'SUPER_ADMIN', TRUE, FALSE);