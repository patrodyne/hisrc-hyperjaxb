-- HyperJAXB test schema: ejb_samples_root_header
BEGIN;
DROP SCHEMA IF EXISTS ejb_samples_root_header CASCADE;
CREATE SCHEMA ejb_samples_root_header;
GRANT ALL ON SCHEMA ejb_samples_root_header TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_samples_root_header IS 'HyperJAXB test schema';
COMMIT;
