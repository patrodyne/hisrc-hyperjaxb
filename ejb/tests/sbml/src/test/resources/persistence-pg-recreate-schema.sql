-- HyperJAXB test schema: ejb_tests_sbml
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_sbml CASCADE;
CREATE SCHEMA ejb_tests_sbml;
GRANT ALL ON SCHEMA ejb_tests_sbml TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_sbml IS 'HyperJAXB test schema';
COMMIT;
