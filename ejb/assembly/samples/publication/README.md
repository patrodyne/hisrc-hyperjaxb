# Sample: Publication
## JPA Inheritance plus Many-To-Many

This Maven project demonstrates the generation of JPA Entities from an XML Schema file (xsd) using the [HiSrc HyperJAXB Maven Plugin][13]. The generated code includes [JPA][1] and [JAXB][2] annotations to support JDBC persistence and XML marshaling/unmarshaling. Further, it uses [HiSrc BasicJAXB XJC Plugins][10] to add custom `hashCode`, `equals`, and `toString` implementations to each generated entity.

The XML Schema file, [Publication.xsd][36], is based on this fine article, [Inheritance Strategies with JPA and Hibernate – The Complete Guide](https://thorben-janssen.com/complete-guide-inheritance-strategies-jpa-hibernate/). The schema defines these entities:

+ `Author` associates with many `Publication(s)`
+ `Publication` associates to many `Author(s)`
+ `Blog` inherits from `Publication`
+ `Book` inherits from `Publication`

This project demonstrates the inheritance strategy and many-to-many configuration used to persist these entities to a database.

### Problem ( from [GitHub][7] )

**HyperJAXB** needs a way to select its default **JPA** inheritance strategy and *possibly* limit the generation of the `@Inheritance` annotation only to top-level entities that are inherited. In **HyperJAXB** 2.1.0, the `JOINED` mapping strategy is always added to top-level entities.

> In **HyperJAXB** 2.1.1, commit [1a269e2][94] modified `EntityMapping.java` to check when a root class is not inherited by some other class in the JAXB context *and* to determine if the root class contains at least one basic field. When these conditions are satisfied, the `@Inheritance` annotation is omitted from the root class. See [Issue #1][7].
>
> `boolean isSuperClass(Mapping context, ClassOutline theClassOutline)`
> `boolean containsNonDiscriminatorColumn(Entity entity)`
>
> The second condition is required for an [EclipseLink][5] issue [#1918][95] that SQL inserts for tables with `IDENTITY` primary key must have at least one other column value. Often, this other column is the `DTYPE` column, alone. For example, a root entity that serves as a *one-to-many* container, only.

From the JPA 3.0 specification:

+ [§11.1.24][8] If the *Inheritance* annotation is not specified or if no inheritance type is specified for an entity class hierarchy, the `SINGLE_TABLE` mapping strategy is used.

+ [§2.12][9] An implementation is required to support the **single table per class** hierarchy inheritance mapping strategy and the **joined subclass** strategy. Support for the **table per concrete class** inheritance mapping strategy is optional.

#### Inheritance

With regard to [JPA][1] entity inheritance, **HyperJAXB** uses the `JOINED` inheritance strategy for all root level entities:

~~~
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Inheritance(strategy = InheritanceType.JOINED)
~~~

As of v2.1.1, **HyperJAXB** ...

+ *does* provide a configuration option for alternative [JPA][1] strategies: `SINGLE_TABLE`, `JOINED`, `TABLE_PER_CLASS`.
+ *does not* add the `@Inheritance` annotation on *every* root class, even if the base has no inherited classes in the XML Schema. Instead the root is checked for inheritance to determine when the annotation is required.
+ *does* return `JOINED` as the default inheritance strategy, see [EntityMapping#getInheritanceStrategy(...)][80].

The [JPA][1] specifies how the *persistence provider* should implement the [§11.1.12. DiscriminatorColumn Annotation](https://jakarta.ee/specifications/persistence/3.0/jakarta-persistence-spec-3.0.html#a14530) to:

+ define the discriminator column for the `SINGLE_TABLE` and `JOINED` inheritance mapping strategies.
+ default the name of the discriminator column defaults to `"DTYPE"` and the discriminator type to `STRING`.

> **Note:** The discriminator column can be configured *explicitly* by adding a `@jakarta.persistence.DiscriminatorColumn` annotation to any root entity. For example, the column length can be changed from the default `31` to another length.

Two well-known *persistence provider*s are [EclipseLink][5] and [Hibernate][6] and each provides a different approach:

+ As of v4.0.1, [EclipseLink][5] adds a `DTYPE` column for each root table when the `@Inheritance` annotation is *explicitly* declared with or without sub-tables; without the annotation, a `DTYPE` column is added only when needed.
+ As of v5.6.15.Final, [Hibernate][6] only adds a `DTYPE` column on root tables with sub-tables.

This [demonstration (zip)][20] can be executed using either provider.

#### Many To Many

> Although the GitHub [Issue #1][7] does not reference a `many-to-many` relationship, the sample here includes an example of one (`Publication`-`Author`) to provide a richer context for the *inheritance* issue. Plus, this sample reveals that **HyperJAXB** support for `many-to-many` may need some review.

With regard to [JPA][1] `many-to-many` relationships, **HyperJAXB** extends [JAXB][2] to specify the `many-to-many` *owner* side relationship, this way:

[Publication.xjb (Publication)][39]
~~~
...
<jaxb:bindings node="xs:complexType[@name='Publication']">
    <jaxb:bindings node="//xs:element[@name='authors']">
        <hj:many-to-many>
            <orm:join-table name="PUBLICATION_AUTHOR_JOIN">
                <orm:join-column name="PUBLICATION_ID" />
                <orm:inverse-join-column name="AUTHOR_ID" />
            </orm:join-table>
        </hj:many-to-many>
    </jaxb:bindings>
</jaxb:bindings>
...
~~~

The above binding declares the `Publication` entity to be the *owner* of the relationship and the `Author` entity becomes the target. **HyperJAXB** generates this Java accessor method with [JPA][1] annotations to specify the target for this owner.

**Publication.java**
~~~
...
@ManyToMany(
    targetEntity = Author.class,
    cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH },
    fetch = FetchType.LAZY)
@JoinTable(
    name = "PUBLICATION_AUTHOR_JOIN",
    joinColumns = { @JoinColumn(name = "PUBLICATION_ID") },
    inverseJoinColumns = { @JoinColumn(name = "AUTHOR_ID") } )
public List<Author> getAuthors()
{
    if (authors == null)
        authors = new ArrayList<>();
    return this.authors;
}
...
~~~

**HyperJAXB** extends [JAXB][2] to specify the `many-to-many` *target* side relationship. The *target* is signified using the `mapped-by` attribute.

[Publication.xjb (Author)][39]
~~~
...
<jaxb:bindings node="xs:complexType[@name='Author']">
    <anx:annotate target="class">@jakarta.xml.bind.annotation.XmlType(name = "Author", propOrder = { "firstName", "lastName" })</anx:annotate>
    <jaxb:bindings node="//xs:element[@name='publications']">
        <hj:many-to-many mapped-by="authors"/>
        <anx:annotate target="field">@jakarta.xml.bind.annotation.XmlTransient</anx:annotate>
        <bas:ignored/>
    </jaxb:bindings>
</jaxb:bindings>
...
~~~

The above binding declares the `Author` entity to be the *target* of the relationship and the `Publication#authors` field to be the owner.

In order to avoid cyclic references during marshaling/unmarshaling, a few additional bindings are required on the target side:

+ [BasicJAXB][10] is used to *ignore* the `publications` field during hash, equals and toString operations
+ [HyperJAXB-Annox][14] is used to:
    + remove `publications` field from the XML property order
    + add XML transience to `publications`
+ [PublicationUL][56] is used to add publications to the author's collection after unmarshalling, see [Context#getUnmarshaller()][55].

**HyperJAXB** generates this Java accessor method with [JPA][1] annotations to specify the owner for this target.


**Author.java**
~~~
...
@ManyToMany(
    targetEntity = Publication.class,
    cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH },
    fetch = FetchType.LAZY,
    mappedBy = "authors")
public List<Publication> getPublications()
{
    if (publications == null)
        publications = new ArrayList<>();
    return this.publications;
}
...
~~~

### Solution

This [demonstration (zip)][20] confirms the observations reported in [Issue #1][7] and provides some approaches to address it.

#### [JPA][1] Inheritance Strategy

The *[Inheritance][8]* annotation defines the inheritance strategy to be used for an entity class hierarchy. It is specified on the entity class that is the root of the entity class hierarchy.

> **Note:** If the *[Inheritance][8]* annotation is not specified or if no inheritance type is specified for an entity class hierarchy, the `SINGLE_TABLE` mapping strategy is used.

There are three basic strategies that are used when mapping a class or class hierarchy to a relational database:

+ **a single table per class hierarchy** maps all entities of the inheritance structure to the same database table.

+ **a joined subclass strategy**, in which fields that are specific to a subclass are mapped to a separate table than the fields that are common to the parent class, and a join is performed to instantiate the subclass.

+ **a table per concrete entity class**, in which each entity is mapped to its own table and all the properties of the entity, including the ones inherited are stored in columns of that table.

#### Changing the Inheritance Strategy

**HyperJAXB** implements the inheritance strategy in [EntityMapping#getInheritanceStrategy(...)][80] as a *configurable* default:

~~~
...
if (isRootClass(context, classOutline))
{
    if (entity.getInheritance() != null && entity.getInheritance().getStrategy() != null)
        return InheritanceType.valueOf(entity.getInheritance().getStrategy());
    else
        return jakarta.persistence.InheritanceType.JOINED;
}...
~~~

> **Note:** The **JPA** default inheritance strategy is `SINGLE_TABLE`; however, **HyperJAXB** customizes **JPA** to use `JOINED` when no other strategy is declared.

You can leverage the difference between the **JPA** and **HyperJAXB** implementations to use the former over the latter.

[**HyperJAXB Annox**][14] can be used to remove the **HyperJAXB** generated `@Inheritance` annotation in post-processing. To explore this approach, edit your copy of [Publication.xjb][39] to enable this (commented) binding in the two root elements: `Publication` and `Author`. This will cause the JPA provider to fall back to the default strategy `SINGLE_TABLE`.

`Publication.xjb`
~~~
...
<anx:removeAnnotation target="class" class="jakarta.persistence.Inheritance"/>
...
~~~

Then use Maven's test goal to generate the JPA/JAXB classes for your review. You can use a pre-configured Maven profile to select the JPA provider: [eclipselink][5] or [hibernate][6]. By removing the `@Inheritance` annotation, the providers default ([§11.1.24][8]) to using the `SINGLE_TABLE` mapping.

~~~
mvn -Peclipselink clean test
OR
mvn -Phibernate clean test
~~~

#### Inheritance Strategy Diagrams

The [PublicationTest#testSchemaCrawler2()][74] method generates a diagram of the database tables in your `target/generated-docs` sub-directory. Here are the diagrams for the options available in v2.1.1 of **HyperJAXB**.

+ You can use a pre-configured Maven profile to select the JPA provider: [eclipselink][5] or [hibernate][6].

+ You can configure the test database in [jvmsystem.properties][38] by setting `org.jvnet.hyperjaxb.persistencePropertiesMoreFile` to one of: 

    + `persistence-h2.properties`
    + `persistence-pg.properties` (see [pg-create-database.sh][25])

**Joined Tables**

+ **HyperJAXB** implements the `JOINED` inheritance strategy in [EntityMapping#getInheritanceStrategy(...)][80] when the default entity mapping is not configured. You can configure the inheritance strategy in your binding file, as shown here:

[Publication.xjb][39]
~~~
<hj:default-entity>
    <orm:inheritance strategy="JOINED"/>
</hj:default-entity>
~~~

| DB | EclipseLink         | Hibernate           |
| -- | ------------------- | ------------------- |
| H2 | ![EL-H2-JOINED][82] | ![HB-H2-JOINED][86] |
| PG | ![EL-PG-JOINED][84] | ![HB-PG-JOINED][88] |

**Single Table (explicit) **

+ When the `@Inheritance` annotation or the `orm.xml` equivalent is not present,  the **JPA** default is `SINGLE_TABLE`. You can *explicitly* configure this inheritance strategy in your binding file, as shown here:

[Publication.xjb][39]
~~~
<hj:default-entity>
    <orm:inheritance strategy="SINGLE_TABLE"/>
</hj:default-entity>
~~~

| DB | EclipseLink               | Hibernate                 |
| -- | ------------------------- | ------------------------- |
| H2 | ![EL-H2-SINGLE_TABLE][83] | ![HB-H2-SINGLE_TABLE][87] |
| PG | ![EL-PG-SINGLE_TABLE][85] | ![HB-PG-SINGLE_TABLE][89] |

**Table Per Class**

+ The `TABLE_PER_CLASS` inheritance strategy is option in [JPA][1]. Providers ([eclipselink][5], [hibernate][6], etc.) are not required to implement it and support may be partial. You can explicitly configure this inheritance strategy in your binding file, as shown here:

[Publication.xjb][39]
~~~
<hj:default-entity>
    <orm:inheritance strategy="TABLE_PER_CLASS"/>
</hj:default-entity>
~~~

> **Note:** The `TABLE_PER_CLASS` strategy applied the root ID to each sub-class in an hierarchal instance; thus, the preferred `generated-id` strategy is `TABLE`.

~~~
<hj:default-generated-id ... >
    <orm:column name="id"/>
    <orm:generated-value generator="SEQUENCES" strategy="TABLE"/>
    <orm:table-generator name="SEQUENCES" table="sequences" />
</hj:default-generated-id>
~~~

| DB | EclipseLink                  | Hibernate                    |
| -- | ---------------------------- | ---------------------------- |
| H2 | ![EL-H2-TABLE_PER_CLASS][90] | ![HB-H2-TABLE_PER_CLASS][92] |
| PG | ![EL-PG-TABLE_PER_CLASS][91] | ![HB-PG-TABLE_PER_CLASS][93] |

#### DTYPE: Omit `@Inheritance` from Childless Roots

> **Note:** As of HyperJAXB 2.1.1, the `@Inheritance` is automatically omitted with one exception. The exception is when the omission of a `DTYPE` column creates a sparse insert, see [EclipseLink][5] [Issue #1918][95].

**Single Table (implicit) **

[**HyperJAXB Annox**][14] can be used to remove the **HyperJAXB** generated `@Inheritance` annotation in post-processing. This approach (edit your copy of [Publication.xjb][39]) can be used to remove the binding from the `Author` table. When [eclipselink][5] and [hibernate][6] fall back to the `SINGLE_TABLE` strategy, they both omit the `dtype` column.

##### Demonstration

A Java standard engine application with a `main(...)` method is at [`org.example.pub.Main`][11]. This application is executed using:

~~~
mvn -Peclipselink clean compile exec:java -Dexec.args="src/test/samples/Blog01.xml"
OR
mvn -Phibernate   clean compile exec:java -Dexec.args="src/test/samples/Blog01.xml"
~~~

<!-- References -->

[1]: https://jakarta.ee/specifications/persistence/3.0/jakarta-persistence-spec-3.0.html
[2]: https://jakarta.ee/specifications/xml-binding/4.0/jakarta-xml-binding-spec-4.0.html
[3]: https://www.h2database.com/
[4]: https://www.postgresql.org/
[5]: https://www.eclipse.org/eclipselink/
[6]: https://hibernate.org/orm/
[7]: https://github.com/patrodyne/hisrc-hyperjaxb/issues/1
[8]: https://jakarta.ee/specifications/persistence/3.0/jakarta-persistence-spec-3.0.html#a14891
[9]: https://jakarta.ee/specifications/persistence/3.0/jakarta-persistence-spec-3.0.html#a966
[10]: https://github.com/patrodyne/hisrc-basicjaxb#readme
[11]: https://github.com/patrodyne/hisrc-basicjaxb-annox#readme
[12]: https://github.com/patrodyne/hisrc-higherjaxb#readme
[13]: https://github.com/patrodyne/hisrc-hyperjaxb#readme
[14]: https://github.com/patrodyne/hisrc-hyperjaxb-annox#readme
[20]: https://github.com/patrodyne/hisrc-hyperjaxb/releases/download/2.1.1/hisrc-hyperjaxb-ejb-sample-publication-2.1.1-mvn-src.zip
[21]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/README.md
[22]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/OUTPUT.txt
[23]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/project-pom.xml
[24]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/bin
[25]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/bin/pg-create-database.sh
[26]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/bin/sql-web-h2db.sh
[27]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/bin/run.sh
[28]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/bin/pg-recreate-schema.sh
[29]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/bin/sql-cli-h2db.sh
[30]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src
[31]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/main
[32]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/main/resources
[33]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/main/resources/persistence-pg.properties
[34]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/main/resources/jvmsystem.arguments
[35]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/main/resources/persistence-h2.properties
[36]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/main/resources/Publication.xsd
[37]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/main/resources/persistence.properties
[38]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/main/resources/jvmsystem.properties
[39]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/main/resources/Publication.xjb
[40]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/main/resources/simplelogger.properties
[41]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/main/resources/META-INF
[42]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/main/resources/META-INF/orm.xml
[50]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/main/java
[51]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/main/java/org
[52]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/main/java/org/example
[53]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/main/java/org/example/pub
[54]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/main/java/org/example/pub/Main.java
[55]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/main/java/org/example/pub/Context.java
[56]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/main/java/org/example/pub/PublicationUL.java
[60]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/test
[61]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/test/samples
[62]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/test/samples/Blog01.xml
[63]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/test/samples/Book01.xml
[64]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/test/resources
[65]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/test/resources/persistence-pg-recreate-schema.sql
[66]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/test/resources/persistence-pg-create-database.sql
[70]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/test/java
[71]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/test/java/org
[72]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/test/java/org/example
[73]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/test/java/org/example/pub
[74]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/test/java/org/example/pub/PublicationTest.java
[75]: https://github.com/patrodyne/hisrc-hyperjaxb/tree/master/ejb/assembly/samples/publication/src/test/java/org/example/pub/RoundtripTest.java
[80]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/2.1.0/ejb/plugin/src/main/java/org/jvnet/hyperjaxb/ejb/strategy/mapping/EntityMapping.java
[81]: https://raw.githubusercontent.com/patrodyne/hisrc-hyperjaxb/master/ejb/assembly/samples/publication/src/main/resources/PublicationClasses.svg
[82]: https://raw.githubusercontent.com/patrodyne/hisrc-hyperjaxb/master/ejb/assembly/samples/publication/src/main/resources/PublicationTables-EL-H2-JOINED.svg
[83]: https://raw.githubusercontent.com/patrodyne/hisrc-hyperjaxb/master/ejb/assembly/samples/publication/src/main/resources/PublicationTables-EL-H2-SINGLE_TABLE.svg
[84]: https://raw.githubusercontent.com/patrodyne/hisrc-hyperjaxb/master/ejb/assembly/samples/publication/src/main/resources/PublicationTables-EL-PG-JOINED.svg
[85]: https://raw.githubusercontent.com/patrodyne/hisrc-hyperjaxb/master/ejb/assembly/samples/publication/src/main/resources/PublicationTables-EL-PG-SINGLE_TABLE.svg
[86]: https://raw.githubusercontent.com/patrodyne/hisrc-hyperjaxb/master/ejb/assembly/samples/publication/src/main/resources/PublicationTables-HB-H2-JOINED.svg
[87]: https://raw.githubusercontent.com/patrodyne/hisrc-hyperjaxb/master/ejb/assembly/samples/publication/src/main/resources/PublicationTables-HB-H2-SINGLE_TABLE.svg
[88]: https://raw.githubusercontent.com/patrodyne/hisrc-hyperjaxb/master/ejb/assembly/samples/publication/src/main/resources/PublicationTables-HB-PG-JOINED.svg
[89]: https://raw.githubusercontent.com/patrodyne/hisrc-hyperjaxb/master/ejb/assembly/samples/publication/src/main/resources/PublicationTables-HB-PG-SINGLE_TABLE.svg
[90]: https://raw.githubusercontent.com/patrodyne/hisrc-hyperjaxb/master/ejb/assembly/samples/publication/src/main/resources/PublicationTables-EL-H2-TABLE_PER_CLASS.svg
[91]: https://raw.githubusercontent.com/patrodyne/hisrc-hyperjaxb/master/ejb/assembly/samples/publication/src/main/resources/PublicationTables-EL-PG-TABLE_PER_CLASS.svg
[92]: https://raw.githubusercontent.com/patrodyne/hisrc-hyperjaxb/master/ejb/assembly/samples/publication/src/main/resources/PublicationTables-HB-H2-TABLE_PER_CLASS.svg
[93]: https://raw.githubusercontent.com/patrodyne/hisrc-hyperjaxb/master/ejb/assembly/samples/publication/src/main/resources/PublicationTables-HB-PG-TABLE_PER_CLASS.svg
[94]: https://github.com/patrodyne/hisrc-hyperjaxb/commit/1a269e2895ee13049c92b38722f42aa80cf9ce17?diff=unified#diff-87ea0290cc2012ff242833e23e08ace1e5fd39e66d2652b2ebe112d42116b81b
[95]: https://github.com/eclipse-ee4j/eclipselink/issues/1918

