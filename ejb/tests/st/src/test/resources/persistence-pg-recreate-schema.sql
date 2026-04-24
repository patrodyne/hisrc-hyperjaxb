-- HyperJAXB test schema: ejb_tests_st
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_st CASCADE;
CREATE SCHEMA ejb_tests_st;
GRANT ALL ON SCHEMA ejb_tests_st TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_st IS 'HyperJAXB test schema';
COMMIT;
