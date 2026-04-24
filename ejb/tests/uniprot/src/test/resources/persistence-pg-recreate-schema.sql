-- HyperJAXB test schema: ejb_tests_uniprot
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_uniprot CASCADE;
CREATE SCHEMA ejb_tests_uniprot;
GRANT ALL ON SCHEMA ejb_tests_uniprot TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_uniprot IS 'HyperJAXB test schema';
COMMIT;
