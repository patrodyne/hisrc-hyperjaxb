-- HyperJAXB test schema: ejb_tests_sml
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_sml CASCADE;
CREATE SCHEMA ejb_tests_sml;
GRANT ALL ON SCHEMA ejb_tests_sml TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_sml IS 'HyperJAXB test schema';
COMMIT;
