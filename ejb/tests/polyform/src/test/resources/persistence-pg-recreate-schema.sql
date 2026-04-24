-- HyperJAXB test schema: ejb_tests_polyform
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_polyform CASCADE;
CREATE SCHEMA ejb_tests_polyform;
GRANT ALL ON SCHEMA ejb_tests_polyform TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_polyform IS 'HyperJAXB test schema';
COMMIT;
