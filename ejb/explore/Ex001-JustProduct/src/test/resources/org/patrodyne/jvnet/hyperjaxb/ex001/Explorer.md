# HiSrc HyperJAXB Ex001 - Product Model

This project is the first exploration of the **HiSrc HyperJAXB Maven Plug-in**. It generates Java source code containing JAXB and JPA annotations and shows you how to design a data model (for products) and implement a persistence JAR by focusing primarily on your XML [schema][3] and associated [bindings][4]. This is an example of *Schema Driven Design*.

There is a lot to learn in this exploration. *HyperJAXB* is a powerful plug-in that generates Java code that can synchronize your data with almost any SQL database. The same code can read and write XML instances, too. It is XML Schema driven and supports customizations. It includes data caching and connection pooling, too. That is a lot. The key to using this plug-in is to learn XML Schema grammar well. It will help if you have experience with it. But if you don't, this exploration will show you how it works and it let's you can learn as you go.

> This project includes a Swing application named *Explorer* to demonstrate features of the *HiSrc HyperJAXB* plug-in. This *Explorer* application presents a lesson with actions for real-time experimentation. Feel free to modify your copy of the [Explorer.java][7] source file by adding or modifying the action methods with your own investigative code. The `Explorer` class is an extension of `AbstractEntityExplorer` which contains the more boring mechanics of this implementation. Feel free to create an `Explorer` class in your own projects to help explain the purpose of your work too.

> This **Explorer** application is a *development tool* and is omitted from your Maven generated artifact; although, you could place it in your main package if you want to build a Swing based application.

> **About AbstractEntityExplorer:** Projects can create their own custom Explorer by extending [AbstractEntityExplorer][6] and writing an HTML lesson page plus `JMenuItem`(s) to trigger exploratory code. An `AbstractEntityExplorer` implementation (like the one you see here) displays three panels: an HTML lesson, a print console and an error (logging) console. The lesson file is read as a resource relative to the implementation (i.e. `Explorer`) class. Text is sent to the print console by calling `println(text)` and error messages are sent to the error console by calling `errorln(msg)`. Additionally, 'standard out' / 'standard error' streams are sent to these respective consoles, too.

## HiSrc HyperJAXB Maven Plug-in

The *HiSrc HyperJAXB Maven Plug-in* generates Java source code containing JAXB and JPA annotations from your XML Schema(s). This allows you to design a data model and implement a persistence JAR to synchronize your data with almost any database. The same classes can be used to unmarshal and marshal XML files or messages into Java objects.

You configure this plug-in in your Maven, for details review this Explorer's [pom.xml][8]; here is a summary of the plug-in's declaration:

**pom.xml**

~~~
<!-- mvn hisrc-hyperjaxb:help -Ddetail=true -->
<!-- mvn hisrc-hyperjaxb:generate -->
<plugin>
    <groupId>org.patrodyne.jvnet</groupId>
    <artifactId>hisrc-hyperjaxb-maven-plugin</artifactId>
    ...
</plugin>
~~~

The plug-in configuration in the [pom.xml][8] defines the location of the XML Schema and Binding files to drive the generation of the JAXB/JPA sources. By default, the Java sources are output to the target path for inclusion in your build. The most pertinent configuration directives are included in this demonstration; however, most are default values and could be omitted in your future projects. For help, use this Maven command to display the details:

~~~
mvn hisrc-hyperjaxb:help -Ddetail=true
~~~

> Note: The [HiSrc HyperJAXB Maven Plug-in][9] is an extension of the [HiSrc HigherJAXB Maven Plug-in][10]. The *HyperJAXB* plug-in adds JPA annotations to the generated classes and methods and are compatible with the JAXB annotations and configuration provided by the *HigherJAXB* plug-in.

## Product Model

This diagram for [Product Model][11] shows the entity relationships between the Java JPA classes generated from this project's [Product.xsd][12] (XML schema) and [Product.xjb][13] (XJC bindings). It is a simple model that generates two Java classes and one Enumeration:

+ `Product` - represents a purchasable item and identified by a unique part number.
+ `Stageable` - super class with workflow attributes.
+ `Stage` - an enumeration to list the workflow stages.

It is an intentionally simple schema because there is much to learn about configuration and customizations in your first exploration.

Your [Product Model][11] represents items that a customer may wish to include in a purchase order. Future explorations will include a "catalog-product" model and a "purchase-order-product" model that demonstrate multi-table relationships; but, this project will focus on a simpler model.

### XML Instance

This project includes fictitious test data, represented by [XML][1] instances. Here is a sample:

~~~
<?xml version="1.0" encoding="UTF-8"?>
<ns2:Product PartNum="ZHRN-U2615" Created="2022-05-04T16:04:28.981" Updated="2022-05-04T16:04:28.981" Stage="Open"
    xmlns:ns2="http://jvnet.patrodyne.org/hyperjaxb/ex001/model">
    <Picture>☕</Picture>
    <Description>hot beverage</Description>
    <Price>49.26</Price>
</ns2:Product>
~~~

> Note: The *Picture* element is a `string` containing graphical UNICODE characters for everyday objects.

The *Explorer* tool includes actions to *unmarshal* the XML instances into JAXB/JPA objects/entities and actions to persist these entities to a local test database. The tool provides several other JAXB/JPA actions to manipulate these instances and you can add your own experiments, too.

### XML Schema

The [Product.xsd][12] schema declares:

+ an element named *Product* with an anonymous complex type containing an attribute for *PartNum* and a sequence of simple elements for *Picture*, *Description* and *Price*. Each *Product* is defined uniquely by its *PartNum*. There are two unused attributes for *UOM* (unit of measure) and *QPU* (quantity per unit) that you will explore later on.
+ a simple type named *sku* used by the *PartNum* element to restrict its values to the given format.
+ a simple type named *money* used by the *Price* element to specify a two-decimal currency.
+ a complex type named *Stageable* will facilitate the workflow of the *Product* instances. It has three attributes, *Stage*, *Created* and *Updated*. The *Stage* attribute has a simple type named *stage* with the enumeration: *Hold*, *Open*, *Active*, *Closed* and *Canceled*.
+ the *Created* and *Updated* attributes use the built-in schema type `dateTime` to conform to the [ISO_8601][5] formatting standard.
+ custom XML annotations to instruct:
    + the [HyperJAXB][9] extension to declare *PartNum* as the **primary identifier (key)** for *Product*.
    + the [HyperJAXB][9] extension to enable the second level cache for *Product*.
    + the [HyperJAXB][9] extension to rename the *Created* and *Updated* database column names, as given.
    + the [HyperJAXB][9] ORM extension to add a `StageableListener` to *Product*.
    + the [BasicJAXB][20] extension to ignore the *Created* and *Updated* attributes when generating the `hashCode`, `equals` and `toString` methods.

Note: The XJC compiler adds `@XmlRootElement` to top-level elements declared with anonymous complex types. For example your *Product* element generates a `Product` class with the `@XmlRootElement` annotation. This enables JAXB to unmarshal files containing a single XML instance into a `Product` Java object.

### XJC Binding

The [Product.xjb][13] XJC [binding][4] declares:

+ Global bindings that apply to all elements.
    + `generateElementProperty` - setting to `false` reduces the use of JAXBElement for better interoperability.
    + `generateIsSetMethod` - setting to `false` (default) avoids some NPE issues.
    + `localScoping` - setting to `false` conforms to JPA section 2.1.
    + `serializable uid` - a numeric value like `YYYYMMDD` conforms to JPA section 2.1.
+ Local bindings that apply to *selected* elements and/or types.

#### Discovering [Product.xsd][12] nodes using [xmllint][21] and xpath.

The XJC binding compiler can use the [Product.xjb][13] binding file to customize the [Product.xsd][12] schema. The binding file can customize either global or local schema nodes or both. Local components are *selected* using **xpath** expressions. The tricky part is picking the correct expression. One tool that can help is [xmllint][21]. In shell mode, `xmllint` provides an interactive CLI to navigate your schema file. You can experiment within the shell to pick the `xpath` that selects the node you are after.

> Hint: Optionally, you can use [rlwrap][22] to improve the interactive CLI behavior of [xmllint][21]. If you find that the `xmllint` shell does not support *backspace* or *up-arrow / down-arrow* to navigate the CLI then you can prefix the `xmllint` command with `rlwrap`. The demonstration below includes this option.

In a terminal and in your project, change to the `src/main/resources` sub-path. List the files and you should see:

~~~
$ ls -l
-rw-rw-r-- 1 rick tapestry  945 Jul  8 16:13 ehcache-podb.xml
-rw-r--r-- 1 rick tapestry 2141 Jul  8 16:12 Product.xjb
-rw-rw-r-- 1 rick tapestry 3150 Jul 11 16:19 Product.xsd
~~~

Next, start the `xmllint` shell (`rlwrap` is optional) and navigate as shown. The listing below has been clipped for brevity.

~~~
$ rlwrap xmllint --shell Product.xsd

> / setns xsd=http://www.w3.org/2001/XMLSchema
cd /xsd:schema

schema > cat //xsd:complexType[@name='Stageable']
<complexType name="Stageable">
    ...
    </complexType>

schema > cat //xsd:complexType[not(@name='Stageable')]
 -------
<complexType>
    ...
    <attribute name="PartNum" type="tns:sku" use="required">
        <annotation> <appinfo> <hj:id/> </appinfo> </annotation>
    </attribute>
</complexType>

schema > bye
~~~

Hopefully, that worked for you; but, if you got lost, don't worry because *xpath* can be tricky. The above demo gives you an idea of how you might discover an *xpath*.

Here is what the listing did:

+ The listing above starts by setting a namespace prefix to be `xsd`. The shell needs to know the prefix before it can navigate an *xpath*.
+ Next, you used the `cat` command to view the contents of the complex type named `Stageable`.
+ Finally, you used an *xpath* that displays all complex types that do not have that name. 

Now, take a look at [Product.xjb][13] and you will see a section that uses and *xpath* to select complex types that do not have the name `Stageable`.

+ First, the section locates `Product.xsd` to be a file in the same sub-directory and the XJB file.
+ Second, it sets *xpath* to focus on the node at `/xsd:schema`.
+ Third, it contains a nested binding for an *xpath* similar to the demo above but also excludes complex types containing the name `Join`.

~~~
<jaxb:bindings schemaLocation="Product.xsd" node="/xsd:schema" >
    <jaxb:bindings node="//xsd:complexType[not(@name='Stageable' or contains(@name, 'Join'))]" multiple="true">
        ...
    </jaxb:bindings>
</jaxb:bindings>
~~~

OK, that's the hard part and learning *xpath* is a matter of some trial and error. The benefit is that the customizations in an XJB file give you a powerful way to tailor the generated JPA / JAXB classes to your needs without changing the XSD file, itself. However, you can apply customizations in the XSD file, too. You can use the approach that best meets your needs.

One more thing, after selecting complex types that do not have the name `Stageable` or having a name that contains the text "Join", The binding declares these customizations:

~~~
<inh:extends>org.patrodyne.jvnet.hyperjaxb.ex001.model.Stageable</inh:extends>
<hj:entity cacheable="false">
    <orm:entity-listeners>
        <orm:entity-listener class="org.patrodyne.jvnet.hyperjaxb.ex001.orm.StageableListener"></orm:entity-listener>
    </orm:entity-listeners>
</hj:entity>
~~~

The customizations will apply to the anonymous complex type of the `Product` element and to other complex types that you may add to the schema. The customizations direct the XJC compiler to generate the Java entities, as follows:

+ the `Product` and other matches will extend the super class named `Stageable`.
+ the `Product` and other matches will be annotated with this entity listener:
    + `@EntityListeners({ StageableListener.class })`

### Drop/Create Database Automatically

This project reads its JPA properties from [persistence.properties][15] and one of [persistence-h2.properties][29] or [persistence-pg.properties][30]. These properties include JPA declarations that enable ORM implementations, like [Hibernate][17], to create the database (and DDL scripts) automatically. When *Explorer* is launched, the JPA/ORM will drop the local test database then recreate a fresh set of tables.

~~~
javax.persistence.schema-generation.database.action=drop-and-create
javax.persistence.schema-generation.scripts.action=drop-and-create
javax.persistence.schema-generation.scripts.create-target=target/test-database-sql/podb-create.sql
javax.persistence.schema-generation.scripts.drop-target=target/test-database-sql/podb-drop.sql
~~~

When you exit *Explorer*, the test database is preserved until the next launch. This allows you to inspect the database using the provided script or any other of your SQL tools.

~~~
$ ./sql-cli-podb.sh

Welcome to H2 Shell 2.1.210 (2022-01-17)

sql> show columns from product;

FIELD       | TYPE                   | NULL | KEY | DEFAULT
----------- | ---------------------- | ---- | --- | -------
part_num    | CHARACTER VARYING(255) | NO   | PRI | NULL
description | CHARACTER VARYING(255) | YES  |     | NULL
picture     | CHARACTER VARYING(255) | YES  |     | NULL
price       | NUMERIC(12, 2)         | YES  |     | NULL
stage       | CHARACTER VARYING(255) | YES  |     | NULL
created     | TIMESTAMP              | YES  |     | NULL
updated     | TIMESTAMP              | YES  |     | NULL

sql> exit
Connection closed
~~~

This project uses the [H2 Database Engine][18] for testing. *H2* is a small, pure Java SQL database server that can *simulate* more advanced databases like [PostgreSQL][19]. Thus, you can develop with *H2* then deploy to your shared application servers in QA or production (using *PostgreSQL*, etc.) later. This project contains a shell script, `sql-cli-podb.sh`, to run the *H2* SQL tool to explore your local database, as shown above.

> Note: Your *H2* local database is at `target/test-database/podb.mv.db`. It is created after you run *Explorer*. It is a single-user MVStore database so you must exit *Explorer* before running the SQL tool.

## Exploration

This tool provides actions that you can take to process data in the [Product Model][11] using the JAXB and JPA frameworks. It provides a tool bar with these buttons:

+ Clear the output and error consoles.
+ Increase/Decrease the text size in the output/error consoles.
+ Toggle XML schema validation.
+ Reuse the first level entity cache.
+ Clear the second level entity cache.
+ Reopen the entity manager session and database connection.
+ Log summary statistics.

The actions can be invoked from the menu bar or using the links provided in the lesson below.

### Context Menu

These actions can be invoked from this list, as links, or from the menu bar, as sub-items:

+ [Generate XML Schema From String](!generateXmlSchemaFromString)
+ [Generate XML Schema From Dom](!generateXmlSchemaFromDom)
+ [Generate XML Schema Validator From Dom](!generateXmlSchemaValidatorFromDom)
+ [Display EntityManagerFactory Properties](!displayEntityManagerFactoryProperties)

When your *Explorer* application launches, it reads the configuration files from your `src/test/resources` path. These links show the original distribution versions:

+ [jvmsystem.properties][14] - JVM System configuration.
+ [persistence.properties][15] - JPA Persistence configuration.
    + [persistence-h2.properties][29] - JPA Persistence [H2Database][18] configuration.
    + [persistence-pg.properties][30] - JPA Persistence [PostgreSQL][19] configuration.
+ [simplelogger.properties][16] - [SLF4J][2] Logging configuration.

Take a look at your `src/test/resources/persistence*.properties`. These files have two sections: *JPA* and *Hibernate*. The JPA section contains properties that apply to all ORM implementations while the *Hibernate* section contains ORM specific properties. Overall, this file provides the configuration for these components:

+ JDBC database connection ([H2][18]).
+ Database schema generation ([PostgreSQL][19] dialect).
+ Database connection pool provider ([HikariCP][23]).
+ Second level entity cache provider ([EhCache][24]).

The Maven build for *Explorer* reads the [Product.xsd][12] and [Product.xjb][13] files and generates Java source code for the JAXB context.

#### Initialize JAXB

The *Explorer* application invokes its `initialLession()` method to create a Java `JAXBContext` instance. The `JAXBContext` object is used to create an `Unmarshaller` and `Marshaller` to convert XML data to/from Java objects. The `JAXBContext` object is created from the classes generated by the *HyperJAXB* plug-in which is based on your [Product.xsd][12] and [Product.xjb][13] files. You can use the `JAXBContext` object to display its internal representation of the XML Schema using either [Generate XML Schema From String](!generateXmlSchemaFromString) or [Generate XML Schema From Dom](!generateXmlSchemaFromDom).

> Note: The second DOM based representation provides a way to create an unmarshaller/marshaller that can validate the XML data agasinst the schema.

#### Initialize JPA

Also in the `initialLession()` method, *Explorer* loads the `EntityManagerFactory` (EMF) properties, resolves the *Persistence Unit Name*, creates the EMF and creates an initial `EntityManager`.

> Note: When the *Explorer* initializes the EMF, several JPA activities are triggered. Take a moment to browse the *error* console (right side, bottom panel, scroll to the top) to review logged events. You will see events related to Hibernate, HikariCP, JPA, EhCache  and SQL. This application uses [SLF4J][2] as its logging framework and logging is configured using [simplelogger.properties][16].

#### Collect All Context Properties

Also in the `initialLession()` method, *Explorer* collects the external configuration properties from your files plus the internal *default* properties from the component services. You can review all these properties using [Display EntityManagerFactory Properties](!displayEntityManagerFactoryProperties).

> Hint: Carefully review all of the EMF properties. The *external* properties are set in this project's configuration files. The *internal* properties extend the list by including all the possible properties that can be configured together with the current values determined by the related service. Generally, the internal default values are optimal. But you should know what the default values are!

> Note: The default values for some internal settings depend on the current service provider and you may have different providers between development and production environments. For example, the default for `hibernate.jdbc.fetch_size` is determined by the [H2][18] driver and will likely be different (but remain optimal) for production (i.e. [PostgreSQL][19]).

#### Create JPA RoundtripTest

Also in the `initialLession()` method, *Explorer* creates an instance of the *HyperJAXB* `RoundtripTest` case. This is a JUnit test that is used during the Maven build to verify that the sample XML data can be unmarshalled, persisted, loaded and marshalled back to the same XML data. *Explorer* creates an instance for your direct experimentation.

> Hint: In your new projects, you can uncomment this line from your `pom.xml` to cause the *HyperJAXB* plug-in to generate the `RoundtripTest.java` class; however, this project has its own copy with a few customizations.

~~~ 
    <!-- Generates RoundtripTest.java for unit testing.
    <roundtripTestClassName>RoundtripTest</roundtripTestClassName>
    -->
~~~

### BindXml Menu

These actions can be invoked from this list, as links, or from the menu bar, as sub-items:

+ [Unmarshal Products](!unmarshalProducts)
+ [Marshal Products](!marshalProducts)

This project includes examples of XML instances of *Product* data. These examples can be found in the `src/test/example/products` sub-directory. To load these examples into the *Explorer*, [unmarshal products](!unmarshalProducts) will use the `JAXBContext` to read and parse the XML instances into a property  holding a `Set<Product>` within the application.

> Hint: You can clear the output and error consoles using the first button in the toolbar.

You can view the set, in the output console, using [marshal products](!marshalProducts) to transform each `Product` in the `Set` to its XML representation.

> Note: By default, the application does not perform an XML Schema Validation when unmarshalling/marshalling. You can enable validation by toggling the *flag* button in the tool bar: Red flag for validation OFF and green flag for validation ON.

### EntityState Menu

These actions can be invoked from this list, as links, or from the menu bar, as sub-items:

+ [Entity State, Product](!entityStateProduct)

After [unmarshalling products](!unmarshalProducts) into this application, you can review the [Entity State, Product](!entityStateProduct). The listing shows the state of the entity for each product. After unmarshalling but prior to persistence, the products are unmanaged entities or just plain *data transfer objects*. The listing shows each product is not in the first or second level cache, all are initialized and none are proxied.

first level cache
: JPA provides an entity cache, also know as the first level cache, to store managed entities for use with an `EntityManager` session.

second level cache
: JPA supports a second level cache, to store entity values for use with an `EntityManagerFactory` lifetime.

initialized
: Simple entities are always initialized; otherwise, the ORM determines when a proxied or collection entity has been initialized.

proxy
: A proxy in the ORM world is an automatically generated instance that substitutes for your entity object. The proxy represents an instance which has not been populated with data from the database yet, but only knows its own primary key. In other words, the proxy is *lazily* loaded. Whenever a property is accessed, *then* the proxy instance will try to *eagerly* load the data from the database. The catch is the property can only be loaded within the same managed context; otherwise, a LazyInitializationException is thrown.

### Persistence Menu

These actions can be invoked from this list, as links, or from the menu bar, as sub-items:

+ [Persist Products](!persistProducts)
+ [Merge Products](!mergeProducts)
+ [Query Products](!queryProducts)
+ [Process Products](!processProducts)
+ [Dispose Products](!disposeProducts)
+ [Remove Products](!removeProducts)

#### Persist Products

After [unmarshalling products](!unmarshalProducts) into this application, you can [Persist Products](!persistProducts) to the local test database. The logging console shows the SQL used to INSERT each product into the database, scroll the contents to review.

> Hint: You can control the SQL logging using the properties in your `simplelogger.properties` file.

~~~
    org.slf4j.simpleLogger.log.org.hibernate.SQL=DEBUG
    org.slf4j.simpleLogger.log.org.hibernate.type.descriptor.sql=TRACE
~~~

Review the [Entity State, Product](!entityStateProduct) and confirm that *Explorer's* set of entities are in the first and second level cache. In fact, the entities are being managed by the `EntityManager` and the `EntityManagerFactory` caches.

For a bit of excitement, try to persist the products a second time. JPA refuses to comply.

~~~
Caused by:
  javax.persistence.PersistenceException:
  detached entity passed to persist:
  org.patrodyne.jvnet.hyperjaxb.ex001.model.Product
~~~

What happened? Examine the method in `Explorer.java` that is used to persist the products:

~~~
public void persistProducts()
{
    // Always perform EntityManager actions within an EntityTransaction!
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
    Integer count = tx.transact(getEntityManager(), reuseCache());
    println("Persist returned " + count + " count.");
}
~~~

A functional interface named `Transactional` is declared, as `tx`, with  a code block to loop over the `Set<Product>` and persist each product. This interface wraps your code block with a "begin work", "commit work" / "rollback work" pattern. Your code block is committed unless an exception thrown, in which case, all pending synchronizations are rolled back.

Then, the `tx` invokes its `transact(getEntityManager(), reuseCache())` method by passing an `EntityManager` instance and the `reuseCache()` flag into the transaction.

Initially, the `reuseCache()` flag is set to `CacheOption.CLEAN`. This causes the transaction to flush and clean the first level cache *before* invoking your code. Experience has shown that performing JPA in a clean transaction is the most predictable practice.

Because the first level cache has been cleared, the `em` sees the products as *detached* and the `PersistenceException` above is thrown!

Double check the [Entity State, Product](!entityStateProduct) and you will see `c1` is false for all the products. However, `Product` is configured to be `Cacheable` and is still in the second level cache (unless the cache timed out).

> Note: Your [pom.xml][8] declares `org.hibernate:hibernate-jcache` and `org.ehcache:ehcache` dependencies to implement the JPA second level cache for this project. [EhCache][24] is configured using [ehcache-podb.xml][25] and it has defined the default timeout for entities in the second level cache to be 240 seconds. Your project finds this configuration file at `src/main/resources/ehcache-podb.xml`.

Use the "drum and red eraser" button in the tool bar to clear the second level cache. Try to persist the products again. A new exception is thrown:

~~~
Caused by:
   javax.persistence.PersistenceException:
   org.hibernate.exception.ConstraintViolationException: could not execute statement
Caused by:
    org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException:
    Unique index or primary key violation: "public.PRIMARY_KEY_E ON public.product(part_num)"
~~~

The *persist* action made it a little farther. It made it to the database tier where a `primary key violation` was detected, of course. The same entities with the same primary keys cannot be inserted twice.

An alternative exploration is to set the `reuseCache()` flag is set to `CacheOption.REUSE`. First, restart *Explorer* or [Remove Products](!removeProducts) to regain the initial status. Then toggle the "green recycle triangle" button in the tool bar. This causes *Explorer* to keep (reuse) the first level cache between transactions.

Repeat the steps to [unmarshalling products](!unmarshalProducts) and [Persist Products](!persistProducts) *twice*. This time the second attempt succeeds! Why?

Because the entities [remain managed](!entityStateProduct) by the same `EntityManager` instance, the EM determined that *persist* did not need to hit the database; thus, avoiding the `primary key violation`.

#### Merge Products

Restart *Explorer* or [remove products](!removeProducts) and toggle the "reuse 1st level cache" button to off in order to regain the initial status.

After [unmarshalling products](!unmarshalProducts) into this application, you can [Merge Products](!mergeProducts) to the local test database. The logging console shows the SQL used to SELECT then INSERT each product into the database, scroll the contents to review.

In JPA, the *merge* action returns a managed instance. It will use the supplied entity, and return a managed copy. *Merge* compares the supplied entity with the value from the database, if any. If the supplied entity is new and does exist in the database then the supplied entity will be persisted; if not new, *merge* copies the supplied entity onto the managed entity. In either case, a second managed entity is returned and the supplied entity is not affected. Normally, the managed entity should be used instead of the original (supplied) entity.

*Merge* selects the entity from the database or uses the cached entity before copying the supplied entity onto a new or existing managed entity. *Persist* verifies the cache does not contain the supplied entity then attempts to insert the supplied entity into the database. The original entity becomes managed when the insert succeeds; otherwise an exception is thrown. *Persist* is more efficient when you know that data has not yet been stored.

[Merge Products](!mergeProducts) repeatedly to verify success and that no exception occurs. Experiment by clearing the 1st and 2nd level caches.

#### Query Products

After [unmarshalling products](!unmarshalProducts) and [persisting products](!persistProducts) or [merging products](!mergeProducts) to your local test database, you can [Query Products](!queryProducts). The logging console shows the SQL used to SELECT each product into the database, scroll the contents to review.

This project uses the *JPA Criteria API* to define its queries. The *Criteria API* is used to define queries for entities. These queries are type-safe, and portable and easy to modify by changing the syntax. The *JPA Metamodel API* is mingled with the *Criteria API* to model persistent entity for criteria queries.

> Note: The major advantage of the *Criteria API* is that syntax errors can be detected earlier during compile time; otherwise, string based JPQL queries and *JPA Criteria* based queries are same in performance and efficiency.

The *JPA Metamodel* is configured in the [pom.xml][8] using `org.hibernate:hibernate-jpamodelgen` as an *annotation processor* on `maven-compiler-plugin`. During compilation, the metamodel is generated and output to `target/generated-sources/apt`. A metamodel class is created for each entity. The package and name of each metamodel class is the same as its entity plus an underscore. For example, the entity class `Product.java` has an associated metamodel class named, `Product_.java`. Similarly, `Stageable.java` has `Stageable_.java`.

The *Criteria API* and *Metamodel API* are used in *Explorer* to select products:

~~~
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
~~~

The metamodel is used to identify the `Product_.stage` field in a type-safe way.

Also, the query is configured to use Hibernate's query cache, if available.

~~~
TypedQuery<Product> query = em.createQuery(cq);
query.setHint("org.hibernate.cacheable", true);
List<Product> entities = query
    .setFirstResult(start)
    .setMaxResults(count)
    .getResultList();
~~~

The results can be paginated using the `start` and `count` integer values.

##### More on 2nd Level Cache Configuration

There are several locations that contribute to the 2nd level cache configuration:

+ [persistence.properties][15] - Hibernate cache properties.
    + [persistence-h2.properties][29] - JPA Persistence [H2Database][18] configuration.
    + [persistence-pg.properties][30] - JPA Persistence [PostgreSQL][19] configuration.
+ [Product.xjb][13] - disable entity caching unless enabled in XSD.
+ [Product.xsd][12] - enable entity caching for specific element (i.e. Product).
+ [ehcache-podb.xml][25] - set default and custom cache regions.
+ `Query` instance - hint to use 2nd level query cache when available.

> Note: The XJB sets the policy to not cache entities, generally. The XSD enables your caching policy for specific entities that are long-lived and non-volatile. Query caching is enabled on a query by query basis.

#### Process Products

After [unmarshalling products](!unmarshalProducts) and [persisting products](!persistProducts) or [merging products](!mergeProducts) to your local test database, you can [Process Products](!processProducts).

As entities are [processed](!processProducts), they goes through several life-cycle stages. Here is how this project enumerates the life cycle stages.

When an entity is instantiated, it is in an **open** stage until it can be validated. When it is ready then it can move to the **active** stage; otherwise, it must be put on **hold** and escalated for review. When **active**, it is available for general processing and when all processing is complete and actions have been satisfied then the entity can be **closed**. An entity on **hold** can be **canceled** or fixed and re-opened. An **open** entity can be **canceled** before it becomes **active**. An **active** entity may return to **hold** for review based on processing issues. A **closed** entity should be deleted, after a time, based on the disposal policy.

+ `HOLD` - Escalated for review to fix issues.
+ `OPEN` - Being prepared for activation.
+ `ACTIVE` - Ready for processing, in-progress.
+ `CLOSED` - Actions complete normally. Immutable. Disposable.
+ `CANCELED` - Can/should not be processed. Immutable. Disposable.

Best practice is to limit stages to these five canonical phases and use the entity itself as the context for further interpretation of each stage. This provides for context flexibility while preserving the canonical stages during transaction processing.

Best practices:

+ **Hold** a parent entity when any of its children are on hold.
+ **Open** a parent entity when any of its children are open and:
    + No other children are on hold.
    + Other children are open, canceled or closed.
+ **Activate** a parent entity when any children are active and:
    + No other children are open or on hold.
    + Other children are active, canceled or closed.
+ **Close** a parent entity after all it children are closed/canceled.
+ **Cancel** a parent entity when all it children are canceled.
+ **Delete** parent/children when parent has been closed/canceled, per disposal policy.

#### Dispose Products

**Practical Guide to Records and Information Management (RIM) Disposal**

Organizations must properly destroy unneeded records to:

+ Protect their sensitive data.
+ Comply with
    + The General Data Protection Regulation (GDPR),
    + The Consumer Privacy Act
    + And other privacy laws.
+ Comply with their own records and information management (RIM) policies.

**Reasons to Dispose Records**

By [disposing](!disposeProducts) unneeded records, organizations boost efficiency by lowering storage costs and spending less time and money searching for records required for various business purposes.

However, organizations face the following data disposal challenges:

+ An ill-defined destruction process.
+ Legal requirements and restrictions concerning data destruction.
+ The decision of what to retain.
+ Missing or inadequate controls.

The following tips will help your organization overcome these challenges and take charge of its data.

**Best Practices**

+ Create a metadata standard. Metadata simplifies searches for paper and electronic documents and should contain records retention data as well.
+ Establish and maintain a records retention schedule. This policy document identifies the length of time records must be retained for legal and operational purposes.
+ Develop and document a disposal process outlining the steps needed to destroy records in accordance with the company’s RIM policy. Departments across the organization should collaborate in developing this process.
+ Validate and implement the disposal process. *Use pilot programs to test the process to ensure it works as expected!*
+ Manage vendors. Third-party organizations like [IronMountain][26] that specialize in records retention, storage and destruction can help you properly destroy records. However, you should ensure that any third parties meet the requirements spelled out in service-level agreements.
+ Monitor and adjust your process as needed. As your organization and documents retention and destruction regulations evolve, so should your policies on these matters.
+ Establish a document hold process. You need to know what to do when the destruction of records is delayed for regulatory or business reasons.

#### Remove Products

After [unmarshalling products](!unmarshalProducts) and [persisting products](!persistProducts) or [merging products](!mergeProducts) to your local test database, you can [Remove Products](!removeProducts). The logging console shows the SQL used to DELETE each product from the database, scroll the contents to review.

### Extension Menu

These actions can be invoked from this list, as links, or from the menu bar, as sub-items:

+ [Extension HashCodes](!extensionHashCodes)
+ [Extension Equality](!extensionEquality)
+ [Extension ToString](!extensionToString)

The XJC compiler from JAXB does the actual code generation using extensions from the [HiSrc BasicJAXB][20] framework. Then JAXB can be used to unmarshal/marshal XML instances to/from Java objects.

> Note: 

XJC generates POJO classes with the standard Java `Object` methods:

+ `hashCode()` - numeric value used for object identification or indexing. 
+ `equals(obj)` - determines when and how to objects are equal.
+ `toString()` - textual representation of an object.

By default, these methods work at the object level. For example, two distinct `Product` object instances are considered _not equal_ even when they contain the same product values because they have different object identifiers. The *HiSrc BasicJAXB Runtime Library* provides strategies to implement these and other `Object` methods that take all field values into account.

The *HiSrc HyperJAXB Maven Plug-in* in your [pom.xml][8] is configured to generate the *hashCode()*, *equals(...)*, and *toString()* overrides from implementations in the *BasicJAXB* dependency. Further, the configuration specifies that the overrides use `strategic` approach.

~~~
<plugin>
    <groupId>org.patrodyne.jvnet</groupId>
    <artifactId>hisrc-hyperjaxb-maven-plugin</artifactId>
    <executions>
        <execution>
            ...
            <configuration>
                ...
                <generateHashCode>true</generateHashCode>
                <generateEquals>true</generateEquals>
                <generateToString>true</generateToString>
                <!-- overrideHET: strategic | simple[123] -->
                <overrideHET>strategic</overrideHET>
            </configuration>
        </execution>
    </executions>
</plugin>
~~~

In the *strategic* approach, each generated class implements these three interfaces: `Equals2`, `HashCode2`, `ToString2`. The implementations include all fields in the class, unless a given field is annotated to be ignored by *BasicJAXB* in the XML schema. Logging for the `Equals2` interface is [configurable][16]. At the `TRACE` level, "field not equal" messages are generated in the log. These messages are verbose and should only be enabled for testing or deep trouble-shooting. Also, logging for the `ToString2` interface is [configurable][16]. It has three levels of detail corresponding to the `INFO`, `DEBUG` and `TRACE` log levels. The *strategic* approach provides for customization and runtime tuning.

Alternatively, `overrideHET` can be set to *simple*, *simple1*, *simple2* or *simple3*. In the *simple* implementations, no interfaces are used; instead, the override is in-lined in the class at build time. The choice for *simple[123]* affects the details for the *toString()* override, only. The *equals(...)* override does not log the "field not equal" messages. The *simple* approach does not provide customization or runtime tuning and but can be configured during the build.

> Note: HET stands for HashCode, Equals, ToString.

#### HashCode Extension

After [unmarshalling products](!unmarshalProducts), you can review the [hash codes](!extensionHashCodes) extension.

~~~
Object Id = 2c9b3e72 -> Entity Id = 14382b3c
Object Id = 7b65f136 -> Entity Id = 6703c106
Object Id = 33c6a23f -> Entity Id = c19ce51b
...
~~~

The *hash code* extension accumulates a hash value for each field in your generated JAXB/JPA class, unless annotated to be ignored. The result is a *hash code* that is sensitive to your data values. The listing above compares the default Java `Object` hash to your more sensitive `Product` hash.

#### Equals Extension

After [unmarshalling products](!unmarshalProducts), you can review the [equality](!extensionEquality) extension.

~~~
Searching for index: 6; Object Id = 525bd5fe -> Entity Id = cc51d4e
...
Comparing index: 5
Comparing index: 6; Match: Object Id = 525bd5fe -> Entity Id = cc51d4e
Comparing index: 7
...
~~~

The *equals* extension tests each field in your generated JAXB/JPA class for equality, unless annotated to be ignored. The listing above searches for a randomly selected `Product` instance by iterating over the full set and testing all fields for equality.

In *strategic* mode when logging is set to `...DefaultEqualsStrategy=TRACE`, the log console contains every non-equal comparison. This is great for trouble-shooting but should be disabled in production.

~~~
...
13:03:00:922 TRACE DefaultEqualsStrategy - Objects are NOT equal!
13:03:00:922 TRACE DefaultEqualsStrategy - LHS: {(Product@5fe4b426).picture} -> 🍵
13:03:00:957 TRACE DefaultEqualsStrategy - RHS: {(Product@4f3445f).picture} -> 🍫
...
~~~

#### ToString Extension

After [unmarshalling products](!unmarshalProducts), you can review the [to string](!extensionToString) extension.

~~~
Product@5d7a5eda[OPEN, 🌾, ear of rice, 52.81, ZHRN-U1F33E]
Product@77c9356e[OPEN, 🍈, melon, 16.83, ZHRN-U1F348]
Product@3f7175ea[OPEN, 🍜, steaming bowl, 76.16, ZHRN-U1F35C]
~~~

The *to string* extension generates a human-readable string for your generated JAXB/JPA class, unless annotated to be ignored. The listing above shows the representation when `DefaultToStringStrategy=INFO` in *strategic* mode. These log levels produce different details:

+ `INFO` - immediate field values only.
+ `DEBUG` - above plus field names.
+ `TRACE` - above plus children and full class names. 

> Note: 'Product' does not have children. A future exploration will show more details.

### Roundtrip Menu

These actions can be invoked from this list, as links, or from the menu bar, as sub-items:

+ [Roundtrip JAXB Valid](!roundtripJAXBValid)
+ [Roundtrip JAXB Invalid](!roundtripJAXBInvalid)
+ [Roundtrip JPA](!roundtripJPA)

#### Roundtrip JAXB Valid

After [unmarshalling products](!unmarshalProducts), you can review the [Roundtrip JAXB Valid](!roundtripJAXBValid) action.

~~~
ProductA vs ProductB (ZHRN-U1F36F): EQUAL
ProductA vs ProductB (ZHRN-U1F34A): EQUAL
ProductA vs ProductB (ZHRN-U1F368): EQUAL
~~~

This action loops over the set of 'Product's and marshalls *productA* into its XML representation then unmarshalls the XML into a new *productB* and uses 'productA.equals(productB)' to determine equality.

#### Roundtrip JAXB Invalid

After [unmarshalling products](!unmarshalProducts), you can review the [Roundtrip JAXB Invalid](!roundtripJAXBInvalid) action.

This action is mischievous. It picks the first `Product` instance from the set (*product1A*) and marshalls it into *product1A-xml*. It renames the `Price` tag to `Cost` into *product1B-xml and unmarshalls that into *product1B*. The results are marshalled for display here and in the output console.

**BEFORE: Product1A vs Product1B**

~~~
<Product PartNum="ZHRN-U1F375" ... >
    ...
    <Price>68.62</Price>
</Product>

<Product PartNum="ZHRN-U1F375" ... >
    ...
    <Cost>68.62</Cost>
</Product>
~~~

**AFTER: Product1A vs Product1B**

~~~
<Product PartNum="ZHRN-U1F375" ... >
    ...
    <Price>68.62</Price>
</Product>

<Product PartNum="ZHRN-U1F375" ... >
    ...
</Product>
~~~

Look closely and you will see that unmarshalling 'product1B-xml' failed to parse the `Cost` tag. You should not be surprised because that tag name is not defined in your [Product.xsd][12]. The logging console detects the issue, too. In one case, *price* is set but not in the other case!

~~~
14:40:00:044 TRACE DefaultEqualsStrategy - Objects are NOT equal!
14:40:00:044 TRACE DefaultEqualsStrategy - LHS: {(Product@5b874bff).price.isSet} -> true
14:40:00:045 TRACE DefaultEqualsStrategy - RHS: {(Product@6f79f9a4).price.isSet} -> false
~~~

**Next**, toggle the *flag* button in your tool bar to enable "Schema Validation is ON" and invoke [Roundtrip JAXB Invalid](!roundtripJAXBInvalid) again. This time an exception is thrown to notify you of the problem.

~~~
[org.xml.sax.SAXParseException; lineNumber: 5; columnNumber: 11; cvc-complex-type.2.4.a:
    Invalid content was found starting with element 'Cost'. One of '{Price}' is expected.]
~~~


#### Roundtrip JPA

The [Roundtrip JPA](!roundtripJPA) action executes the JUnit class normally used by the *HiSrc HyperJAXB Maven Plug-in* during its *test* phase. By default, it searches for sample files in your `src/test/samples` sub-directory. It *unmarshalls* each sample file into *instance1* then uses one `EnityManager` to *merge* it to generate *instance2* and a second `EnityManager` to find the sample by id which becomes *instance3*. Finally, it compares *instance2* with *instance3* and *instance1* with *instance3* using the strategic equals method. If both comparisons are equal then the test is a success.

~~~
14:04:42:720 INFO RoundtripTest - Testing directory [../src/test/samples].
14:04:42:829 INFO RoundtripTest - Testing sample, SUCCESS [Product01.xml].
~~~

**Next** a bit of (too much) cleverness, edit your `src/main/resources/Product.xsd` to enable the `UOM` attribute by removing the comment lines shown here:

~~~
<element name="Product">
    <complexType>
        ...
        <!--
        <attribute name="UOM" type="string" default="each"/>
        -->
    </complexType>
</element>
~~~

Restart *Explorer* to trigger a rebuild and return to this point in the lesson. Execute the [Roundtrip JPA](!roundtripJPA).

This time the JPA `RoundtripTest` fails because the `UOM` is not *set* on the left hand side!

~~~
15:07:18:037 TRACE DefaultEqualsStrategy - Objects are NOT equal!
15:07:18:037 TRACE DefaultEqualsStrategy - LHS: {(Product@2b265333).uom.isSet} -> false
15:07:18:037 TRACE DefaultEqualsStrategy - RHS: {(Product@6fcb9b0f).uom.isSet} -> true
15:07:18:039 ERROR RoundtripTest - Testing sample [Product01.xml] failed the check.
junit.framework.AssertionFailedError: Objects NOT equal. Use DEBUG for location details.
15:07:18:041 INFO RoundtripTest - Testing sample, FAILURE [Product01.xml].
15:07:18:041 DEBUG RoundtripTest - Testing samples, finish
java.lang.AssertionError: Testing summary [1/1] failed the check. Check previous errors for details.
~~~

But Why?

The `src/main/resources/Product.xsd` declares a default value for the attribute named `UOM`; the default is "each". The [JAXB specification handles XML Schema default values][27] by causing XJC to generated this *field* and *property* for `UOM`:

~~~
@XmlAttribute(name = "UOM")
protected String uom;
public String getUOM() {
    if (uom == null) {
        return "each";
    } else {
        return uom;
    }
}
public void setUOM(String value) {
    this.uom = value;
}
~~~

When an XML Schema instance (i.e. `Product01.xml`) is unmarshalled without an explicit value for `UOM`, the `uom` field in the Java `Product` instance will be *null*. But, the accessor *getUOM()* will return the *XML Schema* default value, "each". This is a bit clever. More clever still, if the object is marshalled back into an XML instance then the marshaller can compare the field with the accessor to determine when this attribute is *unset* and omit it from the output. In this way, JAXB can round-trip the original XML to a POJO and back into an XML instance that replicates the original.

In JAXB, an attribute is *set* when its field is set; and optionally, the accessor can return a default value when the field is *unset*!

**But Wait.** When a JAXB instance with an *unset* value is persisted to a database (or saved to a file, etc.), the accessor provides the property value, in the case of *getUOM()*, the value "each". Later, when JPA retrieves this `Product` entity from the database, the *field* will be populated with the value, "each". The JPA context (or a file context, etc.) does not know about the JAXB XML Schema default value.

OK, JAXB has a clever approach to handling its default values, but outside of JAXB things cannot be as clever. Because of this cleverness, the BasicJAXB *hashCode()*, *equals(...)* and even the *toString()* extensions treat entities with *set* values differently from the, otherwise, same entity with *unset* (and defaulted) values.

And this difference explains why ...

~~~
15:07:18:037 TRACE DefaultEqualsStrategy - Objects are NOT equal!
15:07:18:037 TRACE DefaultEqualsStrategy - LHS: {(Product@2b265333).uom.isSet} -> false
15:07:18:037 TRACE DefaultEqualsStrategy - RHS: {(Product@6fcb9b0f).uom.isSet} -> true
~~~

**And now for something completely different, more cleverness**

Edit your `src/main/resources/Product.xsd` to disable (comment out) the `UOM` attribute and enable the `QPU` element. JAXB and XJC generate this code:

~~~
@XmlElement(name = "QPU", defaultValue = "1")
protected int qpu;
public int getQPU() {
    return qpu;
}
public void setQPU(int value) {
    this.qpu = value;
}
~~~

Observe the following:

+ The Java type for `qpu` is `int`, a non-nullable primitive type.
+ The XML Schema default value is declared in the `@XmlElement` annotation.
+ The accessor *getQPU()* does not provide the default when the field is null.

JAXB handles XML Schema default values for elements in a different way than it does for attributes.

Restart *Explorer* to trigger a rebuild and return to this point in the lesson. Execute the [Roundtrip JPA](!roundtripJPA).

~~~
16:40:01:437 INFO RoundtripTest - Testing sample, SUCCESS [Product01.xml].
16:40:01:438 DEBUG RoundtripTest - Testing samples, finish
~~~

This time, the test is (almost) successful. 

**But, But Wait.** If you look closely at the logging console, you'll observe that the value for the `QPU` element is `0` instead of the default `1`?!

~~~
17:33:47:033 DEBUG RoundtripTest - Result object:

<ns2:Product PartNum="ZHRN-U2615" Created="2022-05-04T16:04:28.981" Updated="2022-05-04T16:04:28.981" Stage="Open" xmlns:ns2="http://jvnet.patrodyne.org/hyperjaxb/ex001/model">
    <Picture>☕</Picture>
    <Description>hot beverage</Description>
    <Price>49.26</Price>
    <QPU>0</QPU>
</ns2:Product>
~~~

The reason is more [JAXB cleverness][27]: Default attribute values apply when *attributes are missing*, and default element values apply when *elements are empty*. 

The sample for your project, is missing the element `QPU`; thus, the JAXB trigger for emptiness is not satisfied.

To fix this, edit `src/test/samples/Product01.xml` and add this empty `<QPU></QPU>` element to `Product`. Restart *Explorer* to trigger a rebuild and return to this point in the lesson. Execute the [Roundtrip JPA](!roundtripJPA).

This time, the test is successful. 

~~~
17:46:02:299 DEBUG RoundtripTest - Result object:

<ns2:Product PartNum="ZHRN-U2615" Created="2022-05-04T16:04:28.981" Updated="2022-05-04T16:04:28.981" Stage="Open" xmlns:ns2="http://jvnet.patrodyne.org/hyperjaxb/ex001/model">
    <Picture>☕</Picture>
    <Description>hot beverage</Description>
    <Price>49.26</Price>
    <QPU>1</QPU>
</ns2:Product>

17:46:02:303 INFO RoundtripTest - Testing sample, SUCCESS [Product01.xml].
17:46:02:303 DEBUG RoundtripTest - Testing samples, finish
~~~

> Note: As distributed, the `RoundtripTest` class in this project is annotated to be ignored; otherwise, the example above would fail at build time and you would not be able to explore it here. You should include remove (or comment out) the `@Ignore` annotation for your other projects, in general.

~~~
@Ignore("XML Schema contains default value(s).")
public class RoundtripTest
~~~

### Search Menu

These actions can be invoked from this list, as links, or from the menu bar, as sub-items:

+ [Search Products](!searchProducts)

After [unmarshalling products](!unmarshalProducts) into this application, you can [search](!searchProducts) for a match.

Often a remote XML instance of an existing entity is submitted into a project. Because your JAXB/JPA classes implement deep, field-level *hashCode()* and *equals(...)* methods, you can iterate over a list of existing products and test for equality.

### Chaos Menu

These actions can be invoked from this list, as links, or from the menu bar, as sub-items:

+ [Chaos Exhaust Connection Pool](!chaosExhaustConnectionPool)
+ [Chaos QueryCache JPA](!chaosQueryCacheJPA)
+ [Chaos QueryCache MEM](!chaosQueryCacheMEM)
+ [Chaos QueryCache SQL](!chaosQueryCacheSQL)

#### Exhaust Connection Pool

Your project's [pom.xml][8] declares a dependency on `org.hibernate:hibernate-hikaricp` to configure the JDBC connection pool provider to be [HikariCP][23]. Plus, your [persistence.properties][15] and one of [persistence-h2.properties][29] or [persistence-pg.properties][30] configures the connection provider class to be a custom HyperJAXB class from [HikariCPHyperConnectionProvider][28].
			
> Note: `HikariCPHyperConnectionProvider` is a minor extension of *Hibernate's* `HikariCPConnectionProvider` that sends the JDBC *catalog* and *schema* properties forward to *HikariCP*, when present.

As a best practice, applications should use a connection pool whenever database connections are used. A connection pool provides the following benefits:

+ Promotes connection object reuse.
+ Quickens the process of getting a connection.
+ Reduces the number of times new connection objects are created.
+ Controls the amount of resources spent on maintaining connections.
+ When connections are limited, the pool will wait until one is available.


Your project uses defaults for most of the [HikariCP][23] configuration and you can review all of the settings in the logging console. Here are a few settings that you should know:

~~~
12:16:40:786 DEBUG HikariConfig - connectionTimeout...............30000
12:16:40:788 DEBUG HikariConfig - idleTimeout.....................600000
12:16:40:789 DEBUG HikariConfig - maxLifetime.....................1800000
12:16:40:789 DEBUG HikariConfig - maximumPoolSize.................10
12:16:40:790 DEBUG HikariConfig - minimumIdle.....................10
12:16:40:791 DEBUG HikariConfig - validationTimeout...............5000
12:16:40:791 DEBUG HikariConfig - schema.........................."public"
~~~

Generally, a connection pool *just works*. Let's explore what happens when all connections to the pool are leased out.

[Exhaust](!chaosExhaustConnectionPool) the pool, now. The *Explorer* method launches parallel threads until the `maximumPoolSize` is exceeded by one. Each thread starts a transaction to query the database and intentionally waits until the `connectionTimeout` is exceeded by 5000 ms. The pool provider leases out connections until the last attempt then waits. You can see the pool status in the logging console. After the timeout period, *Explorer* forwards the pool exception to a pop-up dialogue for your review.

#### Hibernate Query Cache

Hibernate provides a query cache using the second level cache. Query result sets may also be cached. This is only useful for queries that are run frequently with the same parameters. To use the query cache you must first enable it:

~~~
hibernate.cache.use_query_cache=true
~~~

This setting causes the creation of two new second level cache regions - one holding cached query result sets (`org.hibernate.cache.StandardQueryCache`), the other holding timestamps of the most recent updates to queryable tables (`org.hibernate.cache.UpdateTimestampsCache`). Note that the query cache does not cache the state of the actual entities in the result set; it caches only identifier values and results of value type. So the query cache should always be used in conjunction with the second-level cache.

Most queries do not benefit from caching, so by default queries are not cached. To enable caching, call `query.setCacheable(true)` or in JPA, `query.setHint("org.hibernate.cacheable", true)`. This call allows the query to look for existing cache results or add its results to the cache when it is executed.

#### Query Cache JPA Update

After [removing all products](!removeProducts), [unmarshalling products](!unmarshalProducts) and [persisting products](!persistProducts) or [merging products](!mergeProducts) to your local test database, you can explore the *query cache*.

[Update the database products using JPA](!chaosQueryCacheJPA) then [review](!extensionToString) the in-memory products.

~~~
14:07:31:729 DEBUG TimestampsCacheEnabledImpl - Pre-invalidating space [public.PRODUCT], timestamp: 6791854947241984
14:07:31:752 DEBUG TimestampsCacheEnabledImpl - Invalidating space [public.PRODUCT], timestamp: 6791854701576192
~~~

First, [query](!queryProducts) all the products and observe how the query cache is populated.

~~~
14:09:39:431 DEBUG QueryResultsCacheImpl - Checking cached query results in region: default-query-results-region
14:09:39:431 DEBUG QueryResultsCacheImpl - Query results were not found in cache
14:09:39:618 DEBUG QueryResultsCacheImpl - Caching query results in region: default-query-results-region; timestamp=6791855224262656
~~~

Second, [query](!queryProducts) all the products again and observe how the query cache is used.

~~~
14:11:30:952 DEBUG QueryResultsCacheImpl - Checking cached query results in region: default-query-results-region
14:11:30:953 DEBUG TimestampsCacheEnabledImpl - [public.PRODUCT] last update timestamp: 6791854701576192, result set timestamp: 6791855224262656
14:11:30:954 DEBUG QueryResultsCacheImpl - Returning cached query results
~~~

#### Query Cache MEM Update

After performing the *Query Cache JPA Update*, you can explore the *query cache* with MEM update.

[Update the in-memory products using Java](!chaosQueryCacheMEM) then [review](!extensionToString) the in-memory products.

First, [query](!queryProducts) all the products and observe how:

+ the modified (and managed) products were flushed to the database, prior to the query.
+ the query cache is populated.

Repeatedly [query](!queryProducts) all the products again and observe how the query cache is used.

#### Query Cache SQL Update

After performing the *Query Cache MEM Update*, you can explore the *query cache* with SQL update.

[Update the database products using SQL](!chaosQueryCacheSQL)then [review](!extensionToString) the in-memory products. The in-memory list does *not show* that "CHAOS-SQL" has been prepended to the descriptions because the update was mad outside of the JPA context!

Next, [query](!queryProducts) all the products and observe that the second level cache is used to satisfy the query and a [review](!extensionToString) of the list does *not show* that "CHAOS-SQL".

Next, use the "drum and eraser" button in the toolbar to clear the second level cache then [query](!queryProducts) all the products and [review](!extensionToString) the list to find that "CHAOS-SQL" has been retrieved from the database.

Experiment with the toolbar and different sequences of the chaos to see the effects.

# End of this Exploration

-30-

<!-- References -->

[1]: https://www.w3.org/TR/xml/
[2]: https://www.slf4j.org/
[3]: https://www.w3.org/TR/xmlschema-0/#Intro
[4]: https://docs.oracle.com/cd/E17802_01/webservices/webservices/docs/1.5/tutorial/doc/JAXBUsing4.html
[5]: https://en.wikipedia.org/wiki/ISO_8601
[6]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/runtime/src/main/java/org/patrodyne/jvnet/hyperjaxb/explore/AbstractEntityExplorer.java?ts=4
[7]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/explore/Ex001-JustProduct/src/test/java/org/patrodyne/jvnet/hyperjaxb/ex001/Explorer.java?ts=4
[8]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/explore/Ex001-JustProduct/project-pom.xml?ts=4
[9]: https://github.com/patrodyne/hisrc-hyperjaxb#readme
[10]: https://github.com/patrodyne/hisrc-higherjaxb#readme
[11]: https://raw.githubusercontent.com/patrodyne/hisrc-hyperjaxb/master/ejb/explore/Ex001-JustProduct/src/test/resources/JustProduct.svg
[12]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/explore/Ex001-JustProduct/src/main/resources/Product.xsd?ts=4
[13]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/explore/Ex001-JustProduct/src/main/resources/Product.xjb?ts=4
[14]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/explore/Ex001-JustProduct/src/test/resources/jvmsystem.properties?ts=4
[15]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/explore/Ex001-JustProduct/src/test/resources/persistence.properties?ts=4
[16]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/explore/Ex001-JustProduct/src/test/resources/simplelogger.properties?ts=4
[17]: https://hibernate.org/orm/
[18]: https://www.h2database.com/
[19]: https://www.postgresql.org/
[20]: https://github.com/patrodyne/hisrc-basicjaxb#readme
[21]: https://www.baeldung.com/linux/xmllint
[22]: https://manpages.ubuntu.com/manpages/bionic/man1/rlwrap.1.html
[23]: https://github.com/brettwooldridge/HikariCP#readme
[24]: https://www.ehcache.org/
[25]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/explore/Ex001-JustProduct/src/main/resources/ehcache-podb.xml?ts=4
[26]: https://www.ironmountain.com/blogs/2020/7-data-destruction-best-practices
[27]: https://www.w3.org/TR/xmlschema-0/#OccurrenceConstraints
[28]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/opt/hikaricp/src/main/java/org/patrodyne/jvnet/hyperjaxb/opt/hikaricp/HikariCPHyperConnectionProvider.java?ts=4
[29]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/explore/Ex001-JustProduct/src/test/resources/persistence-h2.properties?ts=4
[30]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/explore/Ex001-JustProduct/src/test/resources/persistence-pg.properties?ts=4
