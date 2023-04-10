WITH
    user_insert AS (
    INSERT INTO "user" (about, birth_date, city, confirmation_code, country, email, first_name, is_approved, is_blocked,
                        is_deleted, last_name, last_online_time, password, phone, photo, reg_date, status)
    VALUES (null, '1995-01-27 17:58:18.048000', 'New York', 'test', 'USA', 'johndoe1@example.com', 'Johnny', true,
                false, false, 'Doe', '2023-01-27 17:58:18.048000',
                'johndoe1@example.com', 'test', null, '2020-01-27 17:58:18.048000', 'ACTIVE') RETURNING id),
    tag_insert AS (
         INSERT INTO "tag" (tag) VALUES ('test_tag1') RETURNING id),
    post_insert AS (
         INSERT INTO "post" (is_blocked, is_deleted, post_text, "time", title, update_date, author_id)
         SELECT false, false, 'test1 post some text text text',
                to_timestamp('2023-03-27 17:58:18.048000', 'YYYY-MM-DD hh24:mi:ss')::timestamp,
                'test title', to_timestamp('2023-03-27 17:58:18.048000', 'YYYY-MM-DD hh24:mi:ss')::timestamp,
                id FROM user_insert UNION ALL
         SELECT false, false, 'test2 post some text text text',
                to_timestamp('2023-03-27 17:58:18.048000', 'YYYY-MM-DD hh24:mi:ss')::timestamp,
                'test title', to_timestamp('2023-03-27 17:58:18.048000', 'YYYY-MM-DD hh24:mi:ss')::timestamp,
                id FROM user_insert RETURNING id)

INSERT INTO post_to_tag (post_id, tag_id)
SELECT post_insert.id, tag_insert.id FROM post_insert, tag_insert;

WITH rows AS (
    INSERT INTO "user" (about, birth_date, city, confirmation_code, country, email, first_name, is_approved, is_blocked,
                        is_deleted, last_name, last_online_time, password, phone, photo, reg_date, status)
        VALUES (null, '1995-01-27 17:58:18.048000', 'New York', 'test', 'USA', 'johndoe2@example.com', 'Johnatan', true,
                false, false, 'Doe', '2023-01-27 17:58:18.048000',
                'johndoe2@example.com', 'test', null, '2020-01-27 17:58:18.048000', 'ACTIVE') RETURNING id)

INSERT INTO "post" (is_blocked, is_deleted, post_text, "time", title, update_date, author_id)
SELECT false, false, 'test2 post some text post text text text', '2023-03-27 07:58:18.048000', 'test title',
       '2023-03-27', id FROM "rows";

WITH rows AS (
    INSERT INTO "user" (about, birth_date, city, confirmation_code, country, email, first_name, is_approved, is_blocked,
                        is_deleted, last_name, last_online_time, password, phone, photo, reg_date, status)
        VALUES (null, '1995-01-27 17:58:18.048000', 'Novosibirsk', 'test', 'Russia', 'jenya@example.com', 'Jenya',
                true, false, false, 'Boy', '2023-01-27 17:58:18.048000',
                'jenya@example.com', 'test', null, '2020-01-27 17:58:18.048000', 'ACTIVE') RETURNING id)

INSERT INTO "post" (is_blocked, is_deleted, post_text, "time", title, update_date, author_id)
SELECT false, false, 'test3 post text text text text', '2023-02-22 07:58:18.048000', 'test title',
       '2023-03-27', id FROM "rows";