-- HyperJAXB test schema: ejb_tests_floating
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_floating CASCADE;
CREATE SCHEMA ejb_tests_floating;
GRANT ALL ON SCHEMA ejb_tests_floating TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_floating IS 'HyperJAXB test schema';
COMMIT;
