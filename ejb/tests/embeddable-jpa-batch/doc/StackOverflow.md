When JPA is used to perform large batch inserts, here are a few things you can check to improve performance:

Configure the JPA `EntityManagerFactory` to enable SQL batching. This groups multiple SQL statements into a buffered batch and executes them together in a single network trip to the database. Here are the settings for two popular JPA providers: Hibernate and EclipseLink. The default values are shown here.

**Enable SQL Batching**
~~~
hibernate.jdbc.batch_size=15
eclipselink.jdbc.batch-writing=JDBC
eclipselink.jdbc.batch-writing.size=100
~~~

**JPA Batching with Individual Entities**

When persisting entities, say `MyEntity`, you should create a JPA transaction to persist your entities in chunks. The chunk size should be larger than the SQL batch size and limited in size based on memory, network and database capacity.

**JPA Batching with Parent/Child Entities**

When using an entity, say `MyEntityBatch` to contain your `MyEntity` chunks, use a bidirectional JPA relationship to reduce the need for `insert/update` pairs.

**Parent Entity (1:*)**
~~~
@OneToMany(targetEntity = MyEntity.class, mappedBy = "myEntityBatch")
public List<MyEntity> getMyEntity()
{
    if (myEntity == null)
        myEntity = new ArrayList<>();
    return this.myEntity;
}
~~~

The parent is a one-to-many mapping of a batch object to a list of child objects. Typically, you commit each batch in one JPA transaction and clear the first level cache after every commit. Where, the JPA batch size is larger than the SQL batch size.

> Note: The `@OneToMany` annotation sets the property named `mappedBy` to the name of the field on the child entity that represents the other half of the bidirectional relationship.

**Child Entity (*:1)**
~~~
@XmlTransient
protected MyEntityBatch myEntityBatch;

@ManyToOne(targetEntity = MyEntityBatch.class, cascade = {
    CascadeType.PERSIST,
    CascadeType.MERGE,
    CascadeType.REFRESH
}, fetch = FetchType.LAZY)
@JoinColumn(name = "MY_ENTITY_BATCH_ID")
public MyEntityBatch getMyEntityBatch()
{
    return myEntityBatch;
}
~~~

The child `MyEntity` declares a field named `myEntityBatch` to represent the parent instance. This field is annotated with `@XmlTransient` to avoid a cyclic reference exception when using JAXB to marshal the entity. The field's accessor is annotated with `@ManyToOne` and a `@JoindColumn` to name the column in the database table where the parent's primary key is stored.

> Note: This half of the bidirectional relationship allows the JPA provider to combine the persistence of the child fields and the parent key in a single insert, in cases where the parent id is known. JAXB looks for this method and invokes it. To learn more see [Vlad Mihalcea](https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/)

If you use JAXB to unmarshal XML data into `MyEntity` instances, you can add this method to `MyEntity` to assign the parent id to the child instance.

~~~
void afterUnmarshal(jakarta.xml.bind.Unmarshaller target, Object parent)
{
    if ( parent instanceof MyEntityBatch )
        setMyEntityBatch((MyEntityBatch) parent);
}
~~~

**JPA Provider uses Prepare Statements**

Verify that the JPA Provider (Hibernate, EclipseLink) uses SQL prepared statements with parameter substitution. You can use the [H2 database](https://www.h2database.com) to see the generated SQL for verification.

**H2 database showing SQL insert is parameterized**
~~~sql
insert into ejb_tests_embeddable_jpa_batch.MY_TMP_TABLE
(
    ADDITIONAL_DATA,
    MY_ENTITY_BATCH_ID,
    ID_KA_STRING,
    ID_KB_ENUM,
    ID_KC_BOOLEAN
) values (?,?,?,?,?) {1: 'xx1', 2: 'batch0001', 3: 'AAAAA', 4: 'I', 5: TRUE};
~~~

**Embedded Id Ordering**

When using multiple fields/columns to represent the `MyEntity` primary key, you should consider the column order of the compound index. Order the columns from the most discrete to the least discrete. This allow the database optimizer to select the best b-tree path for SQL operations.

~~~
@EmbeddedId
@AttributeOverrides({
    @AttributeOverride(name = "kaString", column = @Column(name = "ID_KA_STRING", length = 255)),
    @AttributeOverride(name = "kbEnum", column = @Column(name = "ID_KB_ENUM", length = 255)),
    @AttributeOverride(name = "kcBoolean", column = @Column(name = "ID_KC_BOOLEAN"))
})
public MyEntityPk getId()
{
    return id;
}
~~~

These `@AttributeOverrides` allow the `MyEntity` class to use its own column names for the fields in the `MyEntityPk` class (a POJO for the composite key).

> Note: Hibernate and EclipseLink order columns using internal strategies. However, Hibernate provides a `legacy` mode to order columns alphabetically when table generation is enabled.

**EntityManageFactory Property**
~~~
hibernate.column_ordering_strategy=legacy
~~~

**Sample Performance**

The [HiSrc HyperJAXB](https://github.com/patrodyne/hisrc-hyperjaxb#readme) project contains a unit test that implements the ideas mentioned in this comment. The test is at [embeddable-jpa-batch](https://github.com/patrodyne/hisrc-hyperjaxb/tree/2cc8686ddaa32da5e2c04d275e5d9edc55cae076/ejb/tests/embeddable-jpa-batch) and it includes a [Main](https://github.com/patrodyne/hisrc-hyperjaxb/blob/2cc8686ddaa32da5e2c04d275e5d9edc55cae076/ejb/tests/embeddable-jpa-batch/src/main/java/org/example/embeddable_jpa_batch/Main.java) application to generate `MyEntity` instances in batches and insert them in to a database.

> Note: These tests were run on a Linux laptop over WiFi to a NAS running a Postgres database server. The initial connect/setup time is not included in the result times.

**Result: 10 batches of 100 entities in 742 ms**
~~~
Main - connect1: cnt=0; avg=3781.0000 ms; tot=3781 ms
Main - batch0001: cnt=101; avg=1.3168 ms; tot=133 ms
Main - batch0002: cnt=101; avg=0.7624 ms; tot=77 ms
Main - batch0003: cnt=101; avg=0.7030 ms; tot=71 ms
Main - batch0004: cnt=101; avg=0.6832 ms; tot=69 ms
Main - batch0005: cnt=101; avg=0.6931 ms; tot=70 ms
Main - batch0006: cnt=101; avg=0.7426 ms; tot=75 ms
Main - batch0007: cnt=101; avg=0.7525 ms; tot=76 ms
Main - batch0008: cnt=101; avg=0.5446 ms; tot=55 ms
Main - batch0009: cnt=101; avg=0.5248 ms; tot=53 ms
Main - batch0010: cnt=101; avg=0.5644 ms; tot=57 ms
Main - ALL: cnt=1010; avg=0.7347 ms; tot=742 ms
~~~

**Result: 1000 batches of 1000 entities in 419719 ms (7 min)**
~~~
Main - connect1: cnt=0; avg=4505.0000 ms; tot=4505 ms
Main - batch0001: cnt=1001; avg=0.9800 ms; tot=981 ms
Main - batch0002: cnt=1001; avg=0.4525 ms; tot=453 ms
Main - batch0003: cnt=1001; avg=0.3776 ms; tot=378 ms
Main - batch0004: cnt=1001; avg=0.3946 ms; tot=395 ms
Main - batch0005: cnt=1001; avg=0.3786 ms; tot=379 ms
Main - batch0006: cnt=1001; avg=0.3956 ms; tot=396 ms
Main - batch0007: cnt=1001; avg=0.3776 ms; tot=378 ms
Main - batch0008: cnt=1001; avg=0.3696 ms; tot=370 ms
Main - batch0009: cnt=1001; avg=0.3566 ms; tot=357 ms
Main - batch0010: cnt=1001; avg=0.3766 ms; tot=377 ms
...
Main - batch0991: cnt=1001; avg=0.3956 ms; tot=396 ms
Main - batch0992: cnt=1001; avg=0.3986 ms; tot=399 ms
Main - batch0993: cnt=1001; avg=0.3606 ms; tot=361 ms
Main - batch0994: cnt=1001; avg=0.3916 ms; tot=392 ms
Main - batch0995: cnt=1001; avg=0.4216 ms; tot=422 ms
Main - batch0996: cnt=1001; avg=0.3966 ms; tot=397 ms
Main - batch0997: cnt=1001; avg=0.3866 ms; tot=387 ms
Main - batch0998: cnt=1001; avg=0.3986 ms; tot=399 ms
Main - batch0999: cnt=1001; avg=0.4246 ms; tot=425 ms
Main - batch1000: cnt=1001; avg=0.4825 ms; tot=483 ms
Main - ALL: cnt=1001000; avg=0.4193 ms; tot=419719 ms
~~~

> _Disclaimer_: I am the maintainer for the HiSrc projects.


