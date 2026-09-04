package org.jvnet.hyperjaxb.opt.hibernate.dialect;

import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.PostgreSQLDialect;

/**
 * An SQL dialect for PostgreSQL 14 and later.
 *
 * @see <a href="https://hibernate.atlassian.net/browse/HHH-17032">Hibernate Issue HHH-17032</a>
 */
public class PostgreSQL14Dialect extends PostgreSQLDialect
{
	public PostgreSQL14Dialect()
	{
		super( DatabaseVersion.make( 14 ) );
	}
}
