-- Insertion d'un utilisateur
INSERT INTO app_user (id, username, email) VALUES (1, 'mario', 'mario.luiz.miranda@gmail.com');

-- Insertion de transactions associées à l'utilisateur
INSERT INTO transaction (id, description, amount, date, type, user_id) VALUES (1, 'Salaire', 2000.00, '2023-10-01', 'income', 1);
INSERT INTO transaction (id, description, amount, date, type, user_id) VALUES (2, 'Loyer', 800.00, '2023-10-05', 'outcome', 1);
INSERT INTO transaction (id, description, amount, date, type, user_id) VALUES (3, 'Courses', 150.00, '2023-10-10', 'outcome', 1);