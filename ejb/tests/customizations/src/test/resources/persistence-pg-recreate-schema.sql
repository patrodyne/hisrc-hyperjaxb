-- HyperJAXB test schema: ejb_tests_customizations
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_customizations CASCADE;
CREATE SCHEMA ejb_tests_customizations;
GRANT ALL ON SCHEMA ejb_tests_customizations TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_customizations IS 'HyperJAXB test schema';
COMMIT;
