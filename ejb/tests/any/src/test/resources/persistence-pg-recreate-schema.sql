-- HyperJAXB test schema: ejb_tests_any
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_any CASCADE;
CREATE SCHEMA ejb_tests_any;
GRANT ALL ON SCHEMA ejb_tests_any TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_any IS 'HyperJAXB test schema';
COMMIT;
