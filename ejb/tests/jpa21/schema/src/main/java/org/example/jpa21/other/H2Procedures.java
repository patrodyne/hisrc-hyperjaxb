package org.example.jpa21.other;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.h2.tools.SimpleResultSet;

/**
 * In H2, stored procedures are implemented by creating a
 * Java method and registering it as a SQL ALIAS.
 *
 * <p>Note: If you need a database connection within this method,
 * define java.sql.Connection as the first parameter; H2 will
 * provide this automatically as a "hidden" parameter.</p>
 *
 * CREATE ALIAS IF NOT EXISTS ejb_tests_jpa21.FETCH_EMP_COUNT FOR "org.example.jpa21.other.H2Procedures.fetchEmployeeCount";
 * CREATE ALIAS IF NOT EXISTS ejb_tests_jpa21.FETCH_EMP_NAME FOR "org.example.jpa21.other.H2Procedures.fetchEmployeeName";
 */
public class H2Procedures
{
	/**
	 * Implementation for FETCH_EMP_COUNT
	 *
	 * @param conn The database connection (provided automatically by H2)
	 * @param count Out parameter: an array of size 1 to store the result
	 */
	public static Long fetchEmployeeCount(Connection conn, Long count)
		throws SQLException
	{
		String sql = "SELECT count(*) FROM ejb_tests_jpa21.EMPLOYEES";
		try ( Statement stmt = conn.createStatement() )
		{
			try ( ResultSet rs = stmt.executeQuery(sql) )
			{
				count = rs.next() ? rs.getLong(1) : 0l;
			}
		}
		return count;
	}

	/**
	 * Implementation for FETCH_EMP_NAME.
	 *
	 * <ul>
	 * <li>The first parameter (Connection) is provided by H2 and not seen by JPA.</li>
	 * <li>H2 does not natively support named parameters for CallableStatement executions.</li>
	 * </ul>
	 *
	 * @param conn The database connection (injected by H2)
	 * @param empId Input/Output parameter (IN)
	 *
	 * @return A simple result set of the query results.
	 */
	public static ResultSet fetchEmployeeName(Connection conn, Long empId)
		throws SQLException
	{
		SimpleResultSet result = new SimpleResultSet();
		// Looks like H2 calls this procedure three times:
		// 1) StatementPreparationTemplate with null IN parameters.
		// 2) recompileIfRequired with valid IN parameters.
		// 3) The actual call with valid IN parameters.
		if ( empId != null )
		{
			String query = "SELECT id, last_name FROM ejb_tests_jpa21.EMPLOYEES WHERE id = ?";
			try ( PreparedStatement ps = conn.prepareStatement(query) )
			{
				ps.setLong(1, empId);
				try ( ResultSet rs = ps.executeQuery() )
				{
					if (rs.next())
					{
						// Column definitions
						result.addColumn("id", Types.BIGINT, 0, 0);
						result.addColumn("last_name", Types.VARCHAR, 255, 0);
						// Row as an array of column objects
						empId = rs.getLong("id");
						String empName = rs.getString("last_name");
						result.addRow(empId, empName);
					}
				}
			}
		}
		return result;
	}
}
