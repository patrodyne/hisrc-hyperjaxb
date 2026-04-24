-- HyperJAXB test schema: ejb_tests_punit_ext
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_punit_ext CASCADE;
CREATE SCHEMA ejb_tests_punit_ext;
GRANT ALL ON SCHEMA ejb_tests_punit_ext TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_punit_ext IS 'HyperJAXB test schema';
COMMIT;
