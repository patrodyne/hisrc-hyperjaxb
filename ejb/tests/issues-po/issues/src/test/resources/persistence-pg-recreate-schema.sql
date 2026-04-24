-- HyperJAXB test schema: ejb_tests_issues
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_issues CASCADE;
CREATE SCHEMA ejb_tests_issues;
GRANT ALL ON SCHEMA ejb_tests_issues TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_issues IS 'HyperJAXB test schema';
COMMIT;
