-- HyperJAXB test schema: ejb_tests_xacml
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_xacml CASCADE;
CREATE SCHEMA ejb_tests_xacml;
GRANT ALL ON SCHEMA ejb_tests_xacml TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_xacml IS 'HyperJAXB test schema';
COMMIT;
