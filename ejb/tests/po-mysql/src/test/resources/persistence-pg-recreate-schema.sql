-- HyperJAXB test schema: ejb_tests_po_mysql
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_po_mysql CASCADE;
CREATE SCHEMA ejb_tests_po_mysql;
GRANT ALL ON SCHEMA ejb_tests_po_mysql TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_po_mysql IS 'HyperJAXB test schema';
COMMIT;
