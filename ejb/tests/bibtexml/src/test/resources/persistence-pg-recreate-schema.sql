-- HyperJAXB test schema: ejb_tests_bibtexml
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_bibtexml CASCADE;
CREATE SCHEMA ejb_tests_bibtexml;
GRANT ALL ON SCHEMA ejb_tests_bibtexml TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_bibtexml IS 'HyperJAXB test schema';
COMMIT;
