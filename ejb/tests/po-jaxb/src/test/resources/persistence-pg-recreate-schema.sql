-- HyperJAXB test schema: ejb_tests_po_jaxb
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_po_jaxb CASCADE;
CREATE SCHEMA ejb_tests_po_jaxb;
GRANT ALL ON SCHEMA ejb_tests_po_jaxb TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_po_jaxb IS 'HyperJAXB test schema';
COMMIT;
