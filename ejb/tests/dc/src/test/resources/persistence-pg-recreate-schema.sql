-- HyperJAXB test schema: ejb_tests_dc
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_dc CASCADE;
CREATE SCHEMA ejb_tests_dc;
GRANT ALL ON SCHEMA ejb_tests_dc TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_dc IS 'HyperJAXB test schema';
COMMIT;
