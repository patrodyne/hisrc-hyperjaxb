package org.example.jpa21;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.example.jpa21.model.Employee;
import org.example.jpa21.model.Organization;
import org.example.jpa21.model.Project;
import org.example.jpa21.other.EmployeeSummary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hyperjaxb.ejb.util.Provider;
import org.jvnet.hyperjaxb.ejb.util.ProviderDetector;
import org.jvnet.hyperjaxb.ejb.util.Transactional;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

public class Jpa21Test extends Context
{
	// Represents the test sample file.
	public static final String SAMPLE_ORG_FILE = "src/test/samples/Organization01.xml";
	// Represents the EntityManager per test.
	protected EntityManager em = null;

	@BeforeEach
	void setUp()
		throws Exception
	{
		em = createEntityManager();
		Organization organization = unmarshal(SAMPLE_ORG_FILE, Organization.class);
		for ( Project project : organization.getProjects() )
			project.tieEmployees();
		for ( Employee employee : organization.getEmployees() )
			employee.tieProjects();

		// EclipseLink may not drop tables, pass isNew=false
		persist(organization, false);

		getLogger().debug("setUp: Organization: {}", organization);
	}

	@AfterEach
	void tearDown()
		throws Exception
	{
		em.close();
		// EclipseLink drops tables when EMF is closed.
		//getEntityManagerFactory().close();
		//setEntityManagerFactory(null);
	}

	@Test
	void connect1()
	{
		// Prepare a transaction to select the Employee count.
		Transactional<Integer> tx = (tem) ->
		{
			Query query = tem.createQuery("select count(E) from Employee E");
			return ((Long) query.getSingleResult()).intValue();
		};
		Integer result = tx.transact(em);
		getLogger().info("connect1: Employee count is {}", result);
		assertEquals(8, result, "There are 8 employees");
	}

	@Test
	void execute1() throws IOException
	{
		// Select Employee(s) by id, if any, up to 10.
		List<Employee> employees = selectEmployees(em, 0, 10, "DE-ENG");
		assertEquals(2, employees.size(), "There are 2 employees in DE-ENG");
	}

	@Test
	void project1() throws IOException
	{
		Transactional<Employee> tx = (tem) ->
		{
			// Retrieve the named graph
			EntityGraph<?> graph = tem.getEntityGraph("Employee.projects");
			return tem.find(Employee.class, "EM-SMI01",
			    Map.of("jakarta.persistence.loadgraph", graph));
		};

		Employee employee  = tx.transact(em);
		getLogger().info("Employee: {}, {}", employee.getId(), employee.getLastName());
		for ( Project project : employee.getProjects())
			getLogger().info("  Project: {}, {}", project.getId(), project.getProjectName());
		assertEquals(2, employee.getProjects().size(), "There are 2 projects");
	}

	@Test
	void summary0() throws IOException
	{
		// Prepare a transaction call getEmployeeCount procedure.
		Transactional<Long> tx = (tem) ->
		{
			StoredProcedureQuery query =
				tem.createNamedStoredProcedureQuery("getEmployeeCount");
			// PG: query.setHint("escapeSyntaxCallMode", "call");
			query.setParameter(1, 0L);

			Provider provider = ProviderDetector.getProvider(em);
			// EclipseLink follows a strict, spec-first time line. It requires an explicit trigger event (execute())
			// to allocate resources and fetch data. If you skip execute(), the internal cursor/statement doesn't
			// exist yet, resulting in an immediate IllegalStateException.
			//
			// Hibernate uses a lazy, auto-executing architecture. If you call getOutputParameterValue() before
			// calling execute(), Hibernate implicitly runs the query for you under the hood. However, this means
			// Hibernate keeps the underlying JDBC statement open longer; if the statement or session is closed
			// before you read the value, it throws Object is already closed.
			if ( provider == Provider.ECLIPSELINK )
				query.execute();

			return (Long) query.getOutputParameterValue(1);
		};

		Long count = tx.transact(em);
		getLogger().info("Summary0: {}", count);
		assertEquals(8, count, "There are 8 employees");
	}

	// Employee Summary Mapping: NamedNativeQuery
	@Test
	void summary1() throws IOException
	{
		// Prepare a transaction call EmployeeSelectAll query.
		@SuppressWarnings("unchecked")
		Transactional<List<EmployeeSummary>> tx = (tem) ->
		{
			return tem.createNamedQuery("EmployeeSelectAll")
				.getResultList();
		};

		List<EmployeeSummary> summaries = tx.transact(em);
		for ( EmployeeSummary es : summaries )
			getLogger().info("Summary1: {}, {}", es.getId(), es.getName());
		assertEquals(8, summaries.size(), "There are 8 employees");
	}

//	@Test
//	@Disabled("PG works; H2 does not support named parameters")
	void summary2() throws IOException
	{
		Transactional<List<EmployeeSummary>> tx = (tem) ->
		{
			// 1. Create the query pointing to your procedure
			StoredProcedureQuery query = tem.createStoredProcedureQuery("ejb_tests_jpa21.fetch_emp_name");

			// 2. Register parameters (REF_CURSOR must be registered with void.class or the target Entity)
			// Dialect [org.hibernate.dialect.H2Dialect] not known to support REF_CURSOR parameters
			query.registerStoredProcedureParameter("emp_id", String.class, ParameterMode.IN);
			query.registerStoredProcedureParameter("emp_name", String.class, ParameterMode.IN);

			// 3. Set the input value(s)
			String empId = "EM-SMI01";
			String empName = "";
			query.setParameter("emp_id", empId);
			query.setParameter("emp_name", empName);

			// 4. Execute and retrieve results
			// ISSUE: getResultList() automatically handles (?) fetching from the REF_CURSOR in JPA 2.1

			List<EmployeeSummary> esList = new ArrayList<>();

			// Get the first result set or INOUT parameters.
			boolean hasResultSet = query.execute();
			if ( hasResultSet )
			{
				@SuppressWarnings("rawtypes")
				List results = query.getResultList();
				Object[] values = (Object[]) results.get(0);
				empId = (String) values[0];
				empName = (String) values[1];
			}
			else
			{
				empId = (String) query.getOutputParameterValue("emp_id");
				empName = (String) query.getOutputParameterValue("emp_name");
			}
			esList.add(new EmployeeSummary(empId, empName));

			return esList;
		};

		List<EmployeeSummary> summaries = tx.transact(em);
		for ( EmployeeSummary es : summaries )
			getLogger().info("Summary2: {}, {}", es.getId(), es.getName());
		assertEquals(1, summaries.size(), "There is 1 summary");
	}

	// Employee Summary Mapping: NamedStoredProcedureQuery (H2)
	@Test
	void summaryH2() throws IOException
	{
		if ( "org.h2.Driver".equals(getJDBCDriver()) )
		{
			// Prepare a transaction call EmployeeSelectAll query.
			@SuppressWarnings("unchecked")
			Transactional<List<EmployeeSummary>> tx = (tem) ->
			{
				StoredProcedureQuery query =
					tem.createNamedStoredProcedureQuery("getEmployeeDetailsH2");
				// IN Parameters
				String empId = "EM-SMI01";
				// Positional for H2 compatibility
				query.setParameter(1, empId);
				return query.getResultList();
			};
			List<EmployeeSummary> summaries = tx.transact(em);
			for ( EmployeeSummary es : summaries )
				getLogger().info("SummaryH2: {}, {}", es.getId(), es.getName());
			assertEquals(1, summaries.size(), "There is 1 summary");
		}
	}

	// Employee Summary Mapping: NamedStoredProcedureQuery (PG)
	@Test
	void summaryPG() throws IOException
	{
		if ( "org.postgresql.Driver".equals(getJDBCDriver()) )
		{
			// Prepare a transaction call EmployeeSelectAll query.
			Transactional<List<EmployeeSummary>> tx = (tem) ->
			{
				StoredProcedureQuery query =
					tem.createNamedStoredProcedureQuery("getEmployeeDetailsPG");
				// Set by db URL: query.setHint("escapeSyntaxCallMode", "call");

				// INOUT Parameters
				String empId = "EM-SMI01";
				String empName = "";
				// Positional for H2 compatibility
				query.setParameter(1, empId);
				query.setParameter(2, empName);

				List<EmployeeSummary> esList = new ArrayList<>();

				// Get the first result set or INOUT parameters.
				boolean hasResultSet = query.execute();
				if ( hasResultSet )
				{
					@SuppressWarnings("rawtypes")
					List results = query.getResultList();
					Object[] values = (Object[]) results.get(0);
					empId = (String) values[0];
					empName = (String) values[1];
				}
				else
				{
					empId = (String) query.getOutputParameterValue(1);
					empName = (String) query.getOutputParameterValue(2);
				}
				esList.add(new EmployeeSummary(empId, empName));

				return esList;
			};
			List<EmployeeSummary> summaries = tx.transact(em);

			for ( EmployeeSummary es : summaries )
				getLogger().info("SummaryPG: {}, {}", es.getId(), es.getName());
			assertEquals(1, summaries.size(), "There is 1 summary");
		}
	}

	/**
	 * Select a limited list of employee(es) for the given id.
	 *
	 * @param start The starting offset.
	 * @param count The count limit.
	 * @param department The department name.
	 *
	 * @return A list of employee(es).
	 *
	 * @throws IOException When the list cannot be selected.
	 */
	protected List<Employee> selectEmployees(EntityManager em, Integer start, Integer count, String department)
		throws IOException
	{
		return selectEmployeesTX(start, count, department).transact(em);
	}

	protected static Transactional<List<Employee>> selectEmployeesTX(Integer start, Integer count, String department)
	{
		// Always perform EntityManager actions within a transaction!
		Transactional<List<Employee>> tx = (tem) ->
		{
			CriteriaBuilder cb = tem.getCriteriaBuilder();
			CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);

			// Force eager loading of entities using an inner join.
			// See https://thorben-janssen.com/5-ways-to-initialize-lazy-relations-and-when-to-use-them/
			Root<Employee> fromEmployee = cq.from(Employee.class);
			fromEmployee.fetch("department", JoinType.INNER);

			cq.select(fromEmployee)
				.where(cb.equal(fromEmployee.get("department").get("id"), department));

			TypedQuery<Employee> query = tem.createQuery(cq);
			query.setHint("eclipselink.query-results-cache", false);
			query.setHint("org.hibernate.cacheable", false);
			List<Employee> entities = query
				.setFirstResult(start)
				.setMaxResults(count)
				.getResultList();

			return entities;
		};
		return tx;
	}

	// Persist an organization to trhe database.
	protected void persist(Organization entity, boolean isNew)
        throws IOException
    {
        Transactional<Integer> tx = (tem) ->
        {
            if ( isNew )
                tem.persist(entity);
            else
                tem.merge(entity);
            return 1;
        };

        Integer count = tx.transact(em);
        getLogger().info("Persisted {} {}(s)", count, Organization.class.getSimpleName());
    }

	// Persist employee(s) in a transaction.
    protected Transactional<Integer>  insertTX(String lable, List<Employee> entityList)
		throws IOException
	{
		// Prepare the transaction to persist or merge one batch.
		Transactional<Integer> tx = (tem) ->
		{
			for ( Employee entity : entityList )
				tem.persist(entity);
			return entityList.size();
		};
		return tx;
	}
}
