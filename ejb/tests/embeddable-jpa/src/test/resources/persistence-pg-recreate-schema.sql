-- HyperJAXB test schema: ejb_tests_embeddable_jpa
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_embeddable_jpa CASCADE;
CREATE SCHEMA ejb_tests_embeddable_jpa;
GRANT ALL ON SCHEMA ejb_tests_embeddable_jpa TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_embeddable_jpa IS 'HyperJAXB test schema';
COMMIT;
