-- HyperJAXB test schema: ejb_tests_one
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_one CASCADE;
CREATE SCHEMA ejb_tests_one;
GRANT ALL ON SCHEMA ejb_tests_one TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_one IS 'HyperJAXB test schema';
COMMIT;
