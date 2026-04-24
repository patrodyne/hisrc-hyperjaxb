-- HyperJAXB test schema: ejb_tests_cda
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_cda CASCADE;
CREATE SCHEMA ejb_tests_cda;
GRANT ALL ON SCHEMA ejb_tests_cda TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_cda IS 'HyperJAXB test schema';
COMMIT;
