-- HyperJAXB test schema: ejb_tests_ccr
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_ccr CASCADE;
CREATE SCHEMA ejb_tests_ccr;
GRANT ALL ON SCHEMA ejb_tests_ccr TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_ccr IS 'HyperJAXB test schema';
COMMIT;
