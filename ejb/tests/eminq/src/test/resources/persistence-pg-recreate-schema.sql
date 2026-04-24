-- HyperJAXB test schema: ejb_tests_eminq
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_eminq CASCADE;
CREATE SCHEMA ejb_tests_eminq;
GRANT ALL ON SCHEMA ejb_tests_eminq TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_eminq IS 'HyperJAXB test schema';
COMMIT;
