-- HyperJAXB test schema: ejb_tests_simple
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_simple CASCADE;
CREATE SCHEMA ejb_tests_simple;
GRANT ALL ON SCHEMA ejb_tests_simple TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_simple IS 'HyperJAXB test schema';
COMMIT;
