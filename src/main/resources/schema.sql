CREATE DATABASE IF NOT EXISTS CV_Administrador
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE CV_Administrador;
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS careers (
    career_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    code VARCHAR(20) UNIQUE,
    description VARCHAR(255),
    duration_years INT DEFAULT 3,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS usuarios (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('graduate', 'company', 'admin') NOT NULL,
    status ENUM('active', 'inactive', 'pending') NOT NULL DEFAULT 'pending',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

CREATE TABLE IF NOT EXISTS egresados (
    graduate_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    document_type VARCHAR(20),
    document_number VARCHAR(20),
    phone VARCHAR(30),
    address VARCHAR(180),
    city VARCHAR(80),
    country VARCHAR(80),
    birth_date DATE,
    photo_url VARCHAR(255),
    linkedin_url VARCHAR(255),
    portfolio_url VARCHAR(255),
    career_id BIGINT,
    graduation_year INT,
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    expected_salary DECIMAL(10,2),
    availability VARCHAR(80),
    CONSTRAINT fk_egresado_user FOREIGN KEY (user_id) REFERENCES usuarios(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_egresado_career FOREIGN KEY (career_id) REFERENCES careers(career_id)
);

CREATE TABLE IF NOT EXISTS cvs (
    cv_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    graduate_id BIGINT NOT NULL,
    title VARCHAR(180) NOT NULL,
    professional_summary TEXT NOT NULL,
    cv_pdf_url VARCHAR(255),
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    views_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cv_graduate FOREIGN KEY (graduate_id) REFERENCES egresados(graduate_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS educacion (
    education_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cv_id BIGINT NOT NULL,
    institution VARCHAR(180) NOT NULL,
    degree VARCHAR(180),
    field_of_study VARCHAR(180),
    start_date DATE,
    end_date DATE,
    is_current BOOLEAN NOT NULL DEFAULT FALSE,
    description TEXT,
    gpa DECIMAL(4,2),
    CONSTRAINT fk_educacion_cv FOREIGN KEY (cv_id) REFERENCES cvs(cv_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS experiencia (
    experiencia_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cv_id BIGINT NOT NULL,
    empresa_nombre VARCHAR(180) NOT NULL,
    posicion VARCHAR(180) NOT NULL,
    start_date DATE,
    end_date DATE,
    is_current BOOLEAN NOT NULL DEFAULT FALSE,
    responsibilities TEXT,
    achievements TEXT,
    employment_type VARCHAR(40),
    CONSTRAINT fk_experiencia_cv FOREIGN KEY (cv_id) REFERENCES cvs(cv_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS habilidades (
    habilidad_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cv_id BIGINT NOT NULL,
    habilidad_name VARCHAR(120) NOT NULL,
    habilidad_category ENUM('technical', 'soft', 'other') NOT NULL DEFAULT 'other',
    preferencia_level INT NOT NULL DEFAULT 3,
    CONSTRAINT fk_habilidad_cv FOREIGN KEY (cv_id) REFERENCES cvs(cv_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS idiomas (
    language_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cv_id BIGINT NOT NULL,
    language_name VARCHAR(80) NOT NULL,
    proficiency_level ENUM('basic', 'intermediate', 'advanced', 'native') NOT NULL DEFAULT 'basic',
    certifications VARCHAR(255),
    CONSTRAINT fk_idioma_cv FOREIGN KEY (cv_id) REFERENCES cvs(cv_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS certificaciones (
    certification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cv_id BIGINT NOT NULL,
    name VARCHAR(160) NOT NULL,
    issuing_organization VARCHAR(160),
    issue_date DATE,
    expiration_date DATE,
    credential_id VARCHAR(100),
    credential_url VARCHAR(255),
    CONSTRAINT fk_certificacion_cv FOREIGN KEY (cv_id) REFERENCES cvs(cv_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS companies (
    company_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    company_name VARCHAR(180),
    ruc VARCHAR(20),
    industry VARCHAR(120),
    company_size VARCHAR(60),
    phone VARCHAR(30),
    website VARCHAR(255),
    description TEXT,
    logo_url VARCHAR(255),
    CONSTRAINT fk_company_user FOREIGN KEY (user_id) REFERENCES usuarios(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS company_favorites (
    favorite_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    cv_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_company_favorite_cv (company_id, cv_id),
    CONSTRAINT fk_company_favorite_company FOREIGN KEY (company_id) REFERENCES companies(company_id) ON DELETE CASCADE,
    CONSTRAINT fk_company_favorite_cv FOREIGN KEY (cv_id) REFERENCES cvs(cv_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS contact_requests (
    request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    graduate_id BIGINT NOT NULL,
    message TEXT,
    status ENUM('pending', 'accepted', 'rejected') NOT NULL DEFAULT 'pending',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_request_company FOREIGN KEY (company_id) REFERENCES companies(company_id) ON DELETE CASCADE,
    CONSTRAINT fk_request_graduate FOREIGN KEY (graduate_id) REFERENCES egresados(graduate_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS password_reset_tokens (
    token_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash CHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_password_reset_user FOREIGN KEY (user_id) REFERENCES usuarios(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS audit_logs (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(120) NOT NULL,
    entity_type VARCHAR(60),
    entity_id BIGINT,
    details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES usuarios(user_id) ON DELETE SET NULL
);

CREATE INDEX idx_usuarios_role ON usuarios(role);
CREATE INDEX idx_usuarios_status ON usuarios(status);
CREATE INDEX idx_egresados_career ON egresados(career_id);
CREATE INDEX idx_egresados_city ON egresados(city);
CREATE INDEX idx_cvs_published ON cvs(is_published);
CREATE INDEX idx_cvs_graduate ON cvs(graduate_id);
CREATE INDEX idx_habilidades_name ON habilidades(habilidad_name);
CREATE INDEX idx_idiomas_name ON idiomas(language_name);
CREATE INDEX idx_company_favorites_company ON company_favorites(company_id);
CREATE INDEX idx_company_favorites_cv ON company_favorites(cv_id);
CREATE INDEX idx_contact_requests_company ON contact_requests(company_id);
CREATE INDEX idx_contact_requests_graduate ON contact_requests(graduate_id);
CREATE INDEX idx_contact_requests_status ON contact_requests(status);
CREATE INDEX idx_password_reset_user ON password_reset_tokens(user_id);
CREATE INDEX idx_password_reset_expires ON password_reset_tokens(expires_at);
CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_created ON audit_logs(created_at);

INSERT INTO careers (name, code, description, duration_years, is_active)
VALUES
    ('Ingeniería de Software', 'ISW', 'Desarrollo y arquitectura de software', 5, TRUE),
    ('Administración de Redes', 'RED', 'Infraestructura y soporte de redes', 3, TRUE),
    ('Diseño Gráfico Digital', 'DGD', 'Comunicación visual y medios digitales', 3, TRUE)
ON DUPLICATE KEY UPDATE
    description = VALUES(description),
    duration_years = VALUES(duration_years),
    is_active = VALUES(is_active);
