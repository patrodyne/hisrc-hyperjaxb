-- HyperJAXB test schema: ejb_tests_px
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_px CASCADE;
CREATE SCHEMA ejb_tests_px;
GRANT ALL ON SCHEMA ejb_tests_px TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_px IS 'HyperJAXB test schema';
COMMIT;
