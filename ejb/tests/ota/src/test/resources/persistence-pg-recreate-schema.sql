-- HyperJAXB test schema: ejb_tests_ota
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_ota CASCADE;
CREATE SCHEMA ejb_tests_ota;
GRANT ALL ON SCHEMA ejb_tests_ota TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_ota IS 'HyperJAXB test schema';
COMMIT;
