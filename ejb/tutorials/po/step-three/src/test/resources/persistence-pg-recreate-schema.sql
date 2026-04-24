-- HyperJAXB test schema: ejb_tutorials_po_step_three
BEGIN;
DROP SCHEMA IF EXISTS ejb_tutorials_po_step_three CASCADE;
CREATE SCHEMA ejb_tutorials_po_step_three;
GRANT ALL ON SCHEMA ejb_tutorials_po_step_three TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tutorials_po_step_three IS 'HyperJAXB test schema';
COMMIT;
