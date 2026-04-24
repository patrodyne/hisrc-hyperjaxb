-- HyperJAXB test schema: ejb_tests_publication
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_publication CASCADE;
CREATE SCHEMA ejb_tests_publication;
GRANT ALL ON SCHEMA ejb_tests_publication TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_publication IS 'HyperJAXB test schema';
COMMIT;
