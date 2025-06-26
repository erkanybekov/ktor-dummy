-- Initialize database for Ktor Todo API
CREATE DATABASE ktor_todo;

-- Connect to the ktor_todo database
\c ktor_todo;

-- Create extensions if needed
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- The tables will be created automatically by Exposed ORM
-- This file is just for any initial setup or data

-- You can add initial data here if needed
-- INSERT INTO users (id, email, name, password_hash, is_email_verified, created_at, updated_at) 
-- VALUES (uuid_generate_v4(), 'admin@example.com', 'Admin User', 'hashed_password', true, NOW(), NOW());

COMMENT ON DATABASE ktor_todo IS 'Database for Ktor Todo API with JWT Authentication'; 