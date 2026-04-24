-- HyperJAXB test schema: ejb_tests_ims_lip
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_ims_lip CASCADE;
CREATE SCHEMA ejb_tests_ims_lip;
GRANT ALL ON SCHEMA ejb_tests_ims_lip TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_ims_lip IS 'HyperJAXB test schema';
COMMIT;
