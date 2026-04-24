-- HyperJAXB test schema: ejb_tests_choice
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_choice CASCADE;
CREATE SCHEMA ejb_tests_choice;
GRANT ALL ON SCHEMA ejb_tests_choice TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_choice IS 'HyperJAXB test schema';
COMMIT;
