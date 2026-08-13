CREATE TABLE roles (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       name VARCHAR(50) NOT NULL,
                       PRIMARY KEY (id),
                       CONSTRAINT uk_roles_name UNIQUE (name)
);

CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       email VARCHAR(150) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       display_name VARCHAR(100) NOT NULL,
                       enabled BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                           ON UPDATE CURRENT_TIMESTAMP,
                       PRIMARY KEY (id),
                       CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            CONSTRAINT fk_user_roles_user
                                FOREIGN KEY (user_id) REFERENCES users (id)
                                    ON DELETE CASCADE,
                            CONSTRAINT fk_user_roles_role
                                FOREIGN KEY (role_id) REFERENCES roles (id)
                                    ON DELETE RESTRICT
);

CREATE TABLE categories (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            name VARCHAR(100) NOT NULL,
                            description VARCHAR(500),
                            PRIMARY KEY (id),
                            CONSTRAINT uk_categories_name UNIQUE (name)
);

CREATE TABLE books (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       title VARCHAR(200) NOT NULL,
                       author VARCHAR(150) NOT NULL,
                       isbn VARCHAR(20) NOT NULL,
                       category_id BIGINT NOT NULL,
                       total_quantity INT NOT NULL,
                       available_quantity INT NOT NULL,
                       archived BOOLEAN NOT NULL DEFAULT FALSE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                           ON UPDATE CURRENT_TIMESTAMP,
                       PRIMARY KEY (id),
                       CONSTRAINT uk_books_isbn UNIQUE (isbn),
                       CONSTRAINT fk_books_category
                           FOREIGN KEY (category_id) REFERENCES categories (id)
                               ON DELETE RESTRICT,
                       CONSTRAINT chk_books_total_quantity
                           CHECK (total_quantity >= 0),
                       CONSTRAINT chk_books_available_quantity
                           CHECK (
                               available_quantity >= 0
                                   AND available_quantity <= total_quantity
                               )
);

CREATE TABLE loans (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       user_id BIGINT NOT NULL,
                       book_id BIGINT NOT NULL,
                       issued_at DATETIME NOT NULL,
                       due_at DATETIME NOT NULL,
                       returned_at DATETIME,
                       status VARCHAR(30) NOT NULL,
                       PRIMARY KEY (id),
                       CONSTRAINT fk_loans_user
                           FOREIGN KEY (user_id) REFERENCES users (id)
                               ON DELETE RESTRICT,
                       CONSTRAINT fk_loans_book
                           FOREIGN KEY (book_id) REFERENCES books (id)
                               ON DELETE RESTRICT,
                       CONSTRAINT chk_loans_status
                           CHECK (status IN ('ACTIVE', 'RETURNED', 'OVERDUE')),
                       CONSTRAINT chk_loans_due_date
                           CHECK (due_at > issued_at),
                       CONSTRAINT chk_loans_return_date
                           CHECK (returned_at IS NULL OR returned_at >= issued_at)
);

CREATE INDEX idx_loans_user_status
    ON loans (user_id, status);

CREATE INDEX idx_loans_book_status
    ON loans (book_id, status);

CREATE INDEX idx_loans_due_at
    ON loans (due_at);

CREATE TABLE fines (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       loan_id BIGINT NOT NULL,
                       amount DECIMAL(10, 2) NOT NULL,
                       overdue_days INT NOT NULL,
                       status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       paid_at DATETIME,
                       PRIMARY KEY (id),
                       CONSTRAINT uk_fines_loan UNIQUE (loan_id),
                       CONSTRAINT fk_fines_loan
                           FOREIGN KEY (loan_id) REFERENCES loans (id)
                               ON DELETE RESTRICT,
                       CONSTRAINT chk_fines_amount
                           CHECK (amount >= 0),
                       CONSTRAINT chk_fines_overdue_days
                           CHECK (overdue_days >= 0),
                       CONSTRAINT chk_fines_status
                           CHECK (status IN ('UNPAID', 'PAID')),
                       CONSTRAINT chk_fines_paid_at
                           CHECK (
                               (status = 'UNPAID' AND paid_at IS NULL)
                                   OR
                               (status = 'PAID' AND paid_at IS NOT NULL)
                               )
);

CREATE TABLE reservations (
                              id BIGINT NOT NULL AUTO_INCREMENT,
                              user_id BIGINT NOT NULL,
                              book_id BIGINT NOT NULL,
                              status VARCHAR(30) NOT NULL DEFAULT 'WAITING',
                              reserved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              available_until DATETIME,
                              fulfilled_at DATETIME,
                              cancelled_at DATETIME,
                              PRIMARY KEY (id),
                              CONSTRAINT fk_reservations_user
                                  FOREIGN KEY (user_id) REFERENCES users (id)
                                      ON DELETE RESTRICT,
                              CONSTRAINT fk_reservations_book
                                  FOREIGN KEY (book_id) REFERENCES books (id)
                                      ON DELETE RESTRICT,
                              CONSTRAINT chk_reservations_status
                                  CHECK (
                                      status IN (
                                                 'WAITING',
                                                 'AVAILABLE',
                                                 'FULFILLED',
                                                 'CANCELLED',
                                                 'EXPIRED'
                                          )
                                      )
);

CREATE INDEX idx_reservations_book_queue
    ON reservations (book_id, status, reserved_at);

CREATE INDEX idx_reservations_user_status
    ON reservations (user_id, status);

CREATE TABLE contact_messages (
                                  id BIGINT NOT NULL AUTO_INCREMENT,
                                  user_id BIGINT NOT NULL,
                                  subject VARCHAR(150) NOT NULL,
                                  message TEXT NOT NULL,
                                  status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  resolved_at DATETIME,
                                  PRIMARY KEY (id),
                                  CONSTRAINT fk_contact_messages_user
                                      FOREIGN KEY (user_id) REFERENCES users (id)
                                          ON DELETE RESTRICT,
                                  CONSTRAINT chk_contact_messages_status
                                      CHECK (status IN ('OPEN', 'RESOLVED'))
);