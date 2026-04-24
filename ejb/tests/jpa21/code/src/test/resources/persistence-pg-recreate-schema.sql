-- HyperJAXB test schema: ejb_tests_jpa21
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_jpa21 CASCADE;
CREATE SCHEMA ejb_tests_jpa21;
GRANT ALL ON SCHEMA ejb_tests_jpa21 TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_jpa21 IS 'HyperJAXB test schema';
COMMIT;
