package org.jvnet.hyperjaxb.opt.hibernate.dialect;

import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.PostgreSQLDialect;

/**
 * An SQL dialect for PostgreSQL 10 and later.
 * 
 * @see <a href="https://hibernate.atlassian.net/browse/HHH-17032">Hibernate Issue HHH-17032</a> 
 */
public class PostgreSQL10Dialect extends PostgreSQLDialect
{
	public PostgreSQL10Dialect()
	{
		super( DatabaseVersion.make( 10 ) );
	}
}