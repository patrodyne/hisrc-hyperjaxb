-- HyperJAXB test schema: ejb_tests_xmldsig
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_xmldsig CASCADE;
CREATE SCHEMA ejb_tests_xmldsig;
GRANT ALL ON SCHEMA ejb_tests_xmldsig TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_xmldsig IS 'HyperJAXB test schema';
COMMIT;
