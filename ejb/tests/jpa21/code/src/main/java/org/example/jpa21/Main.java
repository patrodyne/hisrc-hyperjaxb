package org.example.jpa21;

import static java.lang.String.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.example.jpa21.model.Department;
import org.example.jpa21.model.Employee;
import org.example.jpa21.model.Project;
import org.example.jpa21.other.EmployeeSummary;
import org.jvnet.hyperjaxb.ejb.util.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

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
import jakarta.xml.bind.JAXBException;

/**
 * Example of persisting {@link Employee} instances.
 *
 * Hint: In Eclipse, disable the check box for:
 *		 'Run Configuration / Dependencies / exclude test code'.
 */
public class Main extends Context
{
	private static Logger logger = LoggerFactory.getLogger(Main.class);
	public static Logger getLogger() { return logger; }

	private static final List<Employee> EMPLOYEE_LIST = new ArrayList<>();
	static
	{
		List<Project> lp1 = new ArrayList<>();
		lp1.add(new Project(null, "Tasers"));
		lp1.add(new Project(null, "Training"));
		Department d1 = new Department(null, "Police", EMPLOYEE_LIST);
		EMPLOYEE_LIST.add(new Employee(null, "Smith", false, d1, lp1));
		List<Project> lp2 = new ArrayList<>();
		lp2.add(new Project(null, "Pavilion"));
		lp2.add(new Project(null, "Safety"));
		Department d2 = new Department(null, "Fire", EMPLOYEE_LIST);
		EMPLOYEE_LIST.add(new Employee(null, "Jones", false, d2, lp2));
		EMPLOYEE_LIST.add(new Employee(null, "Brown", false, d2, lp2));
	}

	// Represents a list of Employee(s)
	private List<Employee> employeeList = null;
	public List<Employee> getEmployeeList()
	{
		if ( employeeList == null )
			setEmployeeList(new ArrayList<>());
		return employeeList;
	}
	public void setEmployeeList(List<Employee> employeeList)
	{
		this.employeeList = employeeList;
	}

	/**
	 * Command Line Invocation.
	 *
	 * @param args CLI argument(s) (i.e. connect1 insert1 execute1)
	 *
	 * @throws SAXException
	 * @throws JAXBException
	 */
	public static void main(String[] args)
	{
		try
		{
			Main main = new Main();
			main.run(args);
		}
		catch (IOException | JAXBException | SAXException ex)
		{
			getLogger().error("Aborting " + Main.class.getName(), ex);
		}
	}

	private void run(String[] args) throws IOException, JAXBException, SAXException
	{
		try ( EntityManager em = createEntityManager() )
		{
			for ( String command : args )
			{
				switch (command)
				{
					case "connect1": connect1(em); break;
					case "insert1": insert1(em); break;
					case "execute1": execute(em, "Police"); break;
					case "execute2": execute(em, "Fire"); break;
					case "project1": project1(em); break;
					case "project2": project2(em); break;
					case "project3": project3(em); break;
					case "summary0": summary0(em); break;
					case "summary1": summary1(em); break;
					case "summary2": summary2(em); break;
					case "summaryH2": summaryH2(em); break;
					case "summaryPG": summaryPG(em); break;
					default: connect1(em); break;
				}
			}
		}
	}

	/**
	 * Connect 1, connect to the database.
	 *
	 * @param em An entity manager.
	 *
	 * @throws IOException When the connection cannot be established.
	 */
	public void connect1(EntityManager em) throws IOException
	{
		// Prepare a transaction to select the Employee count.
		Transactional<Integer> tx = (tem) ->
		{
			Query query = tem.createQuery("select count(E) from Employee E");
			return ((Long) query.getSingleResult()).intValue();
		};
		Integer result = tx.transact(em);
		getLogger().info("connect1: Employee count is {}", result);
	}

	/**
	 * Insert 1, persist a list of {@link Employee}.
	 *
	 * @param emAn entity manager.
	 *
	 * @throws IOException When entities cannot be persisted.
	 */
	public void insert1(EntityManager em) throws IOException
	{
		long ms1 = System.currentTimeMillis();
		Transactional<Integer> tx = insertTX("insert", EMPLOYEE_LIST);
		long cnt = tx.transact(em);
		long ms2 = System.currentTimeMillis();
		long tot = ms2 - ms1;
		double avg = (double) tot / (double) cnt;
		getLogger().info(format("%s: cnt=%d; avg=%.4f ms; tot=%d ms", "insert1", cnt, avg, tot));
	}

	// Persist employee(s) in a transaction.
	private Transactional<Integer>  insertTX(String lable, List<Employee> entityList)
		throws IOException
	{
		// Prepare the transaction to persist or merge one batch.
		Transactional<Integer> tx = (em) ->
		{
			for ( Employee entity : entityList )
				em.persist(entity);
			return entityList.size();
		};
		return tx;
	}

	/**
	 * Execute the JPA actions.
	 *
	 * @param em An entity manager.
	 *
	 * @throws JAXBException When a JAXB action fails.
	 * @throws IOException When an I/O actions fails.
	 * @throws SAXException When XmlSchemaValidator cannot be generated from DOM.
	 */
	public void execute(EntityManager em, String department) throws JAXBException, IOException, SAXException
	{
		// Select Employee(s) by id, if any, up to 10.
		List<Employee> employees = selectEmployees(em, 0, 10, department);

		// Persist Batch, if missing.
		if ( employees.isEmpty() )
		{
			// Again, select Batch(es) by id, up to 1.
			employees = selectEmployees(em, 0, 1, department);
		}

		// Display Employee(es) outside transaction,
		// entities were eager loaded using an inner join.
		Set<Employee> employeeSet = new HashSet<>();
		// Build set of employees.
		employeeSet.addAll(employees);
		// JAXB: marshal Batch.
		getLogger().info("execute:\n{}", employeeSet);

		// Display Entities(s) in a transaction to avoid LazyInitializationException.
		int count = displayEmployees(em, employeeSet);
		getLogger().info("Entities displayed: {}", count);
	}

	/**
	 * Display employees for the given detached set of employees.
	 * @param employeeSet A detached set of employees.
	 * @return The count of employees displayed.
	 */
	protected int displayEmployees(EntityManager em, Set<Employee> employeeSet)
		throws IOException
	{
		// Always perform EntityManager actions within a transaction!
		Transactional<Integer> tx = (tem) ->
		{
			Integer count = 0;
			count = employeeSet.size();
			for ( Employee entity : employeeSet )
			{
				// Find the managed entity using the current EM.
				Employee emEntity = tem.find(Employee.class, entity.getId());
				getLogger().debug("Entity:\n{}", emEntity);
			}
			return count;
		};
		return tx.transact(em);
	}

	private void project1(EntityManager em) throws IOException
	{
		project(em, 1L);
	}

	private void project2(EntityManager em) throws IOException
	{
		project(em, 2L);
	}

	private void project3(EntityManager em) throws IOException
	{
		project(em, 3L);
	}

	private void project(EntityManager em, long empId) throws IOException
	{
		Transactional<Employee> tx = (tem) ->
		{
			// Retrieve the named graph
			EntityGraph<?> graph = em.getEntityGraph("Employee.projects");
			return em.find(Employee.class, empId,
			    Map.of("javax.persistence.loadgraph", graph));
		};

		Employee employee  = tx.transact(em);
		getLogger().info("Employee: {}, {}", employee.getId(), employee.getLastName());
		for ( Project project : employee.getProjects())
			getLogger().info("  Project: {}, {}", project.getId(), project.getProjectName());
	}

	private void summary0(EntityManager em) throws IOException
	{
		// Prepare a transaction call getEmployeeCount procedure.
		Transactional<Long> tx = (tem) ->
		{
			StoredProcedureQuery query =
				em.createNamedStoredProcedureQuery("getEmployeeCount");
			// PG: query.setHint("escapeSyntaxCallMode", "call");
			query.setParameter(1, 0L);
			// return (Long) query.getSingleResult();
			return (Long) query.getOutputParameterValue(1);
		};

		Long count = tx.transact(em);
		getLogger().info("Summary0: {}", count);
	}

	// Employee Summary Mapping: NamedNativeQuery
	private void summary1(EntityManager em) throws IOException
	{
		// Prepare a transaction call EmployeeSelectAll query.
		@SuppressWarnings("unchecked")
		Transactional<List<EmployeeSummary>> tx = (tem) ->
		{
			return em.createNamedQuery("EmployeeSelectAll")
				.getResultList();
		};

		List<EmployeeSummary> summaries = tx.transact(em);
		for ( EmployeeSummary es : summaries )
			getLogger().info("Summary1: {}, {}", es.getId(), es.getName());
	}

	// Employee Summary Mapping: (create) StoredProcedureQuery
	private void summary2(EntityManager em)
	{
		Transactional<List<EmployeeSummary>> tx = (tem) ->
		{
			// 1. Create the query pointing to your procedure
			StoredProcedureQuery query = em.createStoredProcedureQuery("ejb_tests_jpa21.fetch_emp_name");

			// 2. Register parameters (REF_CURSOR must be registered with void.class or the target Entity)
			// Dialect [org.hibernate.dialect.H2Dialect] not known to support REF_CURSOR parameters
			query.registerStoredProcedureParameter("emp_id", Long.class, ParameterMode.IN);
			query.registerStoredProcedureParameter("emp_name", String.class, ParameterMode.IN);

			// 3. Set the input value(s)
			Long empId = 1L;
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
				empId = (Long) values[0];
				empName = (String) values[1];
			}
			else
			{
				empId = (Long) query.getOutputParameterValue("emp_id");
				empName = (String) query.getOutputParameterValue("emp_name");
			}
			esList.add(new EmployeeSummary(empId, empName));

			return esList;
		};

		List<EmployeeSummary> summaries = tx.transact(em);
		for ( EmployeeSummary es : summaries )
			getLogger().info("Summary2: {}, {}", es.getId(), es.getName());
	}

	// Employee Summary Mapping: NamedStoredProcedureQuery (H2)
	private void summaryH2(EntityManager em) throws IOException
	{
		// Prepare a transaction call EmployeeSelectAll query.
		@SuppressWarnings("unchecked")
		Transactional<List<EmployeeSummary>> tx = (tem) ->
		{
			StoredProcedureQuery query =
				em.createNamedStoredProcedureQuery("getEmployeeDetailsH2");
			// IN Parameters
			Long empId = 1L;
			// Positional for H2 compatibility
			query.setParameter(1, empId);
			return query.getResultList();
		};
		List<EmployeeSummary> summaries = tx.transact(em);
		for ( EmployeeSummary es : summaries )
			getLogger().info("SummaryH2: {}, {}", es.getId(), es.getName());
	}

	// Employee Summary Mapping: NamedStoredProcedureQuery (PG)
	private void summaryPG(EntityManager em) throws IOException
	{
		// Prepare a transaction call EmployeeSelectAll query.
		Transactional<List<EmployeeSummary>> tx = (tem) ->
		{
			StoredProcedureQuery query =
				em.createNamedStoredProcedureQuery("getEmployeeDetailsPG");
			// Set by db URL: query.setHint("escapeSyntaxCallMode", "call");

			// INOUT Parameters
			Long empId = 1L;
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
				empId = (Long) values[0];
				empName = (String) values[1];
			}
			else
			{
				empId = (Long) query.getOutputParameterValue(1);
				empName = (String) query.getOutputParameterValue(2);
			}
			esList.add(new EmployeeSummary(empId, empName));

			return esList;
		};
		List<EmployeeSummary> summaries = tx.transact(em);

		for ( EmployeeSummary es : summaries )
			getLogger().info("SummaryPG: {}, {}", es.getId(), es.getName());
	}

	/**
	 * Select a limited list of employee(es) for the given id.
	 *
	 * @param start The starting offset.
	 * @param count The count limit.
	 * @param department The employee last name.
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
		Transactional<List<Employee>> tx = (em) ->
		{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);

			// Force eager loading of entities using an inner join.
			// See https://thorben-janssen.com/5-ways-to-initialize-lazy-relations-and-when-to-use-them/
			Root<Employee> fromEmployee = cq.from(Employee.class);
			fromEmployee.fetch("department", JoinType.INNER);

			cq.select(fromEmployee)
				.where(cb.equal(fromEmployee.get("department").get("name"), department));

			TypedQuery<Employee> query = em.createQuery(cq);
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
}
// vi:set tabstop=4 hardtabs=4 shiftwidth=4:
