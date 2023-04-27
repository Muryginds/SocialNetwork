WITH
    user_recipient AS (
INSERT INTO public."user" (about, birth_date, city, confirmation_code, country, email, first_name, is_approved, is_blocked,
                           is_deleted, last_name, last_online_time, password, phone, photo, reg_date, status)
VALUES (null, '1995-01-27 17:58:18.48000', 'New York', 'test', 'USA', 'johndoe1@example1.com', 'Johnny', true,
    false, false, 'Doe', '2023-01-27 17:58:18.48000',
    'johndoe1@example1.com', 'test', null, '2020-01-27 17:58:18.48000', 'ACTIVE') RETURNING id),
    user_sender AS (
INSERT INTO public."user" (about, birth_date, city, confirmation_code, country, email, first_name, is_approved, is_blocked,
                           is_deleted, last_name, last_online_time, password, phone, photo, reg_date, status)
VALUES (null, '1995-01-27 17:58:18.48000', 'New York', 'test', 'USA', 'johndoe1@example2.com', 'Johnny', true,
    false, false, 'Doe', '2023-01-27 17:58:18.48000',
    'johndoe1@example2.com', 'test', null, '2020-01-27 17:58:18.48000', 'ACTIVE') RETURNING id),
    id_dialog AS (
INSERT INTO public."dialog" (recipient_id, sender_id)
select user_recipient.id, user_sender.id from user_recipient,user_sender RETURNING id
    ),
    Message AS (
insert into public.Message(message_text,"read_status",sent_time,dialog_id,author_id)
select 'текст1SENT','SENT', '2023-01-27 17:58:18.48000', id_dialog.id, user_recipient.id
from id_dialog, user_recipient)
insert into public.Message(message_text,"read_status",sent_time,dialog_id,author_id)
select 'текст2READ','READ', '2023-01-27 17:58:18.48000', id_dialog.id, user_recipient.id
from id_dialog, user_recipient;