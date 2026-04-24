-- HyperJAXB test schema: ejb_tests_custom_naming_schema
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_custom_naming_schema CASCADE;
CREATE SCHEMA ejb_tests_custom_naming_schema;
GRANT ALL ON SCHEMA ejb_tests_custom_naming_schema TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_custom_naming_schema IS 'HyperJAXB test schema';
COMMIT;
