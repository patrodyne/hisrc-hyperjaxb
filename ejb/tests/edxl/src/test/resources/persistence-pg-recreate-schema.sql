-- HyperJAXB test schema: ejb_tests_edxl
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_edxl CASCADE;
CREATE SCHEMA ejb_tests_edxl;
GRANT ALL ON SCHEMA ejb_tests_edxl TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_edxl IS 'HyperJAXB test schema';
COMMIT;
