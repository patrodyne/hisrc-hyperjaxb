-- HyperJAXB test schema: ejb_tests_fpml_pretrade
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_fpml_pretrade CASCADE;
CREATE SCHEMA ejb_tests_fpml_pretrade;
GRANT ALL ON SCHEMA ejb_tests_fpml_pretrade TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_fpml_pretrade IS 'HyperJAXB test schema';
COMMIT;
