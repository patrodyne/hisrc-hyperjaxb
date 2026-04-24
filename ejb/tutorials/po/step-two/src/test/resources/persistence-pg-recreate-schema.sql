-- HyperJAXB test schema: ejb_tutorials_po_step_two
BEGIN;
DROP SCHEMA IF EXISTS ejb_tutorials_po_step_two CASCADE;
CREATE SCHEMA ejb_tutorials_po_step_two;
GRANT ALL ON SCHEMA ejb_tutorials_po_step_two TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tutorials_po_step_two IS 'HyperJAXB test schema';
COMMIT;
