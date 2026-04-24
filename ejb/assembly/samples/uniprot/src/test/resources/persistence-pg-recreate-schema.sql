-- HyperJAXB test schema: ejb_samples_uniprot
BEGIN;
DROP SCHEMA IF EXISTS ejb_samples_uniprot CASCADE;
CREATE SCHEMA ejb_samples_uniprot;
GRANT ALL ON SCHEMA ejb_samples_uniprot TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_samples_uniprot IS 'HyperJAXB test schema';
COMMIT;
