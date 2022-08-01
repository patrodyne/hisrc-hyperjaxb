package org.jvnet.hyperjaxb3.ejb.tutorials.po.stepone;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Assert;
import org.junit.Test;

public class H2BigDecimalTest {

	@Test
	public void test() throws Exception {
		Class.forName("org.h2.Driver");
		String url = "jdbc:h2:mem:data/test";
		Connection conn = DriverManager.getConnection(url, "tester", "123456");
		Statement stmt = conn.createStatement();

		stmt.executeUpdate("create table test (content NUMERIC(5,0));");

		String sql = "INSERT INTO test (content) VALUES(?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		pstmt.setBigDecimal(1, BigDecimal.ONE);

		// insert the data
		pstmt.executeUpdate();

		ResultSet rs = stmt.executeQuery("SELECT * FROM test");
		while (rs.next()) {
			Assert.assertEquals(BigDecimal.ONE, rs.getBigDecimal(1));
		}

		rs.close();
		stmt.close();
		conn.close();
	}

}
