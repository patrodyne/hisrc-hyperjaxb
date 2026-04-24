-- HyperJAXB test schema: ejb_tests_po_el_customized
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_po_el_customized CASCADE;
CREATE SCHEMA ejb_tests_po_el_customized;
GRANT ALL ON SCHEMA ejb_tests_po_el_customized TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_po_el_customized IS 'HyperJAXB test schema';
COMMIT;
