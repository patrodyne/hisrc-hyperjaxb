-- HyperJAXB test schema: ejb_tests_enum
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_enum CASCADE;
CREATE SCHEMA ejb_tests_enum;
GRANT ALL ON SCHEMA ejb_tests_enum TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_enum IS 'HyperJAXB test schema';
COMMIT;
