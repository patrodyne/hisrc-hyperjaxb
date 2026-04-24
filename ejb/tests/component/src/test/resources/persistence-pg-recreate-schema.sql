-- HyperJAXB test schema: ejb_tests_component
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_component CASCADE;
CREATE SCHEMA ejb_tests_component;
GRANT ALL ON SCHEMA ejb_tests_component TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_component IS 'HyperJAXB test schema';
COMMIT;
