-- HyperJAXB test schema: ejb_roundtrip
BEGIN;
DROP SCHEMA IF EXISTS ejb_roundtrip CASCADE;
CREATE SCHEMA ejb_roundtrip;
GRANT ALL ON SCHEMA ejb_roundtrip TO postgres, hyperjaxb;
COMMENT ON SCHEMA ejb_roundtrip IS 'HyperJAXB test schema';
COMMIT;
