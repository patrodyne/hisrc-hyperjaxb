package org.patrodyne.jvnet.hyperjaxb.ex001;

import static java.lang.Integer.toHexString;
import static java.lang.System.identityHashCode;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static org.jvnet.hyperjaxb3.ejb.util.EntityManagerFactoryUtil.filterProperties;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToggleButton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.hibernate.SessionFactory;
import org.jvnet.jaxb2_commons.test.Bogus;
import org.patrodyne.jvnet.basicjaxb.explore.AbstractExplorer;
import org.patrodyne.jvnet.basicjaxb.validation.SchemaOutputDomResolver;
import org.patrodyne.jvnet.basicjaxb.validation.SchemaOutputStringResolver;
import org.patrodyne.jvnet.hyperjaxb.ex001.model.Product;
import org.patrodyne.jvnet.hyperjaxb.opt.hibernate.SessionFactoryUtil;
import org.patrodyne.jvnet.hyperjaxb.opt.hikaricp.HikariCPUtil;
import org.xml.sax.SAXException;

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
public class Explorer extends AbstractExplorer
{
	private static final String WINDOW_TITLE = "HiSrc HyperJAXB Ex001 JustProduct";
	private static final String EXPLORER_HTML = "Explorer.html";

	private RoundtripTest roundtripTest;
	public RoundtripTest getRoundtripTest() { return roundtripTest; }
	public void setRoundtripTest(RoundtripTest roundtripTest) { this.roundtripTest = roundtripTest; }
	
	private JAXBContext jaxbContext;
	public JAXBContext getJaxbContext() { return jaxbContext; }
	public void setJaxbContext(JAXBContext jaxbContext) { this.jaxbContext = jaxbContext; }

	private Marshaller marshaller;
	public Marshaller getMarshaller() { return marshaller; }
	public void setMarshaller(Marshaller marshaller) { this.marshaller = marshaller; }

	private Unmarshaller unmarshaller;
	public Unmarshaller getUnmarshaller() { return unmarshaller; }
	public void setUnmarshaller(Unmarshaller unmarshaller) { this.unmarshaller = unmarshaller; }
	
	private Map<String,String> entityManagerFactoryProperties;
	public Map<String, String> getEntityManagerFactoryProperties() { return entityManagerFactoryProperties; }
	public void setEntityManagerFactoryProperties(Map<String, String> entityManagerFactoryProperties) { this.entityManagerFactoryProperties = entityManagerFactoryProperties; }

	private EntityManagerFactory entityManagerFactory;
	public EntityManagerFactory getEntityManagerFactory() { return entityManagerFactory; }
	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) { this.entityManagerFactory = entityManagerFactory; }

	private EntityManager entityManager;
	public EntityManager getEntityManager() { return entityManager; }
	public void setEntityManager(EntityManager entityManager) { this.entityManager = entityManager; }

	private Product product1;
	public Product getProduct1() { return product1; }
	public void setProduct1(Product product1) { this.product1 = product1; }

	private Product product2;
	public Product getProduct2() { return product2; }
	public void setProduct2(Product product2) { this.product2 = product2; }

	private Product product3;
	public Product getProduct3() { return product3; }
	public void setProduct3(Product product3) { this.product3 = product3; }

	private List<Product> productList;
	public List<Product> getProductList() { return productList; }
	public void setProductList(List<Product> productList) { this.productList = productList; }
	
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

	public void generateXmlSchemaFromString()
	{
		try
		{
			SchemaOutputStringResolver sosr = new SchemaOutputStringResolver();
			getJaxbContext().generateSchema(sosr);
			println("Xml Schema from String:\n\n" + sosr.getSchemaString());
		}
		catch ( IOException ex )
		{
			errorln(ex);
		}
	}
	
	public void generateXmlSchemaFromDom()
	{
		try
		{
			SchemaOutputDomResolver sodr = new SchemaOutputDomResolver();
			getJaxbContext().generateSchema(sodr);
			println("Xml Schema from DOM:\n\n" + sodr.getSchemaDomNodeString());
		}
		catch ( IOException | TransformerException ex )
		{
			errorln(ex);
		}
	}
	
	public void generateXmlSchemaValidatorFromDom()
	{
		try
		{
			if ( (getMarshaller() != null) && (getUnmarshaller() != null) )
			{
				// Generate a Schema Validator from given the JAXB context.
				SchemaOutputDomResolver sodr = new SchemaOutputDomResolver();
				getJaxbContext().generateSchema(sodr);
				SchemaFactory schemaFactory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
				Schema schemaValidator = schemaFactory.newSchema(sodr.getDomSource());
				
				// Configure Marshaller / unmarshaller to use validator.
				getMarshaller().setSchema(schemaValidator);
				getUnmarshaller().setSchema(schemaValidator);
				
				getValidateButton().setSelected(true);
				println("Schema Validation is ON.");
			}
			else
				errorln("Please create marshaller and unmarshaller!");
		}
		catch ( IOException | SAXException ex )
		{
			errorln(ex);
		}
	}
	
	public void displayEntityManagerFactoryProperties()
	{
		// JPA Entity Manager Factory
		EntityManagerFactory emf = getEntityManagerFactory();
		println("\nPersistence Configuration Properties: External:\n");
		Map<String, Object> emfProperties = filterProperties(emf);
		for ( Entry<String, Object> entry : emfProperties.entrySet() )
			println("  " + entry.getKey() + " = " + entry.getValue());
		
		// Hibernate Session Factory
		SessionFactory sf = emf.unwrap(SessionFactory.class);
		println("\nPersistence Configuration Properties: Internal:\n");
		Map<String, Object> hcoProperties = SessionFactoryUtil.gatherProperties(sf, emfProperties);
		hcoProperties.putAll(HikariCPUtil.gatherProperties(sf));
		for ( Entry<String, Object> entry : hcoProperties.entrySet() )
			println("  " + entry.getKey() + " = " + entry.getValue());
	}
	
	public void marshalProduct1()
	{
		marshal("Product1", getProduct1());
	}
	
	public void marshalProduct2()
	{
		marshal("Product2", getProduct2());
	}
	
	public void marshalProduct3()
	{
		marshal("Product3", getProduct3());
	}

	public void marshalProducts()
	{
//		marshal("Products", batch);
	}
	
	public void compareHashCodes()
	{
		String product1HashCode = toHexString(getProduct1().hashCode());
		String product2HashCode = toHexString(getProduct2().hashCode());
		String product3HashCode = toHexString(getProduct3().hashCode());

		String product1IdentityHashCode = toHexString(identityHashCode(getProduct1()));
		String product2IdentityHashCode = toHexString(identityHashCode(getProduct2()));
		String product3IdentityHashCode = toHexString(identityHashCode(getProduct3()));
		
		println("Compare Hash Codes\n");
		println("Product1 hashCode: " + product1HashCode + "; identityHashCode: " + product1IdentityHashCode);
		println("Product2 hashCode: " + product2HashCode + "; identityHashCode: " + product2IdentityHashCode);
		println("Product3 hashCode: " + product3HashCode + "; identityHashCode: " + product3IdentityHashCode);
		println();
	}
	
	public void compareEquality()
	{
		println("Compare Equality\n");
		println("Product1 vs Product2: " + (getProduct1().equals(getProduct2()) ? "EQUAL" : "UNEQUAL"));
		println("Product1 vs Product3: " + (getProduct1().equals(getProduct3()) ? "EQUAL" : "UNEQUAL"));
		println();
	}

	public void compareToString()
	{
		println("Compare ToString\n");
		println("Product1 toString: " + getProduct1().toString());
		println("Product2 toString: " + getProduct2().toString());
		println("Product3 toString: " + getProduct3().toString());
		println();
	}

	public void roundtripValid()
	{
		Product product1A = getProduct1();
		String product1AXml = marshalToString(product1A);
		Product product1B = unmarshalFromString(product1AXml, Product.class);
		println("Product1A vs Product1B: " + (product1A.equals(product1B) ? "EQUAL" : "UNEQUAL"));
		println();
	}

	public void roundtripInvalid()
	{
		// product1BXml is made intentionally invalid!
		Product product1A = getProduct1();
		String product1AXml = marshalToString(product1A);
		String product1BXml = product1AXml.replaceAll("symbol", "simbol");
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

	public void searchProducts()
	{
		Product product1 = Bogus.randomItem(getProductList());
		Product product2 = unmarshalFromString(marshalToString(product1), Product.class);
		int index = getProductList().indexOf(product2);
		println("Search: " + product2);
		println("Found.: " + getProductList().get(index));
		println("Index.: " + index);
		println();
	}
	
	/**
	 * Dispatch hyperlinks from the lesson to local method invocations.
	 * The markdown for hyperlinks in the lesson is declared like this:
	 * 
	 *   [description](!hyperLink)
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
			case "marshalProduct1": marshalProduct1(); break;
			case "marshalProduct2": marshalProduct2(); break;
			case "marshalProduct3": marshalProduct3(); break;
			case "marshalProducts": marshalProducts(); break;
			case "compareHashCodes": compareHashCodes(); break;
			case "compareEquality": compareEquality(); break;
			case "compareToString": compareToString(); break;
			case "roundtripValid": roundtripValid(); break;
			case "roundtripInvalid": roundtripInvalid(); break;
			case "searchProducts": searchProducts(); break;
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

		// Marshal Menu
		{
			JMenu marshalMenu = new JMenu("Marshal");
			// Marshal: Product1
			{
				JMenuItem menuItem = new JMenuItem("Product1");
				menuItem.addActionListener((event) -> marshalProduct1());
				marshalMenu.add(menuItem);
			}
			// Marshal: Product2
			{
				JMenuItem menuItem = new JMenuItem("Product2");
				menuItem.addActionListener((event) -> marshalProduct2());
				marshalMenu.add(menuItem);
			}
			// Marshal: Product3
			{
				JMenuItem menuItem = new JMenuItem("Product3");
				menuItem.addActionListener((event) -> marshalProduct3());
				marshalMenu.add(menuItem);
			}
			// Marshal: Products
			{
				JMenuItem menuItem = new JMenuItem("Products");
				menuItem.addActionListener((event) -> marshalProducts());
				marshalMenu.add(menuItem);
			}
			menuBar.add(marshalMenu);
		}

		// Compare Menu
		{
			JMenu compareMenu = new JMenu("Compare");
			// Compare: HashCodes
			{
				JMenuItem menuItem = new JMenuItem("HashCodes");
				menuItem.addActionListener((event) -> compareHashCodes());
				compareMenu.add(menuItem);
			}
			// Compare: Equality
			{
				JMenuItem menuItem = new JMenuItem("Equality");
				menuItem.addActionListener((event) -> compareEquality());
				compareMenu.add(menuItem);
			}
			// Compare: ToString
			{
				JMenuItem menuItem = new JMenuItem("ToString");
				menuItem.addActionListener((event) -> compareToString());
				compareMenu.add(menuItem);
			}
			menuBar.add(compareMenu);
		}

		// Roundtrip Menu
		{
			JMenu roundtripMenu = new JMenu("Roundtrip");
			// Roundtrip: Valid
			{
				JMenuItem menuItem = new JMenuItem("Valid");
				menuItem.addActionListener((event) -> roundtripValid());
				roundtripMenu.add(menuItem);
			}
			// Roundtrip: Invalid
			{
				JMenuItem menuItem = new JMenuItem("Invalid");
				menuItem.addActionListener((event) -> roundtripInvalid());
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

		return menuBar;
	}
	
	private JToggleButton validateButton;
	public JToggleButton getValidateButton() { return validateButton; }
	public void setValidateButton(JToggleButton validateButton) { this.validateButton = validateButton; }

	public void modifyToolBar()
	{
		getToolBar().addSeparator();
		String validateOffPath = OILPATH+"/actions/flag-red.png";
		String validateOnPath = OILPATH+"/actions/flag-green.png";
		setValidateButton(createImageToggleButton(Explorer.class, validateOffPath, validateOnPath));
		getValidateButton().addActionListener((event) -> toggleValidateSchema(event));
		getValidateButton().setToolTipText("Toggle schema validation");
		getToolBar().add(validateButton);
	}
	
	private void toggleValidateSchema(ActionEvent event)
	{
		JToggleButton toggleButton = (JToggleButton) event.getSource();
		if ( toggleButton.isSelected() )
			generateXmlSchemaValidatorFromDom();
		else
		{
			setMarshaller(createMarshaller(getJaxbContext()));
			setUnmarshaller(createUnmarshaller(getJaxbContext()));
			println("Schema Validation is OFF.");
		}
	}

	@SuppressWarnings("unchecked")
	public void initializeLesson() throws Exception
	{
		// Explore RoundtripTest
		setRoundtripTest(new RoundtripTest());
		
		// Initialize JAXB
		setJaxbContext(getRoundtripTest().getSamplesTest().createContext());
		setMarshaller(createMarshaller(getJaxbContext()));
		setUnmarshaller(createUnmarshaller(getJaxbContext()));
		
		// Initialize JPA
		setEntityManagerFactoryProperties(getRoundtripTest().getEntityManagerFactoryProperties());
		setEntityManagerFactory(getRoundtripTest().createEntityManagerFactory());
		setEntityManager(getEntityManagerFactory().createEntityManager());
		
//		setProductList(createProductList());
	}

	private Marshaller createMarshaller(JAXBContext jaxbContext)
	{
		Marshaller marshaller = null;
		try
		{
			if ( jaxbContext != null )
			{
				marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(JAXB_FORMATTED_OUTPUT, true);
			}
			else
				errorln("Cannot create marshaller because JAXB context is null!");
		}
		catch ( JAXBException ex )
		{
			errorln(ex);
		}
		return marshaller;
	}
	
	private Unmarshaller createUnmarshaller(JAXBContext jaxbContext)
	{
		Unmarshaller unmarshaller = null;
		try
		{
			if ( jaxbContext != null )
			{
				unmarshaller = jaxbContext.createUnmarshaller();
			}
			else
				errorln("Cannot create unmarshaller because JAXB context is null!");
		}
		catch ( JAXBException ex )
		{
			errorln(ex);
		}
		return unmarshaller;
	}

	private void marshal(String label, Serializable instance)
	{
		String ehc = toHexString(instance.hashCode());
		String ihc = toHexString(identityHashCode(instance));
		String productXml = marshalToString(instance);
		// Entity Hash vs Object Hash
		println(label + " XML: (E#=" + ehc + ", O#=" + ihc + ")\n\n" +productXml);
		println();
	}
	
	private String marshalToString(Object instance)
	{
		String xml = null;
		if ( instance != null)
		{
			try ( StringWriter writer = new StringWriter() )
			{
				getMarshaller().marshal(instance, writer);
				xml = writer.toString();
			}
			catch (JAXBException | IOException ex)
			{
				errorln(ex);
			}
		}
		return xml;
	}

	@SuppressWarnings("unchecked")
	private <T> T unmarshalFromString(String xml, Class<?> clazz)
	{
		T instance = null;
		try ( StringReader reader = new StringReader(xml) )
		{
			instance = (T) getUnmarshaller().unmarshal(new StreamSource(reader), clazz).getValue();
		}
		catch (JAXBException ex)
		{
			errorln(ex);
		}
		return instance;
	}
}
