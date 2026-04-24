-- HyperJAXB test schema: ejb_tests_rim
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_rim CASCADE;
CREATE SCHEMA ejb_tests_rim;
GRANT ALL ON SCHEMA ejb_tests_rim TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_rim IS 'HyperJAXB test schema';
COMMIT;
