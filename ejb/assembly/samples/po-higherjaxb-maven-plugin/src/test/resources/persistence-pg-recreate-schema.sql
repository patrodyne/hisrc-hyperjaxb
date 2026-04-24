-- HyperJAXB test schema: ejb_samples_po_higherjaxb_maven_plugin
BEGIN;
DROP SCHEMA IF EXISTS ejb_samples_po_higherjaxb_maven_plugin CASCADE;
CREATE SCHEMA ejb_samples_po_higherjaxb_maven_plugin;
GRANT ALL ON SCHEMA ejb_samples_po_higherjaxb_maven_plugin TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_samples_po_higherjaxb_maven_plugin IS 'HyperJAXB test schema';
COMMIT;
