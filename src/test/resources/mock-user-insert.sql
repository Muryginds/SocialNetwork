WITH rows AS (
    INSERT INTO "user" (about, birth_date, city, confirmation_code, country, email, first_name, is_approved, is_blocked,
                        is_deleted, last_name, last_online_time, message_permission, password, phone, photo, reg_date,
                        status)
        VALUES (null, null, null, 'test', null, 'testAccount@hotmail.com', 'Серж', true,
                false, false, 'Богданов', '2022-01-27 17:58:18.048000', 'ALL',
                '$argon2id$v=19$m=4096,t=1,p=1$SWNGVlRHT1ZRTnNhS0dONA$urKNvix0im19T+rF1Tu5Rt77NyY4GheiKty/ILyb9oc',
                'test', null, '2022-01-27 17:58:18.048000', 'ACTIVE') RETURNING id)

INSERT INTO "user_to_role" (user_id, role_id)
    SELECT id, 1 FROM "rows";