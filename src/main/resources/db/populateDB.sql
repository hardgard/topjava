DELETE
FROM user_roles;
DELETE
FROM meals;
DELETE
FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (user_id, id, date_time, description, calories)
VALUES (100001, 100006, TIMESTAMP '2021-10-19 11:23:54', 'Lunch', 1500),
       (100000, 100002, TIMESTAMP '2021-10-19 11:23:54', 'Braeakfast', 500),
       (100001, 100003, TIMESTAMP '2021-10-21 10:23:54', 'Dinner', 1500),
       (100001, 100004, TIMESTAMP '2021-10-22 10:23:54', 'Dinner', 1500);
