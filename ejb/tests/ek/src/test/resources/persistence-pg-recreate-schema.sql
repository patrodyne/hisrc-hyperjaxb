-- HyperJAXB test schema: ejb_tests_ek
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_ek CASCADE;
CREATE SCHEMA ejb_tests_ek;
GRANT ALL ON SCHEMA ejb_tests_ek TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_ek IS 'HyperJAXB test schema';
COMMIT;
