-- HyperJAXB test schema: ejb_tests_episodes_a
BEGIN;
DROP SCHEMA IF EXISTS ejb_tests_episodes_a CASCADE;
CREATE SCHEMA ejb_tests_episodes_a;
GRANT ALL ON SCHEMA ejb_tests_episodes_a TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_tests_episodes_a IS 'HyperJAXB test schema';
COMMIT;
