CREATE USER banking_user WITH
	LOGIN
	NOSUPERUSER
	CREATEDB
	NOCREATEROLE
	INHERIT
	NOREPLICATION
	CONNECTION LIMIT -1
	PASSWORD 'banking_user';

GRANT pg_write_server_files TO banking_user;