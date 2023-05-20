# Sample: Publication
## JPA Inheritance and Many-To-Many

This Maven project demonstrates the generation of JPA Entities from an XML Schema file (xsd) using the [HiSrc HyperJAXB Maven Plugin][13]. The generated code include [JPA][1] and [JAXB][2] annotations to support JDBC persistence and XML marshaling/unmarshaling. Further, it uses [HiSrc BasicJAXB XJC Plugins][10] plugins to add custom `hashCode`, `equals`, and `toString` implementations to each generated entity.

The XML Schema file, [Publication.xsd][36], is based on this fine article, [Inheritance Strategies with JPA and Hibernate – The Complete Guide](https://thorben-janssen.com/complete-guide-inheritance-strategies-jpa-hibernate/). The schema defines these entities:

+ `Author` associates with many `Publication(s)`
+ `Publication` associates to many `Author(s)`
+ `Blog` inherits from `Publication`
+ `Book` inherits from `Publication`

This project demonstrates the inheritance strategy and many-to-many configuration used to persist these entities to a database.

### Problem ( from [GitHub][7] )

#### Inheritance

With regard to [JPA][1] entity inheritance, **HyperJAXB** uses the `JOINED` inheritance strategy for all root level entities:

~~~
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Inheritance(strategy = InheritanceType.JOINED)
~~~

As of v2.1.0, **HyperJAXB** ...

+ *does not* provide a configuration option for alternative [JPA][1] strategies: `SINGLE_TABLE`, `JOINED`, `TABLE_PER_CLASS`.
+ *does* add the `@Inheritance` annotation on *every* root class, even if the base has no inherited classes in the XML Schema.
+ *does* return `JOINED` as the default inheritance strategy, see [EntityMapping#getInheritanceStrategy(...)][80].

The [JPA][1] specifies how the *persistence provider* should implement the [11.1.12. DiscriminatorColumn Annotation](https://jakarta.ee/specifications/persistence/3.0/jakarta-persistence-spec-3.0.html#a14530) to:

+ define the discriminator column for the `SINGLE_TABLE` and `JOINED` inheritance mapping strategies.
+ default the name of the discriminator column defaults to `"DTYPE"` and the discriminator type to `STRING`.

> **Note:** The discriminator column can be configured *explicitly* by adding a `@jakarta.persistence.DiscriminatorColumn` annotation to any root entity. For example, the column length can be changed from the default `31` to another length.

Two well-known *persistence provider*s are [EclipseLink][4] and [Hibernate][5] and each provides a different approach:

+ As of v4.0.1, [EclipseLink][5] always adds a `DTYPE` column for each root table.
+ As of v5.6.15.Final, [Hibernate][6] only adds a `DTYPE` column on root tables with sub-tables.

This [demonstration (zip)][20] can be executed using either provider.

#### Many To Many

> Although the [GitHub issue][7] does not reference a `many-to-many` relationship, the sample here includes an example of one (`Publication`-`Author`) to provide a richer context for the *inheritance* issue. Plus, this sample reveals that **HyperJAXB** support for `many-to-many` may need some review.

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

### Solution (in progress)

This [demonstration (zip)][20] confirms the observations reported in this [issue][7] and provides some approaches to address it; but, a full resolution will require some review and changes to a future release.

#### Change the Inheritance Strategy

Currently in v2.1.0, **HyperJAXB** implements the inheritance strategy in [EntityMapping#getInheritanceStrategy(...)][80] as a non-configurable default:

~~~
...
return jakarta.persistence.InheritanceType.JOINED;
...
~~~

However, [**HyperJAXB Annox**][14] can be used to remove the `@Inheritance` annotation in post-processing. To explore this approach, edit your copy of [Publication.xjb][39] to enable this commented binding for the two root elements: `Publication` and `Author`.

**Publication.xjb**
~~~
...
<anx:removeAnnotation target="class" class="jakarta.persistence.Inheritance"/>
...
~~~

Use Maven's test goal to generate the JPA/JAXB classes for your review. You can use a pre-configured Maven profile to select the JPA provider: [eclipselink][5] or [hibernate][6].

~~~
mvn -Peclipselink clean test
OR
mvn -Phibernate clean test
~~~

#### Omit `@Inheritance` from Childless Roots

TBD

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
[10]: https://github.com/patrodyne/hisrc-basicjaxb#readme
[11]: https://github.com/patrodyne/hisrc-basicjaxb-annox#readme
[12]: https://github.com/patrodyne/hisrc-higherjaxb#readme
[13]: https://github.com/patrodyne/hisrc-hyperjaxb#readme
[14]: https://github.com/patrodyne/hisrc-hyperjaxb-annox#readme
[20]: https://github.com/patrodyne/hisrc-hyperjaxb/releases/download/2.1.0/hisrc-hyperjaxb-ejb-sample-publication-2.1.0-mvn-src.zip
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
