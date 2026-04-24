-- HyperJAXB test schema: ejb_tests_onix30
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_onix30 CASCADE;
CREATE SCHEMA ejb_tests_onix30;
GRANT ALL ON SCHEMA ejb_tests_onix30 TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_onix30 IS 'HyperJAXB test schema';
COMMIT;
