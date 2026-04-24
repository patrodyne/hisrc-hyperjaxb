-- HyperJAXB test schema: ejb_tests_web
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_web CASCADE;
CREATE SCHEMA ejb_tests_web;
GRANT ALL ON SCHEMA ejb_tests_web TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_web IS 'HyperJAXB test schema';
COMMIT;
