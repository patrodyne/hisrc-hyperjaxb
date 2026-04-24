-- HyperJAXB test schema: ejb_tests_idsymbolspace
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_idsymbolspace CASCADE;
CREATE SCHEMA ejb_tests_idsymbolspace;
GRANT ALL ON SCHEMA ejb_tests_idsymbolspace TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_idsymbolspace IS 'HyperJAXB test schema';
COMMIT;
