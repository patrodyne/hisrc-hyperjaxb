package org.jvnet.hyperjaxb.ejb.tutorials.po.step_one;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.jvnet.hyperjaxb.ejb.util.EntityManagerFactoryUtil.createEntityManagerFactoryProperties;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class H2BigDecimalTest
{
	// See src/test/resources/META-INF/orm.xml
	private static final String SCHEMA = "ejb_tutorials_po_step_one";
	
	private Map<String, String> properties;

	@BeforeEach
	public void setUp()
		throws Exception
	{
		properties = createEntityManagerFactoryProperties(getClass());
	}
	
	@Test
	public void test() throws Exception
	{
		// connect (create) the database
		String drv = properties.get("jakarta.persistence.jdbc.driver");
		String url = properties.get("jakarta.persistence.jdbc.url");
		String user = properties.get("jakarta.persistence.jdbc.user");
		String pass = properties.get("jakarta.persistence.jdbc.password");
		
		// load JDBC driver
		Class.forName(drv);
		
		try ( Connection conn = DriverManager.getConnection(url, user, pass) )
		{
			conn.setSchema(SCHEMA);
			conn.setAutoCommit(false);
			
			// (re)create the table
			try ( Statement stmt = conn.createStatement() )
			{
				stmt.executeUpdate("drop table if exists test;");
				stmt.executeUpdate("create table test (content NUMERIC(5,0));");
				conn.commit();
				
				// insert the data
				String sql = "INSERT INTO test (content) VALUES(?)";
				try ( PreparedStatement pstmt = conn.prepareStatement(sql) )
				{
					pstmt.setBigDecimal(1, BigDecimal.ONE);
					pstmt.executeUpdate();
				}
				conn.commit();
				
				// assert the result(s)
				try ( ResultSet rs = stmt.executeQuery("SELECT * FROM test") )
				{
					while (rs.next())
						assertEquals(BigDecimal.ONE, rs.getBigDecimal(1));
				}
				conn.commit();
			}
			conn.setAutoCommit(true);
		}
	}
}
