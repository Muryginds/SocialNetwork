delete
from "post";

INSERT INTO "user" (id, about, birth_date, city, confirmation_code, country, email, first_name, is_approved, is_blocked,
                    is_deleted, last_name, last_online_time, password, phone, photo, reg_date, status)
VALUES (2, null, null, null, 'test', null, 'testAccount2@hotmail.com', 'Боря', true,
        false, false, 'Богданов', '2022-01-27 17:58:18.048000',
        '$argon2id$v=19$m=4096,t=1,p=1$SWNGVlRHT1ZRTnNhS0dONA$urKNvix0im19T+rF1Tu5Rt77NyY4GheiKty/ILyb9oc',
        'test', null, '2022-01-27 17:58:18.048000', 'ACTIVE');

insert into "post" (id, is_blocked, is_deleted, post_text, time, title, update_date, author_id)
values (1, false, false, 'Как здорово жить в Пало-Альто', '2020-03-25 00:55:51.024', 'Первый пост',
        '2023-02-19 21:57:38.297', 1),
       (2, false, false, 'Не заряжается телефон!', '2022-01-06 08:50:43.573', 'Второй пост', '2023-02-19 21:57:38.302',
        1),
       (3, false, false, 'Завтра будет выходной', '2021-03-23 18:30:03.268', 'Третий пост', '2023-02-19 21:57:38.306',
        1),
       (4, false, false, 'Кошка опять сходила в туалет', '2022-11-20 11:57:50.096', 'Четвертый пост',
        '2023-02-19 21:57:38.316', 2),
       (5, false, true, 'Удаленный пост, который нужно восстановить', '2022-11-20 11:57:50.096', 'Пятый пост',
        '2023-02-19 21:57:38.316', 1),
       (6, false, true, 'Удаленный пост другого пользователя', '2022-11-20 11:57:50.096', 'Шестой пост',
        '2023-02-19 21:57:38.316', 2)