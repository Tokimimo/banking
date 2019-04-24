CREATE DATABASE banking_sys
    WITH 
    OWNER = banking_user
    ENCODING = 'UTF8'
    LC_COLLATE = 'German_Germany.1252'
    LC_CTYPE = 'German_Germany.1252'
    TABLESPACE = dbo
    CONNECTION LIMIT = -1;

GRANT ALL ON DATABASE banking_sys TO banking_user;