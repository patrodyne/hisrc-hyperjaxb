-- PostgreSQL
-- Drop and create the PUBLIC schema in HYPERJAXB database.

BEGIN;
DROP SCHEMA IF EXISTS "public" CASCADE;
CREATE SCHEMA "public";
GRANT ALL ON SCHEMA "public" TO "postgres", "hyperjaxb";
COMMENT ON SCHEMA "public" IS 'standard public schema';
COMMIT;

