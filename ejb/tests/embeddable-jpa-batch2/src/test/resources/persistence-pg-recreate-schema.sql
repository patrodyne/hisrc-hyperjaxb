-- HyperJAXB test schema: ejb_tests_embeddable_jpa_batch
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_embeddable_jpa_batch CASCADE;
CREATE SCHEMA ejb_tests_embeddable_jpa_batch;
GRANT ALL ON SCHEMA ejb_tests_embeddable_jpa_batch TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_embeddable_jpa_batch IS 'HyperJAXB test schema';
COMMIT;
