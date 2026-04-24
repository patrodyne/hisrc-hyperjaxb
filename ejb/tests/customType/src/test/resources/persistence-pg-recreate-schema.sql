-- HyperJAXB test schema: ejb_tests_customtype
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_customtype CASCADE;
CREATE SCHEMA ejb_tests_customtype;
GRANT ALL ON SCHEMA ejb_tests_customtype TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_customtype IS 'HyperJAXB test schema';
COMMIT;
