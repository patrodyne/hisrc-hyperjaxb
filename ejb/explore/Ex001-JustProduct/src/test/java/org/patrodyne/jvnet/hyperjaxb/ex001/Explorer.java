package org.patrodyne.jvnet.hyperjaxb.ex001;

import static java.lang.System.nanoTime;
import static java.util.Arrays.sort;
import static org.patrodyne.jvnet.hyperjaxb.ex001.model.Stage.ACTIVE;
import static org.patrodyne.jvnet.hyperjaxb.ex001.model.Stage.CANCELED;
import static org.patrodyne.jvnet.hyperjaxb.ex001.model.Stage.CLOSED;
import static org.patrodyne.jvnet.hyperjaxb.ex001.model.Stage.HOLD;
import static org.patrodyne.jvnet.hyperjaxb.ex001.model.Stage.OPEN;
import static org.patrodyne.jvnet.hyperjaxb.opt.hibernate.SessionFactoryUtil.gatherProperties;
import static org.patrodyne.jvnet.hyperjaxb.opt.hibernate.SessionFactoryUtil.isInitialized;
import static org.patrodyne.jvnet.hyperjaxb.opt.hibernate.SessionFactoryUtil.isProxy;
import static org.patrodyne.jvnet.hyperjaxb.opt.hibernate.SessionFactoryUtil.sqlAction;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.jvnet.hyperjaxb3.ejb.util.Transactional;
import org.jvnet.jaxb2_commons.test.Bogus;
import org.patrodyne.jvnet.hyperjaxb.ex001.model.ObjectFactory;
import org.patrodyne.jvnet.hyperjaxb.ex001.model.Product;
import org.patrodyne.jvnet.hyperjaxb.ex001.model.Product_;
import org.patrodyne.jvnet.hyperjaxb.ex001.model.Stage;
import org.patrodyne.jvnet.hyperjaxb.explore.AbstractEntityExplorer;
import org.patrodyne.jvnet.hyperjaxb.opt.hibernate.SessionFactoryUtil;
import org.patrodyne.jvnet.hyperjaxb.opt.hikaricp.HikariCPUtil;

/**
 * A Swing application to explore features of the HiSrc HyperJAXB Framework.
 * 
 * Projects create their own custom Explorer by extending AbstractExplorer and
 * providing an HTML lesson page then adding JMenuItem(s) to trigger exploratory code.
 * 
 * The AbstractExplorer displays three panels: an HTML lesson, a print console and an
 * error console. The lesson file is read as a resource relative to this class. Text
 * is sent to the print console by calling 'println(text)' and error messages are
 * sent to the error console by calling 'errorln(msg)'. Also, 'standard out' /
 * 'standard error' streams are sent to their respective consoles.
 * 
 * @author Rick O'Sullivan
 */
@SuppressWarnings("serial")
public class Explorer extends AbstractEntityExplorer
{
	private static final String WINDOW_TITLE = "HiSrc HyperJAXB Ex001 JustProduct";
	private static final String PRODUCT_FILES_PATH = "src/test/example/products";
	private static final Integer MAX_RESULT_COUNT = 1000;

	private RoundtripTest roundtripTest;
	public RoundtripTest getRoundtripTest() { return roundtripTest; }
	public void setRoundtripTest(RoundtripTest roundtripTest) { this.roundtripTest = roundtripTest; }

	private Set<Product> productSet;
	public Set<Product> getProductSet()
	{
		if ( productSet == null )
			setProductSet(new HashSet<>());
		return productSet;
	}
	public void setProductSet(Set<Product> productSet)
	{
		this.productSet = productSet;
	}
	
	/**
	 * Main entry point for command line invocation.
	 * @param args CLI arguments
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(() -> {
			JFrame frame = new Explorer(EXPLORER_HTML);
			frame.setVisible(true);
		});
	}

	/**
	 * Construct application with an HTML lesson.
	 */
	public Explorer(String htmlName)
	{
		super(htmlName);
		setTitle(WINDOW_TITLE);
		try
		{
			setJMenuBar(createMenuBar());
			modifyToolBar();
			initializeLesson();
		}
		catch (Exception ex)
		{
			errorln(ex);
		}
	}
	
	public void initializeLesson() throws Exception
	{
		// Initialize JAXB
		Class<?>[] classesToBeBound = { ObjectFactory.class, Product.class };
		setJaxbContext(createJAXBContext(classesToBeBound));
		setMarshaller(createMarshaller(getJaxbContext()));
		setUnmarshaller(createUnmarshaller(getJaxbContext()));
		
		// Initialize JPA
		setEntityManagerFactoryProperties(loadEntityManagerFactoryProperties());
		setPersistenceUnitName(resolvePerstenceUnitName(ObjectFactory.class));
		setEntityManagerFactory(createEntityManagerFactory());
		setEntityManager(createEntityManager());
		
		// Collect context properties
		setExternalContextProperties(filterExternalProperties());
		setInternalContextProperties(gatherInternalProperties());
		
		// Initialize RoundtripTest
		setRoundtripTest(new RoundtripTest(getEntityManagerFactory()));
	}

	// Properties: Hibernate Session Factory
	private Map<String, Object> gatherInternalProperties()
	{
		EntityManagerFactory emf = getEntityManagerFactory();
		Map<String, Object> extProps = gatherProperties(emf, getExternalContextProperties());
		Map<String, Object> hikariProperties = HikariCPUtil.gatherProperties(getEntityManagerFactory());
		extProps.putAll(hikariProperties);
		return extProps;
	}
	
	public void unmarshalProducts()
	{
		File productFilePath = new File(PRODUCT_FILES_PATH);
		File[] productFileList = productFilePath.listFiles();
		if ( productFileList != null )
		{
			getProductSet().clear();
			sort(productFileList);
			for ( File productFile : productFileList )
			{
				Product product = unmarshalFromFile(productFile);
				if ( product != null )
				{
					if ( getProductSet().add(product) )
						println("Unmarshal: " + "'" + productFile + "'");
					else
						println("Duplicate: " + "'" + productFile + "'");
				}
			}
		}
		else
			println("No product files found at path '" + PRODUCT_FILES_PATH + "'");
	}
	
	public void marshalProducts()
	{
		for ( Product product : getProductSet() )
			marshal("Product: ", product);
	}
	
	public void persistProducts()
	{
		// Always perform EntityManager actions within a transaction!
		Transactional<Integer> tx = (em) ->
		{
			int count = 0;
			for ( Product product : getProductSet() )
			{
				// Persist makes the instance managed and persistent.
				em.persist(product);
				++count;
			}
			return count;
		};
		long t1 = nanoTime();
		Integer count = tx.transact(getEntityManager(), reuseCache());
		long t2 = nanoTime();
		println("Persist returned " + count + " count; " + ns(t1,t2));
	}
	
	public void mergeProducts()
	{
		// Always perform EntityManager actions within a transaction!
		Transactional<Integer> tx = (em) ->
		{
			Set<Product> mergeProductSet = new HashSet<Product>();
			for ( Product product : getProductSet() )
			{
				// Merge returns the managed entity.
				Product mergeProduct = em.merge(product);
				mergeProductSet.add(mergeProduct);
			}
			setProductSet(mergeProductSet);
			return mergeProductSet.size();
		};
		long t1 = nanoTime();
		Integer count = tx.transact(getEntityManager(), reuseCache());
		long t2 = nanoTime();
		println("Merge returned " + count + " count; " + ns(t1,t2));
	}
	
	public void queryProducts(Integer start, Integer count, Stage stage)
	{
		long t1 = nanoTime();
		List<Product> productList = selectProducts(start, count, stage);
		long t2 = nanoTime();
		println("Query returned " + productList.size() + " results; " + ns(t1,t2) + "\n");
		setProductSet(new HashSet<>(productList));
	}
	
	private List<Product> selectProducts(Integer start, Integer count, Stage stage)
	{
		// Always perform EntityManager actions within a transaction!
		Transactional<List<Product>> tx = (em) ->
		{
			CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
			CriteriaQuery<Product> cq = cb.createQuery(Product.class);
			Root<Product> queryRoot = cq.from(Product.class);
			
			if ( stage != null )
			{
				cq.select(queryRoot)
					.where(cb.equal(queryRoot.get(Product_.stage), stage));
			}
			else
				cq.select(queryRoot);
			
			TypedQuery<Product> query = em.createQuery(cq);
			query.setHint("org.hibernate.cacheable", true);
			List<Product> entities = query
				.setFirstResult(start)
				.setMaxResults(count)
				.getResultList();

			return entities;
		};
		List<Product> productList = tx.transact(getEntityManager(), reuseCache());
		return productList;
	}

	private void queryAllProducts()
	{
		queryProducts(0, MAX_RESULT_COUNT, null);
	}
	
	public void processProducts()
	{
		processProducts(OPEN, ACTIVE, HOLD);
		processProducts(ACTIVE, CLOSED, HOLD);
		processProducts(HOLD, CANCELED, OPEN);
		println();
		queryAllProducts();
	}
		
	private void processProducts(Stage stageFrom, Stage stageTo, Stage stageAlt)
	{
		println(stageFrom + " products processing... ");
		// Always perform EntityManager actions within a transaction!
		Transactional<Integer> tx = (em) ->
		{
			int count = 0;
			List<Product> selectProducts = selectProducts(0, MAX_RESULT_COUNT, stageFrom);
			for ( Product product : selectProducts )
			{
				if ( RANDOM.nextInt(100) < 50 )
				{
					product.setStage(stageTo);
					++count;
					println("  " + stageTo + ": " + product);
				}
				else if ( RANDOM.nextInt(100) < 10 )
				{
					product.setStage(stageAlt);
					++count;
					println("  " + stageAlt + ": " + product);
				}
			}
			return count;
		};
		long t1 = nanoTime();
		int count = tx.transact(getEntityManager(), reuseCache());
		long t2 = nanoTime();
		println(stageFrom + " products processed: " + count + "; " + ns(t1,t2) + "\n");
	}
	
	public void disposeProducts()
	{
		disposeProducts(CLOSED);
		disposeProducts(CANCELED);
		println();
		queryAllProducts();
	}

	private void disposeProducts(Stage stage)
	{
		println(stage + " products disposing... ");
		// Always perform EntityManager actions within a transaction!
		Transactional<Integer> tx = (em) ->
		{
			int count = 0;
			List<Product> selectProducts = selectProducts(0, MAX_RESULT_COUNT, stage);
			for ( Product product : selectProducts )
			{
				if ( RANDOM.nextInt(100) < 50 )
				{
					em.remove(product);
					++count;
					println("  " + stage + " disposed: " + product);
				}
			}
			return count;
		};
		long t1 = nanoTime();
		Integer count = tx.transact(getEntityManager(), reuseCache());
		long t2 = nanoTime();
		println(stage + " products disposed: " + count + "; " + ns(t1,t2) + "\n");
	}

	public void removeProducts()
	{
		// Always perform EntityManager actions within a transaction!
		Transactional<Integer> tx = (em) ->
		{
			int count = 0;
			Iterator<Product> iterator = getProductSet().iterator();
			while ( iterator.hasNext() )
			{
				Product startProduct = iterator.next();
				Product foundProduct = em.find(Product.class, startProduct.getPartNum());
				if ( foundProduct != null)
					em.remove(foundProduct);
				iterator.remove();
				++count;
			}
			return count;
		};
		long t1 = nanoTime();
		Integer count = tx.transact(getEntityManager(), reuseCache());
		long t2 = nanoTime();
		println("Remove returned " + count + " count; " + ns(t1,t2) + "\n");
	}

	public void extensionHashCodes()
	{
		println("Extension Hash Codes");
		println();

		for ( Product product : getProductSet() )
			println(identify(product));
		println();
	}
	
	public void extensionEquality()
	{
		println("Extension Equality\n");
		List<Product> productList = new ArrayList<>(getProductSet());
		int needleIndex = RANDOM.nextInt(productList.size());
		Product needle = productList.get(needleIndex);
		println("Searching for index: " + needleIndex + "; " + identify(needle));
		println();
		// Loop over product list and compare each item to the needle.
		for ( int index = 0; index < productList.size(); ++index )
		{
			Product haystackItem = productList.get(index);
			if ( needle.equals(haystackItem) )
				println("Comparing index: " + index + "; Match: " + identify(haystackItem));
			else
				println("Comparing index: " + index);
		}
		println();
	}
	
	public void extensionToString()
	{
		println("Extension ToString\n");
		List<Product> productList = new ArrayList<>(getProductSet());
		for ( Product product : productList )
			println(product.toString());
		println();
	}

	public void roundtripJAXBValid()
	{
		List<Product> productList = new ArrayList<>(getProductSet());
		if ( !productList.isEmpty() )
		{
			for ( Product productA : productList )
			{
				String productAXml = marshalToString(productA);
				Product productB = unmarshalFromString(productAXml, Product.class);
				println("ProductA vs ProductB (" + productA.getPartNum()+ "): " + (productA.equals(productB) ? "EQUAL" : "UNEQUAL"));
			}
			println();
		}
		else
			println("No products.");
	}

	public void roundtripJAXBInvalid()
	{
		List<Product> productList = new ArrayList<>(getProductSet());
		if ( !productList.isEmpty() )
		{
			Product product1A = productList.get(0);
			String product1AXml = marshalToString(product1A);
			// product1BXml is made intentionally invalid!
			String product1BXml = product1AXml.replaceAll("Price", "Cost");
			Product product1B = unmarshalFromString(product1BXml, Product.class);
			if ( product1A.equals(product1B) )
				println("Product1A vs Product1B: EQUAL");
			else
			{
				println("Product1A vs Product1B: UNEQUAL");
				println();
				println("[BEFORE] Product1A XML:\n" + product1AXml);
				println("[BEFORE] Product1B XML:\n" + product1BXml);
				println();
				println("[AFTER] Product1A XML:\n" + marshalToString(product1A));
				println("[AFTER] Product1B XML:\n" + marshalToString(product1B));
			}
			println();
		}
		else
			println("No products.");
	}

	public void roundtripJPA()
	{
		try
		{
			getRoundtripTest().testSamples();
		}
		catch (Exception ex)
		{
			errorln(ex);
		}
	}
	
	public void searchProducts()
	{
		List<Product> productList = new ArrayList<>(getProductSet());
		if ( !productList.isEmpty() )
		{
			Product product1 = Bogus.randomItem(productList);
			Product product2 = unmarshalFromString(marshalToString(product1), Product.class);
			int index = productList.indexOf(product2);
			println("Search: " + product2);
			println("Found.: " + productList.get(index));
			println("Index.: " + index);
			println();
		}
		else
			println("No products.");
	}
	
	public void chaosExhaustConnectionPool()
	{
		int maximumPoolSize = getContextProperty("hibernate.hikari.maximumPoolSize", 10);
		long connectionTimeout = getContextProperty("hibernate.hikari.connectionTimeout", 30000L);
		long connectionTimeoutPlus = connectionTimeout + 5000L;
		
		// Loop the maximum pool size plus one.
		for ( int index = 0; index <= maximumPoolSize; ++index )
		{
			// Create a thread to start our runnable in the background so
			// Swing's event dispatch thread can display the output in parallel.
			Runnable runnable = () ->
			{
				String threadName = Thread.currentThread().getName();
				EntityManager entityManager = getEntityManagerFactory().createEntityManager();
				Transactional<Long> tx = (em) ->
				{
					Query query = em.createQuery("select count(p) from Product p");
					Long count = (Long) query.getSingleResult();
					println("Pausing thread " + threadName + " for " + connectionTimeoutPlus + " ms");
					sleep(connectionTimeoutPlus);
					return count;
				};
				try
				{
					Long count = tx.transact(entityManager);
					println("Successful thread " + threadName + "; Count = " + count);
				}
				catch ( RuntimeException ex)
				{
					errorln(ex);
					errorDialog("Failed thread: "+threadName, ex);
				}
				entityManager.close();
			};
			Thread thread = new Thread(runnable, "chaosExhaustConnectionPool-" + index);
			thread.start();
		}
	}
	
	public void chaosQueryCacheJPA()
	{
		// Always perform EntityManager actions within a transaction!
		Transactional<Integer> tx = (em) ->
		{
			int count = 0;
			Set<Product> mergeProductSet = new HashSet<Product>();
			for ( Product product : getProductSet() )
			{
				Product mergeProduct = em.merge(product);
				mergeProduct.setDescription("CHAOS-JPA: " + mergeProduct.getDescription());
				mergeProductSet.add(mergeProduct);
				++count;
			}
			setProductSet(mergeProductSet);
			return count;
		};
		Integer count = tx.transact(getEntityManager(), reuseCache());
		println("JPA updated " + count + " product descriptions with CHAOS!");
	}
	
	public void chaosQueryCacheMEM()
	{
		int count = 0;
		for ( Product product : getProductSet() )
		{
			product.setDescription("CHAOS-MEM: " + product.getDescription());
			++count;
		}
		println("MEM updated (without transaction) " + count + " product descriptions with CHAOS!");
	}
	
	public void chaosQueryCacheSQL()
	{
		String sql = "update product set description = concat('CHAOS-SQL: ', description)";
		int count = sqlAction(getEntityManagerFactory(), sql);
		println("SQL updated " + count + " product descriptions with CHAOS!");
	}
	
	/**
	 * Dispatch hyperlinks from the lesson to local method invocations.
	 * The markdown for hyperlinks in the lesson is declared like this:
	 * 
	 *	 [description](!hyperLink)
	 */
	@Override
	public void dispatchHyperLink(String hyperLink)
	{
		switch ( hyperLink )
		{
			case "generateXmlSchemaFromString": generateXmlSchemaFromString(); break;
			case "generateXmlSchemaFromDom": generateXmlSchemaFromDom(); break;
			case "generateXmlSchemaValidatorFromDom": generateXmlSchemaValidatorFromDom(); break;
			case "displayEntityManagerFactoryProperties": displayEntityManagerFactoryProperties(); break;
			case "marshalProducts": marshalProducts(); break;
			case "unmarshalProducts": unmarshalProducts(); break;
			case "entityStateProduct": printEntityState(Product.class); break;
			case "persistProducts": persistProducts(); break;
			case "mergeProducts": mergeProducts(); break;
			case "queryProducts": queryProducts(0, MAX_RESULT_COUNT, null); break;
			case "processProducts": processProducts(); break;
			case "disposeProducts": disposeProducts(); break;
			case "removeProducts": removeProducts(); break;
			case "extensionHashCodes": extensionHashCodes(); break;
			case "extensionEquality": extensionEquality(); break;
			case "extensionToString": extensionToString(); break;
			case "roundtripJAXBValid": roundtripJAXBValid(); break;
			case "roundtripJAXBInvalid": roundtripJAXBInvalid(); break;
			case "roundtripJPA": roundtripJPA(); break;
			case "searchProducts": searchProducts(); break;
			case "chaosExhaustConnectionPool": chaosExhaustConnectionPool(); break;
			case "chaosQueryCacheJPA": chaosQueryCacheJPA(); break;
			case "chaosQueryCacheMEM": chaosQueryCacheMEM(); break;
			case "chaosQueryCacheSQL": chaosQueryCacheSQL(); break;
		}
	}

	/*
	 * Create a JMenuBar to display JMenuItem(s) to trigger
	 * your own exploratory methods.
	 */
	public JMenuBar createMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		
		// Context Menu
		{
			JMenu contextMenu = new JMenu("Context");
			// Context: Generate XML Schema from String
			{
				JMenuItem menuItem = new JMenuItem("Generate XML Schema from String");
				menuItem.addActionListener((event) -> generateXmlSchemaFromString());
				contextMenu.add(menuItem);
			}
			// Context: Generate XML Schema from DOM
			{
				JMenuItem menuItem = new JMenuItem("Generate XML Schema from DOM");
				menuItem.addActionListener((event) -> generateXmlSchemaFromDom());
				contextMenu.add(menuItem);
			}
			// Context: Generate XML Schema Validator from DOM
			{
				JMenuItem menuItem = new JMenuItem("Generate XML Schema Validator from DOM");
				menuItem.addActionListener((event) -> generateXmlSchemaValidatorFromDom());
				contextMenu.add(menuItem);
			}
			// Context: Display Entity Manager Factory Properties
			{
				JMenuItem menuItem = new JMenuItem("Display Entity Manager Factory Properties");
				menuItem.addActionListener((event) -> displayEntityManagerFactoryProperties());
				contextMenu.add(menuItem);
			}
			menuBar.add(contextMenu);
		}

		// BindXml Menu
		{
			JMenu bindXmlMenu = new JMenu("BindXml");
			// BindXml: Products Sub-Menu
			{
				JMenu productsMenu = new JMenu("Products");
				// BindXml Products: Unmarshal Menu Item
				{
					JMenuItem menuItem = new JMenuItem("Unmarshal");
					menuItem.addActionListener((event) -> unmarshalProducts());
					productsMenu.add(menuItem);
				}
				// BindXml Products: Marshal Menu Item
				{
					JMenuItem menuItem = new JMenuItem("Marshal");
					menuItem.addActionListener((event) -> marshalProducts());
					productsMenu.add(menuItem);
				}
				bindXmlMenu.add(productsMenu);
			}
			menuBar.add(bindXmlMenu);
		}
		
		// EntityState Menu
		{
			JMenu entityStateMenu = new JMenu("EntityState");
			// EntityState: Products Sub-Menu
			{
				JMenuItem menuItem = new JMenuItem("Product");
				menuItem.addActionListener((event) -> printEntityState(Product.class));
				entityStateMenu.add(menuItem);
			}
			menuBar.add(entityStateMenu);
		}
		
		// Persist Menu
		{
			JMenu persistenceMenu = new JMenu("Persistence");
			// Persistence: Products Sub-Menu
			{
				JMenu productsMenu = new JMenu("Products");
				// Persistence Products: Persist Menu Item
				{
					JMenuItem menuItem = new JMenuItem("Persist");
					menuItem.addActionListener((event) -> persistProducts());
					productsMenu.add(menuItem);
				}
				// Persistence Products: Merge Menu Item
				{
					JMenuItem menuItem = new JMenuItem("Merge");
					menuItem.addActionListener((event) -> mergeProducts());
					productsMenu.add(menuItem);
				}
				// Persistence Products: Query Sub-Menu
				{
					JMenu queryMenu = new JMenu("Query");
					// Persistence Products Query: All Menu Item
					{
						JMenuItem menuItem = new JMenuItem("All");
						menuItem.addActionListener((event) -> queryAllProducts());
						queryMenu.add(menuItem);
					}
					// Persistence Products Query: Hold Menu Item
					{
						JMenuItem menuItem = new JMenuItem("Hold");
						menuItem.addActionListener((event) -> queryProducts(0, MAX_RESULT_COUNT, HOLD));
						queryMenu.add(menuItem);
					}
					// Persistence Products Query: Open Menu Item
					{
						JMenuItem menuItem = new JMenuItem("Open");
						menuItem.addActionListener((event) -> queryProducts(0, MAX_RESULT_COUNT, OPEN));
						queryMenu.add(menuItem);
					}
					// Persistence Products Query: Active Menu Item
					{
						JMenuItem menuItem = new JMenuItem("Active");
						menuItem.addActionListener((event) -> queryProducts(0, MAX_RESULT_COUNT, ACTIVE));
						queryMenu.add(menuItem);
					}
					// Persistence Products Query: Closed Menu Item
					{
						JMenuItem menuItem = new JMenuItem("Closed");
						menuItem.addActionListener((event) -> queryProducts(0, MAX_RESULT_COUNT, CLOSED));
						queryMenu.add(menuItem);
					}
					// Persistence Products Query: Canceled Menu Item
					{
						JMenuItem menuItem = new JMenuItem("Canceled");
						menuItem.addActionListener((event) -> queryProducts(0, MAX_RESULT_COUNT, CANCELED));
						queryMenu.add(menuItem);
					}
					productsMenu.add(queryMenu);
				}
				// Persistence Products: Process Menu Item
				{
					JMenuItem menuItem = new JMenuItem("Process");
					menuItem.addActionListener((event) -> processProducts());
					productsMenu.add(menuItem);
				}
				// Persistence Products: Dispose Menu Item
				{
					JMenuItem menuItem = new JMenuItem("Dispose");
					menuItem.addActionListener((event) -> disposeProducts());
					productsMenu.add(menuItem);
				}
				// Persistence Products: Remove Menu Item
				{
					JMenuItem menuItem = new JMenuItem("Remove");
					menuItem.addActionListener((event) -> removeProducts());
					productsMenu.add(menuItem);
				}
				persistenceMenu.add(productsMenu);
			}
			menuBar.add(persistenceMenu);
		}
		
		// Extension Menu
		{
			JMenu extensionMenu = new JMenu("Extension");
			// Extension: HashCodes
			{
				JMenuItem menuItem = new JMenuItem("HashCodes");
				menuItem.addActionListener((event) -> extensionHashCodes());
				extensionMenu.add(menuItem);
			}
			// Extension: Equality
			{
				JMenuItem menuItem = new JMenuItem("Equality");
				menuItem.addActionListener((event) -> extensionEquality());
				extensionMenu.add(menuItem);
			}
			// Extension: ToString
			{
				JMenuItem menuItem = new JMenuItem("ToString");
				menuItem.addActionListener((event) -> extensionToString());
				extensionMenu.add(menuItem);
			}
			menuBar.add(extensionMenu);
		}

		// Roundtrip Menu
		{
			JMenu roundtripMenu = new JMenu("Roundtrip");
			// Roundtrip: JAXB
			{
				JMenu roundtripJaxbMenu = new JMenu("JAXB");
				// Roundtrip JAXB: Valid
				{
					JMenuItem menuItem = new JMenuItem("Valid");
					menuItem.addActionListener((event) -> roundtripJAXBValid());
					roundtripJaxbMenu.add(menuItem);
				}
				// Roundtrip JAXB: Invalid
				{
					JMenuItem menuItem = new JMenuItem("Invalid");
					menuItem.addActionListener((event) -> roundtripJAXBInvalid());
					roundtripJaxbMenu.add(menuItem);
				}
				roundtripMenu.add(roundtripJaxbMenu);
			}
			// Roundtrip JPA: Valid
			{
				JMenuItem menuItem = new JMenuItem("JPA");
				menuItem.addActionListener((event) -> roundtripJPA());
				roundtripMenu.add(menuItem);
			}
			menuBar.add(roundtripMenu);
		}

		// Search Menu
		{
			JMenu searchMenu = new JMenu("Search");
			// Search: Products
			{
				JMenuItem menuItem = new JMenuItem("Products");
				menuItem.addActionListener((event) -> searchProducts());
				searchMenu.add(menuItem);
			}
			menuBar.add(searchMenu);
		}

		// Chaos Menu
		{
			JMenu chaosMenu = new JMenu("Chaos");
			// Chaos: Exhaust Connection Pool
			{
				JMenuItem menuItem = new JMenuItem("Exhaust Connection Pool");
				menuItem.addActionListener((event) -> chaosExhaustConnectionPool());
				chaosMenu.add(menuItem);
			}
			// Chaos: Query Cache
			{
				JMenu chaosQueryCacheMenu = new JMenu("Query Cache");
				// Chaos: Query Cache JPA
				{
					JMenuItem menuItem = new JMenuItem("JPA");
					menuItem.addActionListener((event) -> chaosQueryCacheJPA());
					chaosQueryCacheMenu.add(menuItem);
				}
				// Chaos: Query Cache MEM
				{
					JMenuItem menuItem = new JMenuItem("MEM");
					menuItem.addActionListener((event) -> chaosQueryCacheMEM());
					chaosQueryCacheMenu.add(menuItem);
				}
				// Chaos: Query Cache SQL
				{
					JMenuItem menuItem = new JMenuItem("SQL");
					menuItem.addActionListener((event) -> chaosQueryCacheSQL());
					chaosQueryCacheMenu.add(menuItem);
				}
				chaosMenu.add(chaosQueryCacheMenu);
			}
			menuBar.add(chaosMenu);
		}

		return menuBar;
	}
	
	/**
	 * Print the management state(s) of a given entity type.
	 * 
	 * @param <T> The generic entity type.
	 * @param type The entity class type.
	 */
	private <T extends Serializable> void printEntityState(Class<T> type)
	{
		println("Entity Managment State");
		println("Legend:");
		println("  c1 = first level cache");
		println("  c2 = second level cache");
		println("  in = initialized");
		println("  pr = proxied");
		Cache cache2 = getEntityManagerFactory().getCache();
		if ( Product.class == type )
		{
			println("Products [size = " + getProductSet().size() + "]");
			for ( Product product : getProductSet() )
			{
				String productId = product.getPartNum();
				boolean xin = isInitialized(product);
				boolean xpr = isProxy(product);
				printEntityState("	", product, productId, cache2, xin, xpr);
			}
		}
	}

	private void logSummaryStatistics()
	{
		if ( SessionFactoryUtil.logSummaryStatistics(getEntityManagerFactory()) )
			println("Summary statistics have been logged.");
		else
			println("Summary statistics are not enabled!!!");
	}

	@Override
	protected void logSummaryStatistics(ActionEvent event)
	{
		logSummaryStatistics();
	}
}
