-- HyperJAXB test schema: ejb_tests_nml
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_nml CASCADE;
CREATE SCHEMA ejb_tests_nml;
GRANT ALL ON SCHEMA ejb_tests_nml TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_nml IS 'HyperJAXB test schema';
COMMIT;
