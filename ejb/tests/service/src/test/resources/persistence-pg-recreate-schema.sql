-- HyperJAXB test schema: ejb_tests_service
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_service CASCADE;
CREATE SCHEMA ejb_tests_service;
GRANT ALL ON SCHEMA ejb_tests_service TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_service IS 'HyperJAXB test schema';
COMMIT;
