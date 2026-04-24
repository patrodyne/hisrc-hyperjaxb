-- HyperJAXB test schema: ejb_tests_verylong
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_verylong CASCADE;
CREATE SCHEMA ejb_tests_verylong;
GRANT ALL ON SCHEMA ejb_tests_verylong TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_verylong IS 'HyperJAXB test schema';
COMMIT;
