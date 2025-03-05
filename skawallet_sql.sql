CREATE TABLE users (
    pk_users SERIAL PRIMARY KEY,
    name CHARACTER VARYING NOT NULL,
    email CHARACTER VARYING UNIQUE NOT NULL,
    phone CHARACTER VARYING  UNIQUE NOT NULL,
    password CHARACTER VARYING NOT NULL,
    type CHARACTER VARYING  CHECK (type IN ('USER', 'ADMIN', 'MERCHANT')) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    enabled BOOLEAN DEFAULT TRUE,
    locked BOOLEAN DEFAULT FALSE,
    two_factor_enabled BOOLEAN DEFAULT FALSE, -- Suporte para autenticação de dois fatores
    two_factor_secret CHARACTER VARYING ,          -- Chave para autenticação de dois fatores (opcional)
    last_login TIMESTAMP                      -- Último login
);

CREATE TABLE digital_wallets (
    pk_digital_wallets SERIAL PRIMARY KEY,
    wallet_name CHARACTER VARYING NOT NULL,
    wallet_type CHARACTER VARYING  CHECK (wallet_type IN ('PERSONAL', 'MERCHANT', 'SAVINGS')) DEFAULT 'PERSONAL',
    balance NUMERIC(15, 2) NOT NULL DEFAULT 0.0,
    currency CHARACTER VARYING  DEFAULT 'AKZ', -- Suporte a múltiplas moedas
    fk_users INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    wallet_code CHARACTER VARYING
    is_default BOOLEAN DEFAULT FALSE, -- Indica a carteira padrão do usuário
    CONSTRAINT digital_wallets_fk_users FOREIGN KEY (fk_users)
        REFERENCES users (pk_users) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE transactions (
    pk_transactions SERIAL PRIMARY KEY,
    amount NUMERIC(15, 2) NOT NULL,
    transaction_type CHARACTER VARYING  CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER', 'PAYMENT')) NOT NULL,
    status CHARACTER VARYING  CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    fk_source_wallet INTEGER, -- Fonte da transação (opcional)
    fk_destination_wallet INTEGER, -- Destino da transação (opcional)
    payment_method CHARACTER VARYING  CHECK (payment_method IN ('CARD', 'BANK', 'DIGITAL_WALLET')) NOT NULL,
    description TEXT, -- Descrição da transação
    metadata JSONB, -- Dados adicionais em formato JSON
    FOREIGN KEY (fk_source_wallet) REFERENCES digital_wallets (pk_digital_wallets) ON DELETE SET NULL,
    FOREIGN KEY (fk_destination_wallet) REFERENCES digital_wallets (pk_digital_wallets) ON DELETE SET NULL
);

-- Criando o tipo ENUM para event_type
CREATE TYPE event_type AS ENUM ('CREATED', 'UPDATED', 'DELETED');

-- Criando a tabela transaction_history
CREATE TABLE transaction_history (
    pk_transaction_history SERIAL PRIMARY KEY,
    fk_transactions INT,
    event_type CHARACTER VARYING NOT NULL,  -- Usando o tipo ENUM criado
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fk_transactions) REFERENCES transactions(pk_transactions) ON DELETE CASCADE
);

CREATE TYPE category_type AS ENUM ('TELECOM', 'ENERGY', 'GOVERNMENT', 'FINANCE', 'RETAIL');

CREATE TABLE partners (
    pk_partners SERIAL PRIMARY KEY,
    partner_code CHARACTER VARYING UNIQUE NOT NULL, -- Código único do parceiro (e.g., "UNITEL")
    name CHARACTER VARYING NOT NULL, -- Nome da entidade
    description TEXT, -- Descrição do parceiro
    category CHARACTER VARYING CHECK (category IN ('TELECOM', 'ENERGY', 'GOVERNMENT', 'FINANCE', 'RETAIL')), -- Tipo de entidade
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Data de criação
    payment_supported BOOLEAN DEFAULT TRUE, -- Indica se aceita pagamentos via carteira digital
    contact_info CHARACTER VARYING -- Informações de contato (telefone, email, etc.)
);


CREATE TABLE cards (
    pk_cards SERIAL PRIMARY KEY,
    card_number CHARACTER VARYING  NOT NULL,
    card_holder_name CHARACTER VARYING NOT NULL,
    expiration_date DATE NOT NULL,
    cvv CHARACTER VARYING  NOT NULL,
    fk_wallet INTEGER NOT NULL, -- Vínculo com a carteira digital
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fk_wallet) REFERENCES digital_wallets (pk_digital_wallets) ON DELETE CASCADE
);

CREATE TABLE notifications (
    pk_notifications SERIAL PRIMARY KEY,
    message TEXT NOT NULL,
    fk_users INTEGER NOT NULL,
    notification_type CHARACTER VARYING  CHECK (notification_type IN ('TRANSACTION', 'PROMOTION', 'SECURITY')),
    status CHARACTER VARYING  CHECK (status IN ('SENT', 'READ')) DEFAULT 'SENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fk_users) REFERENCES users (pk_users) ON DELETE CASCADE
);

CREATE TABLE audits (
    pk_audit SERIAL PRIMARY KEY,
    action CHARACTER VARYING NOT NULL, -- Ação realizada (e.g., "CREATE_TRANSACTION")
    fk_users INTEGER,
    wallet_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metadata JSONB, -- Informações adicionais em JSON
    FOREIGN KEY (fk_users) REFERENCES users (pk_users) ON DELETE CASCADE,
    FOREIGN KEY (wallet_id) REFERENCES digital_wallets (pk_digital_wallets)
);

CREATE TABLE user_devices (
    pk_device SERIAL PRIMARY KEY,
    device_name CHARACTER VARYING NOT NULL,
    device_type CHARACTER VARYING  NOT NULL,
    last_used TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fk_users INTEGER NOT NULL,
    FOREIGN KEY (fk_users) REFERENCES users(pk_users) ON DELETE CASCADE
);

CREATE TABLE topup_tokens (
    pk_token SERIAL PRIMARY KEY,
    code CHARACTER VARYING UNIQUE NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    status CHARACTER VARYING  CHECK (status IN ('ACTIVE', 'REDEEMED', 'EXPIRED')) DEFAULT 'ACTIVE',
    fk_users INTEGER,
    FOREIGN KEY (fk_users) REFERENCES users(pk_users) ON DELETE SET NULL
);

CREATE TABLE system_settings (
    pk_setting SERIAL PRIMARY KEY,
    key CHARACTER VARYING UNIQUE NOT NULL,
    value TEXT NOT NULL
);

CREATE TABLE loyalty_points (
    pk_loyalty SERIAL PRIMARY KEY,
    fk_users INTEGER NOT NULL,
    points INTEGER DEFAULT 0,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fk_users) REFERENCES users(pk_users) ON DELETE CASCADE
);

CREATE TABLE transaction_fees (
    pk_fee SERIAL PRIMARY KEY,
    transaction_type CHARACTER VARYING  NOT NULL,
    fee_percentage NUMERIC(5, 2) NOT NULL,
    fixed_fee NUMERIC(15, 2) DEFAULT 0.0,
    currency CHARACTER VARYING  DEFAULT 'AKZ'
);

CREATE TABLE user_activity_logs (
    pk_activity SERIAL PRIMARY KEY,
    fk_users INTEGER NOT NULL,
    activity_type CHARACTER VARYING  NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (fk_users) REFERENCES users(pk_users) ON DELETE CASCADE
);

CREATE TABLE exchange_rates (
    pk_exchange SERIAL PRIMARY KEY,
    from_currency CHARACTER VARYING  NOT NULL,
    to_currency CHARACTER VARYING  NOT NULL,
    rate NUMERIC(15, 6) NOT NULL,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_tokens (
    pk_user_tokens SERIAL PRIMARY KEY,       -- Identificador único do token
    fk_users INT NOT NULL,                  -- Relacionado ao usuário
    token TEXT NOT NULL,                    -- Token de acesso ou atualização
    token_type CHARACTER VARYING  CHECK (token_type IN ('ACCESS', 'REFRESH', 'PASSWORD_RESET')) NOT NULL, -- Tipo do token
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Data de emissão do token
    expires_at TIMESTAMP,                   -- Data de expiração do token
    ip_address CHARACTER VARYING ,                 -- Endereço IP do usuário no momento da emissão
    user_agent TEXT,                        -- Informações sobre o dispositivo/navegador
    status CHARACTER VARYING  CHECK (status IN ('ACTIVE', 'EXPIRED', 'REVOKED')) DEFAULT 'ACTIVE', -- Status do token
    FOREIGN KEY (fk_users) REFERENCES users(pk_users) ON DELETE CASCADE
);

CREATE TABLE password_resets (
    pk_reset SERIAL PRIMARY KEY,            -- Identificador único do reset
    fk_users INT NOT NULL,                  -- Relacionado ao usuário
    reset_token TEXT NOT NULL,              -- Token de redefinição de senha
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Data de emissão do token
    expires_at TIMESTAMP,                   -- Data de expiração do token
    used BOOLEAN DEFAULT FALSE,             -- Status do uso do token
    FOREIGN KEY (fk_users) REFERENCES users(pk_users) ON DELETE CASCADE
);

CREATE TABLE security_logs (
    pk_log SERIAL PRIMARY KEY,              -- Identificador único do log
    fk_users INT,                           -- Relacionado ao usuário (pode ser NULL para tentativas anônimas)
    action CHARACTER VARYING  NOT NULL,           -- Ação realizada (e.g., "LOGIN_SUCCESS", "PASSWORD_RESET_REQUEST")
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Data e hora do evento
    ip_address CHARACTER VARYING ,                 -- Endereço IP associado ao evento
    user_agent TEXT,                        -- Informações sobre o dispositivo/navegador
    status CHARACTER VARYING  CHECK (status IN ('SUCCESS', 'FAILED')) NOT NULL, -- Resultado do evento
    FOREIGN KEY (fk_users) REFERENCES users(pk_users) ON DELETE SET NULL
);

CREATE TABLE login_attempts (
    pk_attempt SERIAL PRIMARY KEY,          -- Identificador único da tentativa
    fk_users INT,                           -- Relacionado ao usuário (pode ser NULL para usuários desconhecidos)
    email CHARACTER VARYING ,                     -- Email usado na tentativa
    phone CHARACTER VARYING ,                      -- Telefone usado na tentativa
    ip_address CHARACTER VARYING ,                 -- Endereço IP associado à tentativa
    user_agent TEXT,                        -- Informações sobre o dispositivo/navegador
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Data e hora da tentativa
    status CHARACTER VARYING  CHECK (status IN ('SUCCESS', 'FAILED')) NOT NULL, -- Resultado da tentativa
    FOREIGN KEY (fk_users) REFERENCES users(pk_users) ON DELETE SET NULL
);
