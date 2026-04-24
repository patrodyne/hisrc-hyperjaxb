-- HyperJAXB test schema: ejb_tests_device
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_device CASCADE;
CREATE SCHEMA ejb_tests_device;
GRANT ALL ON SCHEMA ejb_tests_device TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_device IS 'HyperJAXB test schema';
COMMIT;
