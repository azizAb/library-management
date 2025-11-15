CREATE TABLE login_attempts (
    id BIGSERIAL PRIMARY KEY,
    identifier VARCHAR(100) NOT NULL,
    ip_address VARCHAR(45),
    successful BOOLEAN NOT NULL,
    attempt_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_login_attempts_identifier ON login_attempts(identifier);
CREATE INDEX idx_login_attempts_time ON login_attempts(attempt_time DESC);

-- Cleanup old login attempts (older than 30 days)
CREATE OR REPLACE FUNCTION cleanup_old_login_attempts()
RETURNS void AS $$
BEGIN
    DELETE FROM login_attempts 
    WHERE attempt_time < NOW() - INTERVAL '30 days';
END;
$$ LANGUAGE plpgsql;