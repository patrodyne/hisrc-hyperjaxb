-- HyperJAXB test schema: ejb_tests_dom
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_dom CASCADE;
CREATE SCHEMA ejb_tests_dom;
GRANT ALL ON SCHEMA ejb_tests_dom TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_dom IS 'HyperJAXB test schema';
COMMIT;
