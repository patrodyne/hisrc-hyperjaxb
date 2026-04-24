-- HyperJAXB test schema: ejb_tests_nokis
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_nokis CASCADE;
CREATE SCHEMA ejb_tests_nokis;
GRANT ALL ON SCHEMA ejb_tests_nokis TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_nokis IS 'HyperJAXB test schema';
COMMIT;
