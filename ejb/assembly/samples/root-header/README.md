# Sample: Root Header, One To One

This Maven project demonstrates the generation of JPA Entities from an XML Schema file (xsd) using the [HiSrc HyperJAXB Maven Plugin][9]. The generated classes include JPA and JAXB annotations to support JDBC persistence and XML marshaling/unmarshaling. Further, it uses [HiSrc BasicJAXB XJC Plugins][10] plugins to add custom `hashCode`, `equals`, and `toString` implementations to each generated entity.

### Problem ( from [StackOverflow #78482471](https://stackoverflow.com/questions/78482471/) )

+ How to configure [HiSrc HyperJAXB Maven Plugin][9] to use the `@OneToOne` JPA annotation instead of `@ManyToOne`?

### Solution

[HiSrc HyperJAXB Maven Plugin][9] generates the `@ManyToOne` JPA annotation when a source entity references a single target entity. This is the intended behavior. In absence of other information, this choice provides a foreign reference without inferring ownership. When ownership _is_ present then the target entity's life cycle is bound to the source which is a much stronger choice.

Of course, your design can choose to apply ownership to this type of relation ship, when it is appropriate. To generate a `@OneToOne` relationship, you can define this JAXB binding:

[RootHeader.xjb][49]
~~~
...
<jaxb:bindings node="xs:element[@name='Root']//xs:element[@name='header']">
    <hj:one-to-one>
        <!-- Optional, custom column name.
        <orm:join-column name="HEADER_ROOT_HJID"/>
        -->
    </hj:one-to-one>
</jaxb:bindings>
...
~~~

The above binding is applies on the target `header` element using a customization from the HyperJAXB namespace. Optionally, you can use the `orm` namespace to customize the database column that JPA uses to join the source entity, `Root`, to the target entity, `Header`; otherwise, HyperJAXB will choose an appropriate column name to represent the join.

The JAXB binding file, [RootHeader.xjb][49], is applied to your XML schema file [RootHeader.xsd][50] to generate the JAXB/JPA entities. These classes are generated to `target/generated-sources/xjc/org/example/root_header/` and included in the classpath.

#### Execution

After downloading this [demo (zip)][1] and extracting it to a local folder, you can run the test and/or application using either of two JPA providers:

> **Note:** This demo is a stand-alone Maven project. 

**[Eclipselink][15]**
~~~
mvn -Peclipselink clean test
mvn -Peclipselink clean compile exec:java
~~~

or

**[Hibernate][16]**
~~~
mvn -Phibernate clean test
mvn -Phibernate clean compile exec:java
~~~

This [output][2] shows the test results using *Hibernate*.

##### Persistence: H2 or PostgreSQL

By default, this project uses a local [H2][13] database. The H2 database is automatically created when the application creates its `EntityManagerFactory`.

Alternatively, this project can be re-configured to use your own [PostgreSQL][14] database server.

##### Approach

The [hisrc-hyperjaxb-maven-plugin][9] is configured to generate **JPA/JAXB** classes using the provided [RootHeader.xsd][50] schema. The schema provides the namespace `"http://org.example/root_header"` which **JAXB** uses to create the Java `package` name using its own naming convention.

As the default option, a more advanced implementation of Java's built-in `Object` methods are generated using these **XJC** [hisrc-basicjaxb-plugins][10]. In particular, the sample project uses the `toString` plugin to display *human-readable* representations of the unmarshaled `Root` objects.

> *Note:* The [hisrc-hyperjaxb-maven-plugin][9] extends the [hisrc-higherjaxb-maven-plugin][17] but *pre-configures* several of the **XJC** [hisrc-basicjaxb-plugins][10]; thus, the [hisrc-basicjaxb-runtime][10] dependency is required on the run-time class path. For more help on the configuration details, see ...

~~~
mvn hisrc-hyperjaxb:help -Ddetail=true
~~~

##### Testing

The JUnit test class, [RoundtripTest.java][60], scans for the sample files and invokes the method `super.checkSample(File samle)` to provide each file to the tester. For this project, an `EntityManagerFactory` and a `JAXBContext` are created and each file in the [samples][63] path is:

+ *unmarshaled* to an initial `Root` object
+ *unmarshaled* to an etalon `Root` object
+ A JPA transaction is started.
    + The initial object is *merged* into the persistence session
+ The JPA transaction is committed
+ A new JPA `EntityManager`, *em*, instance is created
    + The new *em* is used to load the persisted `Root` by id
    + The etalon, merged and loaded `Root` objects are compared for equality
+ The *em* is closed and each `Root` object is marshaled for review in the logs

When successful, each object is *marshaled* for logging and your [review][2].

##### Demonstration

A Java standard engine application with a `main(...)` method is at [`org.example.root_header.Main`][42]. This application is executed using:

~~~
mvn -Peclipselink clean compile exec:java -Dexec.args="src/test/samples/root-header-01.xml"
OR
mvn -Phibernate   clean compile exec:java -Dexec.args="src/test/samples/root-header-01.xml"
~~~

<!-- References -->

[1]: https://github.com/patrodyne/hisrc-hyperjaxb/releases/download/2.2.1/hisrc-hyperjaxb-ejb-sample-root-header-2.2.1-mvn-src.zip
[2]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/OUTPUT.txt
[9]: https://github.com/patrodyne/hisrc-hyperjaxb#readme
[10]: https://github.com/patrodyne/hisrc-basicjaxb#readme
[12]: https://jakarta.ee/specifications/xml-binding/
[13]: https://www.h2database.com/
[14]: https://www.postgresql.org/
[15]: https://www.eclipse.org/eclipselink/
[16]: https://hibernate.org/orm/
[17]: https://github.com/patrodyne/hisrc-higherjaxb#readme
[20]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/build-cfg.sh
[21]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/build-inc.sh
[22]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/build.log
[23]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/build.sh
[24]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/pg-create-database.sh
[25]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/pg-recreate-schema.sh
[30]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/pom.xml
[31]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/README.md
[32]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/run.sh
[33]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/sql-cli-h2db.sh
[34]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/sql-web-h2db.sh
[41]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/src/main/java/org/example/root_header/Context.java
[42]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/src/main/java/org/example/root_header/Main.java
[43]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/src/main/resources/jvmsystem.arguments
[44]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/src/main/resources/jvmsystem.properties
[45]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/src/main/resources/META-INF/orm.xml
[46]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/src/main/resources/persistence-h2.properties
[47]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/src/main/resources/persistence-pg.properties
[48]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/src/main/resources/persistence.properties
[49]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/src/main/resources/RootHeader.xjb
[50]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/src/main/resources/RootHeader.xsd
[51]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/src/main/resources/simplelogger.properties
[60]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/src/test/java/org/example/root_header/RoundtripTest.java
[61]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/src/test/resources/persistence-pg-create-database.sql
[62]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/src/test/resources/persistence-pg-recreate-schema.sql
[63]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/root-header/src/test/samples/root-header-01.xml

