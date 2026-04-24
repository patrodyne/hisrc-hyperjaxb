-- HyperJAXB test schema: ejb_tests_episodes_b
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_episodes_b CASCADE;
CREATE SCHEMA ejb_tests_episodes_b;
GRANT ALL ON SCHEMA ejb_tests_episodes_b TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_episodes_b IS 'HyperJAXB test schema';
COMMIT;
