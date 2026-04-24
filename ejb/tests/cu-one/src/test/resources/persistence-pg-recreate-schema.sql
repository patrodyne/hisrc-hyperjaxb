-- HyperJAXB test schema: ejb_tests_cu_one
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_cu_one CASCADE;
CREATE SCHEMA ejb_tests_cu_one;
GRANT ALL ON SCHEMA ejb_tests_cu_one TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_cu_one IS 'HyperJAXB test schema';
COMMIT;
