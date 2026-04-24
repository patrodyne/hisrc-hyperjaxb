-- HyperJAXB test schema: ejb_tests_star
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_star CASCADE;
CREATE SCHEMA ejb_tests_star;
GRANT ALL ON SCHEMA ejb_tests_star TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_star IS 'HyperJAXB test schema';
COMMIT;
