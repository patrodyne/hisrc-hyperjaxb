-- HyperJAXB test schema: ejb_tutorials_po_step_one
BEGIN;
DROP SCHEMA IF EXISTS ejb_tutorials_po_step_one CASCADE;
CREATE SCHEMA ejb_tutorials_po_step_one;
GRANT ALL ON SCHEMA ejb_tutorials_po_step_one TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tutorials_po_step_one IS 'HyperJAXB test schema';
COMMIT;
