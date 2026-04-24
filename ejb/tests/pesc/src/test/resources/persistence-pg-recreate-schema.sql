-- HyperJAXB test schema: ejb_tests_pesc
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_pesc CASCADE;
CREATE SCHEMA ejb_tests_pesc;
GRANT ALL ON SCHEMA ejb_tests_pesc TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_pesc IS 'HyperJAXB test schema';
COMMIT;
