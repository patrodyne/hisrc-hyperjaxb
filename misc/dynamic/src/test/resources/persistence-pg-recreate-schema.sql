-- HyperJAXB test schema: misc_dynamic
BEGIN;
DROP SCHEMA IF EXISTS misc_dynamic CASCADE;
CREATE SCHEMA misc_dynamic;
GRANT ALL ON SCHEMA misc_dynamic TO postgres, hyperjaxb;
COMMENT ON SCHEMA misc_dynamic IS 'HyperJAXB test schema';
COMMIT;
