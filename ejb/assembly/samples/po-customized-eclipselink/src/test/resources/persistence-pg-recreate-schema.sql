-- HyperJAXB test schema: ejb_samples_po_customized_eclipselink
BEGIN;
DROP SCHEMA IF EXISTS ejb_samples_po_customized_eclipselink CASCADE;
CREATE SCHEMA ejb_samples_po_customized_eclipselink;
GRANT ALL ON SCHEMA ejb_samples_po_customized_eclipselink TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_samples_po_customized_eclipselink IS 'HyperJAXB test schema';
COMMIT;
