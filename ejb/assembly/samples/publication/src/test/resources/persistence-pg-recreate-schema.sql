-- HyperJAXB test schema: ejb_samples_po_initial
BEGIN;
DROP SCHEMA IF EXISTS ejb_samples_publication CASCADE;
CREATE SCHEMA ejb_samples_publication;
GRANT ALL ON SCHEMA ejb_samples_publication TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_samples_publication IS 'HyperJAXB test schema';
COMMIT;
