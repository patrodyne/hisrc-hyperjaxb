package org.example.jpa21;

import org.example.jpa21.model.Employee;
import org.example.jpa21.model.Organization;
import org.example.jpa21.model.Project;
import org.junit.jupiter.api.Order;
import org.jvnet.basicjaxb.xml.bind.ContextPathAware;

@Order(2)
public class RoundtripTest
    extends org.jvnet.hyperjaxb.ejb.test.RoundtripTest
    implements ContextPathAware
{
	@Override
	public String getContextPath()
	{
		return "org.example.jpa21.model";
	}

	@Override
	public String getPersistenceUnitName()
	{
		return "org.example.jpa21.model";
	}

	// JAXB Unmarshal: When the departments provide the list of
	// employees, JAXB does not set the department on the employees
	// because it is not provided in the data. This method sets
	// that property.
	//
	// Persistence Control: Updates made only to the inverse side
	// (the side with mappedBy) are ignored by the database. To save
	// a relationship, you must set the reference on the owning side
	// (the child side).
	@Override
	protected void postUnmarshalSample(Sample sample)
	{
		if ( sample.getValue() instanceof Organization organization )
		{
//			for ( Department department : organization.getDepartments() )
//				department.tieEmployeeRefs();
			for ( Project project : organization.getProjects() )
				project.tieEmployees();
			for ( Employee employee : organization.getEmployees() )
				employee.tieProjects();
		}
	}
}
