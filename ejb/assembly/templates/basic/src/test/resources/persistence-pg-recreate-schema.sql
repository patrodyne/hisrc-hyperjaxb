-- HyperJAXB test schema: ejb_templates_basic
BEGIN;
DROP SCHEMA IF EXISTS ejb_templates_basic CASCADE;
CREATE SCHEMA ejb_templates_basic;
GRANT ALL ON SCHEMA ejb_templates_basic TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_templates_basic IS 'HyperJAXB test schema';
COMMIT;
