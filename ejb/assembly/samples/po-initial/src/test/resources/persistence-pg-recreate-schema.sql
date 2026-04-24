-- HyperJAXB test schema: ejb_samples_po_initial
BEGIN;
DROP SCHEMA IF EXISTS ejb_samples_po_initial CASCADE;
CREATE SCHEMA ejb_samples_po_initial;
GRANT ALL ON SCHEMA ejb_samples_po_initial TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_samples_po_initial IS 'HyperJAXB test schema';
COMMIT;
