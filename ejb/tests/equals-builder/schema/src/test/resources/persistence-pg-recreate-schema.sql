-- HyperJAXB test schema: ejb_tests_equals_builder_schema
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_equals_builder_schema CASCADE;
CREATE SCHEMA ejb_tests_equals_builder_schema;
GRANT ALL ON SCHEMA ejb_tests_equals_builder_schema TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_equals_builder_schema IS 'HyperJAXB test schema';
COMMIT;
