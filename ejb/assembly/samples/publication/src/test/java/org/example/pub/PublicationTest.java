package org.example.pub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.jvnet.basicjaxb.test.Bogus.RANDOM;
import static org.jvnet.basicjaxb.test.Bogus.alpha;
import static org.jvnet.basicjaxb.test.Bogus.firstName;
import static org.jvnet.basicjaxb.test.Bogus.lastName;
import static org.jvnet.basicjaxb.test.Bogus.streetAddress;
import static org.jvnet.hyperjaxb.ejb.util.Transactional.CacheOption.REUSE;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.example.pub.model.Author;
import org.example.pub.model.Blog;
import org.example.pub.model.Book;
import org.example.pub.model.ObjectFactory;
import org.example.pub.model.Publication;
import org.junit.jupiter.api.Test;
import org.jvnet.hyperjaxb.ejb.test.AbstractEntityManagerTest;
import org.jvnet.hyperjaxb.ejb.util.Transactional;

import jakarta.persistence.EntityManager;
import schemacrawler.inclusionrule.RegularExpressionInclusionRule;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;
import us.fatehi.utility.datasource.MultiUseUserCredentials;

/**
 * Unit tests for Publication persistence.
 */
public class PublicationTest extends AbstractEntityManagerTest
{
	private static final int AUTHOR_PER_PUB_COUNT_MAX = 2;
	private static final int AUTHOR_REUSE_ODDS = 5;
	private static final int AUTHOR_COUNT_MIN = 10;
	private static final int BLOG_COUNT_MAX = 10;
	private static final int BOOK_COUNT_MAX = 10;
	private static final String TITLE_PREFIX = "Mystery of ";
	private static final String CATALOG_SCHEMA = "ejb_samples_publication";
	private static final String OUTFILE_PATH = "./target/generated-docs";
	private static final String OUTFILE_NAME1 = "Publication";
	private static final String OUTFILE_NAME2 = "Tables";
	private static final String OUTFILE_NAME3 = "HB-H2-JOINED";
	private static final String SCHEMA_TITLE = OUTFILE_NAME1 + ": " + OUTFILE_NAME3;
	private static final String OUTFILE_NAME = OUTFILE_NAME1 + OUTFILE_NAME2 + "-" + OUTFILE_NAME3;
	
	@Override
	public String getPersistenceUnitName()
	{
		return ObjectFactory.class.getPackageName();
	}
	
	/**
	 * <p>Test persistence of many-to-many relationship and entity inheritance.
	 * In the publication model, there is a many-to-many relationship between
	 * <code>Publication</code> and <code>Author</code>. This test generates
	 * the many-to-many instances to test JPA <em>persist</em> and <em>query</em>
	 * methods.</p>
	 * 
	 * <p>Also, the <code>Publication</code> entity is inherited by to sub-entities:
	 * <code>Blog</code> and <code>Book</code>. This test generates both types of
	 * sub-entities for <em>persist</em> and <em>query</em> method testing.</p>
	 */
	@Test
	public void testPersistence()
	{
		List<Author> authorList = new ArrayList<>();
		
		try ( EntityManager entityManager = getEntityManagerFactory().createEntityManager() )
		{
			assertTrue(entityManager.isOpen(), "EntityManager instance is open");

			for ( int authorIdx = 0; authorIdx < AUTHOR_COUNT_MIN; ++authorIdx)
			{
				Transactional<Author> tx = (em) ->
				{
					Author author = generateAuthor(authorList);
					em.persist(author);
					return author;
				};
				
				Author author = tx.transact(entityManager, REUSE);
				assertTrue(author.getId() > 0, "Author entity has non-zero id.");
			}

			int blogCount = 1 + RANDOM.nextInt(BLOG_COUNT_MAX);
			for ( int blogIdx = 0; blogIdx < blogCount; ++blogIdx)
			{
				Transactional<Blog> tx = (em) ->
				{
					Blog blog = new Blog();
					blog.setTitle(TITLE_PREFIX + streetAddress());
					blog.setPublishingDateItem(new Date());
					blog.setUrl("https://example.org/blog/"+alpha(6));
					
					int authorCount = 1 + RANDOM.nextInt(2);
					for ( int authorIdx = 0; authorIdx < authorCount; ++authorIdx)
					{
						Author author = generateAuthor(authorList);
						if ( !blog.getAuthors().contains(author) )
						{
							blog.getAuthors().add(author);
							author.getPublications().add(blog);
						}
					}

					em.persist(blog);
					return blog;
				};
				
				Blog blog = tx.transact(entityManager, REUSE);
				assertTrue(blog.getId() > 0, "Blog entity has non-zero id.");
				
				// Keep the managed authors for 'many' other publications.
				for ( Author author : blog.getAuthors() )
				{
					if ( !authorList.contains(author) )
						authorList.add(author);
				}
			}
			
			int bookCount = 1 + RANDOM.nextInt(BOOK_COUNT_MAX);
			for ( int bookIdx = 0; bookIdx < bookCount; ++bookIdx)
			{
				Transactional<Book> tx = (em) ->
				{
					Book book = new Book();
					book.setTitle(TITLE_PREFIX + streetAddress());
					book.setPublishingDateItem(new Date());
					book.setPages(100 + RANDOM.nextInt(400));
					
					int authorCount = 1 + RANDOM.nextInt(AUTHOR_PER_PUB_COUNT_MAX);
					for ( int authorIdx = 0; authorIdx < authorCount; ++authorIdx)
					{
						Author author = generateAuthor(authorList);
						if ( !book.getAuthors().contains(author) )
						{
							book.getAuthors().add(author);
							author.getPublications().add(book);
						}
					}
					
					em.persist(book);
					return book;
				};
				Book book = tx.transact(entityManager, REUSE);
				assertTrue(book.getId() > 0, "Book entity has non-zero id.");
				
				// Keep the managed authors for 'many' other publications.
				for ( Author author : book.getAuthors() )
				{
					if ( !authorList.contains(author) )
						authorList.add(author);
				}
			}
			
			for ( Author author : authorList )
			{
				assertTrue(author.getPublications().size() > 0, "There must be at least 1 publication per author.");
				for ( Publication pub : author.getPublications() )
				{
					String title = pub.getTitle();
					Transactional<List<Publication>> tx = Main.selectPublicationsTX(0, 1, title);
					List<Publication> publicationList = tx.transact(entityManager, REUSE);
					assertTrue(publicationList.size() > 0, "Publication with '" + title + "' should exist.");
					assertTrue(pub.getAuthors().size() > 0, "There must be at least 1 author per publication.");
				}
			}
		}
		
		assertTrue(authorList.size() > 0, "At least 1 author must exist.");
	}

	private Author generateAuthor(List<Author> authorList)
	{
		Author author = null;
		if ( !authorList.isEmpty() && (RANDOM.nextInt(AUTHOR_REUSE_ODDS) == 0) )
			author = authorList.get(RANDOM.nextInt(authorList.size()));
		else
		{
			author = new Author();
			author.setFirstName(firstName());
			author.setLastName(lastName());
		}
		return author;
	}
	
	@Test
	public void testSchemaCrawler1() throws Exception
	{
		try ( DatabaseConnectionSource dataSource = newDatabaseConnectionSource(getEntityManagerFactoryProperties()) )
		{
		    final SchemaCrawlerOptions scOptions = newSchemaCrawlerOptions();
			final Catalog catalog = SchemaCrawlerUtility.getCatalog(dataSource, scOptions);
			
			assertEquals(1, catalog.getSchemas().size(), "Schema(s) are limited by config");

			if ( getLogger().isDebugEnabled() )
			{
				StringBuilder sb = new StringBuilder();
				for (final Schema schema : catalog.getSchemas())
				{
					sb.append("Catalog Schema\n\n" + schema.getFullName()+"\n");
					for (final Table table : catalog.getTables(schema))
					{
						sb.append("    " + table.getName()+"\n");
						for (final Column column : table.getColumns())
							sb.append("        " + column.getName() + " (" + column.getColumnDataType() + ")\n");
					}
				}
				getLogger().debug(sb.toString());
			}
		}
	}

	@Test
	public void testSchemaCrawler2() throws Exception
	{
	    final SchemaCrawlerOptions scOptions = newSchemaCrawlerOptions();
	    
	    final DiagramOutputFormat dof = DiagramOutputFormat.svg;
	    final Path outputFile = getOutputFile(OUTFILE_PATH, OUTFILE_NAME, dof);
//	    final OutputOptions outputOptions =
//	    	OutputOptionsBuilder.newOutputOptions(dof, outputFile);
	    
	    final OutputOptions outputOptions = OutputOptionsBuilder.builder()
			.withOutputFormat(dof)
			.withOutputFile(outputFile)
			.title(SCHEMA_TITLE)
			.toOptions();

	    final Config config = new Config();
	    config.put("no-info", true);
	    config.put("no-remarks", true);
	    config.put("portable-names", true);

	    final String command = "schema";
	    
		try ( DatabaseConnectionSource dataSource = newDatabaseConnectionSource(getEntityManagerFactoryProperties()) )
		{
			final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
			executable.setSchemaCrawlerOptions(scOptions);
			executable.setOutputOptions(outputOptions);
			executable.setDataSource(dataSource);
			executable.setAdditionalConfiguration(config);
			executable.execute();
		}
	}

	private static Path getOutputFile(String path, String name, DiagramOutputFormat dof)
	{
		String outputfile = path + "/" + name + "." + dof.name();
		return Paths.get(outputfile).toAbsolutePath().normalize();
	}
	
	private DatabaseConnectionSource newDatabaseConnectionSource(Map<String, String> props)
	{
		String jdbcURL = props.get("jakarta.persistence.jdbc.url");
		String jdbcUser = props.get("jakarta.persistence.jdbc.user");
		String jdbcPass = props.get("jakarta.persistence.jdbc.password");
		MultiUseUserCredentials muuc = new MultiUseUserCredentials(jdbcUser, jdbcPass);
		return DatabaseConnectionSources.newDatabaseConnectionSource( jdbcURL, muuc );
	}

	private SchemaCrawlerOptions newSchemaCrawlerOptions()
	{
		// Create the options
		final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder()
			.includeSchemas(new RegularExpressionInclusionRule(".*"+CATALOG_SCHEMA));
		
		// Set what details are required in the schema - this affects the
		// time taken to crawl the schema
		final LoadOptionsBuilder loadOptionsBuilder = LoadOptionsBuilder.builder()
			.withSchemaInfoLevel(SchemaInfoLevelBuilder.standard());
		
		final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
			.withLimitOptions(limitOptionsBuilder.toOptions())
			.withLoadOptions(loadOptionsBuilder.toOptions());
		return options;
	}
}
