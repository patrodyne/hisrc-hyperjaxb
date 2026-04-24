-- HyperJAXB test schema: ejb_tests_ioda
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_ioda CASCADE;
CREATE SCHEMA ejb_tests_ioda;
GRANT ALL ON SCHEMA ejb_tests_ioda TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_ioda IS 'HyperJAXB test schema';
COMMIT;
