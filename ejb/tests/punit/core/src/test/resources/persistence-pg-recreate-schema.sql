-- HyperJAXB test schema: ejb_tests_punit_core
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_punit_core CASCADE;
CREATE SCHEMA ejb_tests_punit_core;
GRANT ALL ON SCHEMA ejb_tests_punit_core TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_punit_core IS 'HyperJAXB test schema';
COMMIT;
