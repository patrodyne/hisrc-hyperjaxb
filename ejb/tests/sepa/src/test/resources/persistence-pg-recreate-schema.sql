-- HyperJAXB test schema: ejb_tests_sepa
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_sepa CASCADE;
CREATE SCHEMA ejb_tests_sepa;
GRANT ALL ON SCHEMA ejb_tests_sepa TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_sepa IS 'HyperJAXB test schema';
COMMIT;
