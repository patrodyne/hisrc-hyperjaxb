-- HyperJAXB test schema: ejb_tests_dl
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_dl CASCADE;
CREATE SCHEMA ejb_tests_dl;
GRANT ALL ON SCHEMA ejb_tests_dl TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_dl IS 'HyperJAXB test schema';
COMMIT;
