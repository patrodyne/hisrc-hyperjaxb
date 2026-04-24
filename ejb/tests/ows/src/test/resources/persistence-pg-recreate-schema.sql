-- HyperJAXB test schema: ejb_tests_ows
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_ows CASCADE;
CREATE SCHEMA ejb_tests_ows;
GRANT ALL ON SCHEMA ejb_tests_ows TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_ows IS 'HyperJAXB test schema';
COMMIT;
