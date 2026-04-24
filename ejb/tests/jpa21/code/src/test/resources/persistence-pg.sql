-- Stored Procedure: FETCH_EMP_COUNT
CREATE OR REPLACE PROCEDURE ejb_tests_jpa21.fetch_emp_count(INOUT cnt bigint) LANGUAGE plpgsql AS $$ BEGIN SELECT COUNT(*) FROM ejb_tests_jpa21.employees INTO cnt; END; $$;
-- Stored Procedure: FETCH_EMP_NAME
CREATE OR REPLACE PROCEDURE ejb_tests_jpa21.fetch_emp_name(INOUT emp_id bigint, INOUT emp_name varchar) LANGUAGE plpgsql AS $$ BEGIN SELECT e.id, e.last_name FROM ejb_tests_jpa21.employees e WHERE e.id = emp_id INTO emp_id, emp_name; END; $$;
