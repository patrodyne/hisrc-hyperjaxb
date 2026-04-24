-- HyperJAXB test schema: ejb_tests_issues_jpa
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_issues_jpa CASCADE;
CREATE SCHEMA ejb_tests_issues_jpa;
GRANT ALL ON SCHEMA ejb_tests_issues_jpa TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_issues_jpa IS 'HyperJAXB test schema';
COMMIT;
