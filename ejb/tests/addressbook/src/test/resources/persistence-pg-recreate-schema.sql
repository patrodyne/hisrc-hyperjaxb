-- HyperJAXB test schema: ejb_tests_addressbook
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_addressbook CASCADE;
CREATE SCHEMA ejb_tests_addressbook;
GRANT ALL ON SCHEMA ejb_tests_addressbook TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_addressbook IS 'HyperJAXB test schema';
COMMIT;
