-- HyperJAXB test schema: ejb_tests_tp
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_tp CASCADE;
CREATE SCHEMA ejb_tests_tp;
GRANT ALL ON SCHEMA ejb_tests_tp TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_tp IS 'HyperJAXB test schema';
COMMIT;
