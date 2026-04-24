-- HyperJAXB test schema: ejb_tests_anyelement
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_anyelement CASCADE;
CREATE SCHEMA ejb_tests_anyelement;
GRANT ALL ON SCHEMA ejb_tests_anyelement TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_anyelement IS 'HyperJAXB test schema';
COMMIT;
