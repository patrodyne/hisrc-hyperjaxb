-- HyperJAXB test schema: ejb_tests_ak
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_ak CASCADE;
CREATE SCHEMA ejb_tests_ak;
GRANT ALL ON SCHEMA ejb_tests_ak TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_ak IS 'HyperJAXB test schema';
COMMIT;
