-- HyperJAXB test schema: ejb_explore_ex001_justproduct
BEGIN;
DROP SCHEMA IF EXISTS ejb_explore_ex001_justproduct CASCADE;
CREATE SCHEMA ejb_explore_ex001_justproduct;
GRANT ALL ON SCHEMA ejb_explore_ex001_justproduct TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_explore_ex001_justproduct IS 'HyperJAXB test schema';
COMMIT;
