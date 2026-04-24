-- HyperJAXB test schema: ejb_tests_regrep
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_regrep CASCADE;
CREATE SCHEMA ejb_tests_regrep;
GRANT ALL ON SCHEMA ejb_tests_regrep TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_regrep IS 'HyperJAXB test schema';
COMMIT;
