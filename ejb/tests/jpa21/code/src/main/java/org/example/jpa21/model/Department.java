package org.example.jpa21.model;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;

import java.util.List;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "DEPARTMENT")
@NamedEntityGraph(
	name = "graph.Department.employees",
	attributeNodes = @NamedAttributeNode("employees")
)
public class Department
{
	// Getters and Setters...

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	@Basic
	private String name;
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	// Inverse side of the relationship
	@OneToMany(mappedBy = "department", cascade = { MERGE, PERSIST, REFRESH })
	private List<Employee> employees;
	public List<Employee> getEmployees() { return employees; }
	public void setEmployees(List<Employee> employees) { this.employees = employees; }

	// Constructors

	/** Standard no-arg constructor required by JPA */
	public Department() {}

	/** Full constructor */
	public Department(Long id, String name, List<Employee> employees)
	{
		this.id = id;
		this.name = name;
		this.employees = employees;
	}
}
