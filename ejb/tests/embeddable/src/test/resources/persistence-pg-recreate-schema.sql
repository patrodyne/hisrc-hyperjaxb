-- HyperJAXB test schema: ejb_tests_embeddable
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_embeddable CASCADE;
CREATE SCHEMA ejb_tests_embeddable;
GRANT ALL ON SCHEMA ejb_tests_embeddable TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_embeddable IS 'HyperJAXB test schema';
COMMIT;
