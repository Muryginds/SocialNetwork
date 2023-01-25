DROP TYPE IF EXISTS user_status CASCADE;
CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE');

DROP TYPE IF EXISTS message_permissions CASCADE;
CREATE TYPE message_permissions AS ENUM ('ALL', 'FRIENDS');
