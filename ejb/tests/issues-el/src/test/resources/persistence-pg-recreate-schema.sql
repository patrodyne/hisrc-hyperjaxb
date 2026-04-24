-- HyperJAXB test schema: ejb_tests_issues_el
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_issues_el CASCADE;
CREATE SCHEMA ejb_tests_issues_el;
GRANT ALL ON SCHEMA ejb_tests_issues_el TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_issues_el IS 'HyperJAXB test schema';
COMMIT;
