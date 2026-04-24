-- HyperJAXB test schema: ejb_tests_ims_eportfolio
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_ims_eportfolio CASCADE;
CREATE SCHEMA ejb_tests_ims_eportfolio;
GRANT ALL ON SCHEMA ejb_tests_ims_eportfolio TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_ims_eportfolio IS 'HyperJAXB test schema';
COMMIT;
