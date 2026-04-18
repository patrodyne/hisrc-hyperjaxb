package org.example.jpa21.model;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;

import java.util.List;

import org.example.jpa21.other.BooleanConverter;
import org.example.jpa21.other.EmployeeSummary;

import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.StoredProcedureParameter;
import jakarta.persistence.Table;

@Entity
// Table with standardized @Index
@Table(name = "EMPLOYEES",
	indexes = { @Index(name = "idx_emp_last_name", columnList = "last_name") }
)
// Entity Graph for optimizing fetch plans
@NamedEntityGraph(
	name = "Employee.projects",
	attributeNodes = @NamedAttributeNode("projects")
)
// Standardized Stored Procedure Mapping: Count
@NamedStoredProcedureQuery(
	name = "getEmployeeCount",
	procedureName = "ejb_tests_jpa21.FETCH_EMP_COUNT",
	parameters = {
		@StoredProcedureParameter(mode = ParameterMode.INOUT, type = Long.class)
	}
)
//SQL to summarize all employees
@NamedNativeQuery(
	name = "EmployeeSelectAll",
	query = "SELECT e.id, e.last_name FROM ejb_tests_jpa21.EMPLOYEES e",
	resultSetMapping = "EmployeeSummaryMapping"
)
// Standardized Stored Procedure Mapping: Details
// When mapping this in JPA, you do not use ParameterMode.OUT.
// Instead, you treat it as a procedure that returns a result set.
// Set the resultClasses or resultSetMappings to capture the output.
@NamedStoredProcedureQuery(
	name = "getEmployeeDetailsH2",
	procedureName = "ejb_tests_jpa21.FETCH_EMP_NAME",
	parameters = {
		// , name = "empId"; Use positional parameters for H2.
		@StoredProcedureParameter(mode = ParameterMode.IN, type = Long.class)
	},
	resultSetMappings = "EmployeeSummaryMapping"
)
@NamedStoredProcedureQuery(
    name = "getEmployeeDetailsPG",
    procedureName = "ejb_tests_jpa21.fetch_emp_name",
    parameters = {
        @StoredProcedureParameter(mode = ParameterMode.INOUT, type = Long.class),
        @StoredProcedureParameter(mode = ParameterMode.INOUT, type = String.class)
    }
)
// Standard mapping for native SQL result to a non-entity DTO
@SqlResultSetMapping(
	name = "EmployeeSummaryMapping",
	classes = @ConstructorResult(
		targetClass = EmployeeSummary.class,
		columns = {
			@ColumnResult(name = "id", type = Long.class),
			@ColumnResult(name = "last_name", type = String.class)
		}
	)
)
public class Employee
{
	// Getters and Setters...

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	@Column(name = "last_name")
	private String lastName;
	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }

	// Custom conversion
	@Convert(converter = BooleanConverter.class)
	private boolean active;
	public boolean isActive() { return active; }
	public void setActive(boolean active) { this.active = active; }

	// Standardized @ForeignKey
	@ManyToOne(cascade = { MERGE, PERSIST, REFRESH })
	@JoinColumn(name = "dept_id",
		foreignKey = @ForeignKey(name = "FK_DEPT_EMP")
	)
	private Department department;
	public Department getDepartment() { return department; }
	public void setDepartment(Department department) { this.department = department; }

	@ManyToMany(cascade = { MERGE, PERSIST, REFRESH })
	@JoinTable(
		name = "EMP_PROJECT",
		joinColumns = @JoinColumn(name = "emp_id"),
		inverseJoinColumns = @JoinColumn(name = "proj_id")
	)
	private List<Project> projects;
	public List<Project> getProjects() { return projects; }
	public void setProjects(List<Project> projects) { this.projects = projects; }

	// Constructors

	/** Default constructor */
	public Employee() {  }

	/** Full constructor */
	public Employee(Long id, String lastName, boolean active, Department department, List<Project> projects)
	{
		this.id = id;
		this.lastName = lastName;
		this.active = active;
		this.department = department;
		this.projects = projects;
	}
}
