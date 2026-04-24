-- HyperJAXB test schema: ejb_tests_plmxml
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_plmxml CASCADE;
CREATE SCHEMA ejb_tests_plmxml;
GRANT ALL ON SCHEMA ejb_tests_plmxml TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_plmxml IS 'HyperJAXB test schema';
COMMIT;
