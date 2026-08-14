INSERT IGNORE INTO roles (name)
VALUES
    ('ROLE_ADMIN'),
    ('ROLE_USER');

INSERT IGNORE INTO categories (name, description)
VALUES
    (
        'Software Engineering',
        'Programming, architecture, testing, and software design'
    ),
    (
        'Computer Science',
        'Algorithms, data structures, and computing theory'
    ),
    (
        'Databases',
        'Database design, SQL, and data management'
    ),
    (
        'Web Development',
        'Frontend and backend web technologies'
    ),
    (
        'Artificial Intelligence',
        'Machine learning, deep learning, and AI systems'
    );