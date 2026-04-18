package org.example.jpa21.other;

/**
 * SQL result set mapping target.
 *
 * <p>
 * {@code EmployeeSummary} is implemented as a Plain Old Java Object (POJO) or a Data Transfer Object (DTO).
 * Because it is used with the {@code @ConstructorResult} annotation in the {@code Employee} entity,
 * it does not need to be a JPA {@code @Entity}. However, it must have a constructor that exactly matches
 * the types and order of the {@code @ColumnResult} entries defined in the mapping.
 * </p>
 */
public class EmployeeSummary
{
	// The constructor must match the @ConstructorResult order: id, then name
	public EmployeeSummary(Long id, String name)
	{
		this.id = id;
		this.name = name;
	}

	// Getters only (setters are optional since JPA uses the constructor)

	private Long id;
	public Long getId() { return id; }

	private String name;
	public String getName() { return name; }
}
