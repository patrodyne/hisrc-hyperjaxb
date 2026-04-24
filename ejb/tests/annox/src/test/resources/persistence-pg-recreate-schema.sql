-- HyperJAXB test schema: ejb_tests_annox
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_annox CASCADE;
CREATE SCHEMA ejb_tests_annox;
GRANT ALL ON SCHEMA ejb_tests_annox TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_annox IS 'HyperJAXB test schema';
COMMIT;
