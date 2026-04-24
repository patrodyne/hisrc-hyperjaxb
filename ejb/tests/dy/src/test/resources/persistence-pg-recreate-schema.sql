-- HyperJAXB test schema: ejb_tests_dy
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_dy CASCADE;
CREATE SCHEMA ejb_tests_dy;
GRANT ALL ON SCHEMA ejb_tests_dy TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_dy IS 'HyperJAXB test schema';
COMMIT;
