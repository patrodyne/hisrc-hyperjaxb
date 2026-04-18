package org.example.jpa21.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "PROJECT")
public class Project
{
	// Getters and Setters...

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	private String projectName;
	public String getProjectName() { return projectName; }
	public void setProjectName(String projectName) { this.projectName = projectName; }

	// Constructors

	/** Default constructor */
	public Project() {}

	/** Full constructor */
	public Project(Long id, String projectName)
	{
		super();
		this.id = id;
		this.projectName = projectName;
	}

}
