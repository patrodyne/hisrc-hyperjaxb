CREATE SCHEMA IF NOT EXISTS ejb_tests_jpa21;
CREATE ALIAS  IF NOT EXISTS ejb_tests_jpa21.FETCH_EMP_COUNT FOR "org.example.jpa21.other.H2Procedures.fetchEmployeeCount";
CREATE ALIAS  IF NOT EXISTS ejb_tests_jpa21.FETCH_EMP_NAME  FOR "org.example.jpa21.other.H2Procedures.fetchEmployeeName";
