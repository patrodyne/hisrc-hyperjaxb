-- HyperJAXB test schema: ejb_tests_ids
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_ids CASCADE;
CREATE SCHEMA ejb_tests_ids;
GRANT ALL ON SCHEMA ejb_tests_ids TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_ids IS 'HyperJAXB test schema';
COMMIT;
