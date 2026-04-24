-- HyperJAXB test schema: ejb_tests_derby
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_derby CASCADE;
CREATE SCHEMA ejb_tests_derby;
GRANT ALL ON SCHEMA ejb_tests_derby TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_derby IS 'HyperJAXB test schema';
COMMIT;
