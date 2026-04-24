-- HyperJAXB test schema: ejb_tests_po
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_po CASCADE;
CREATE SCHEMA ejb_tests_po;
GRANT ALL ON SCHEMA ejb_tests_po TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_po IS 'HyperJAXB test schema';
COMMIT;
