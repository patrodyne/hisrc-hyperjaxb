-- HyperJAXB test schema: ejb_tests_ebxmlrr
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_ebxmlrr CASCADE;
CREATE SCHEMA ejb_tests_ebxmlrr;
GRANT ALL ON SCHEMA ejb_tests_ebxmlrr TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_ebxmlrr IS 'HyperJAXB test schema';
COMMIT;
