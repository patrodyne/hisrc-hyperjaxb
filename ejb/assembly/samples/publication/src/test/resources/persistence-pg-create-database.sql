-- PostgreSQL
-- Create the HYPERJAXB database.

BEGIN;
CREATE ROLE hyperjaxb PASSWORD 'ChangeMe!' NOSUPERUSER NOCREATEDB NOCREATEROLE INHERIT LOGIN;
CREATE DATABASE hyperjaxb OWNER hyperjaxb ENCODING 'UTF8';
COMMIT;

SELECT datname, datdba, encoding as enc, datcollate, datctype, dattablespace as dts
  FROM pg_database
  WHERE datistemplate = false;
