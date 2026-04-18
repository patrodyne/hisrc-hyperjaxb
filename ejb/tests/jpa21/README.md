JPA 2.1 (JSR 338) introduced several significant annotations to standardize features that were previously only available through vendor-specific extensions like Hibernate or EclipseLink. [^1] [^2] [^3] [^4] [^5]

## New Annotations in JPA 2.1

* **`@Convert`** and **`@Converter`**: These are used for *Basic Attribute Type* conversion. They allow you to define how a custom Java type (like a `Boolean` or a complex object) should be mapped to a database column.
* **`@Index`**: Finally standardized in 2.1, this allows you to define database indexes directly within your `@Table` or `@SecondaryTable` annotations.
* **`@ForeignKey`**: Provides a standard way to define foreign key constraints for join columns and primary key join columns.
* **`@NamedStoredProcedureQuery`** and **`@StoredProcedureParameter`**: These enable the definition and calling of database *stored procedures* using standard JPA metadata.
* **`@NamedEntityGraph`** and **`@NamedAttributeNode`**: These allow you to define *Entity Graphs*, which specify the fetching or processing of a graph of entity objects to optimize performance and avoid "N+1" select issues.
* **`@ConstructorResult`**: Used within **`@SqlResultSetMapping`** to map the results of a native SQL query directly to a Java constructor. [^1] [^2] [^5] [^6] [^7] [^8] [^9] [^10] 

## Key Behavior & Contextual Changes

* **Schema Generation**: While not a single new annotation, JPA 2.1 added a standard set of properties (prefixed with `javax.persistence.schema-generation.*`) to control the generation of DDL scripts and database schemas at startup.
* **CDI Integration**: You can now use CDI (Contexts and Dependency Injection) to inject beans into *Entity Listeners*, allowing for more flexible logic in lifecycle methods like `@PrePersist` or `@PostUpdate`.
* **Programmatic Named Queries**: The `EntityManagerFactory` now supports `addNamedQuery` at runtime, providing a programmatic alternative to the static `@NamedQuery` annotation. [^1] [^2] [^6] [^11] [^12]

## Summary [^6] [^13] [^14] [^15] [^16]

| Feature | JPA 2.1 Annotation(s) | Purpose |
|---|---|---|
| Type Conversion | @Converter, @Convert | Map custom Java types to DB columns. |
| Stored Procedures | @NamedStoredProcedureQuery | Invoke database-stored logic. |
| Indexing | @Index | Standardize index creation in DDL. |
| Performance | @NamedEntityGraph | Define custom fetch plans/graphs. |
| Constraints | @ForeignKey | Standardize FK constraint naming. |

<!-- Footnotes -->

[^1]: [https://www.ibm.com](https://www.ibm.com/docs/en/was-liberty/nd?topic=jpa-java-persistence-api-21-behavior-changes)
[^2]: [https://openjpa.apache.org](https://openjpa.apache.org/jpa-2.1-tasks.html)
[^3]: [https://terasolunaorg.github.io](https://terasolunaorg.github.io/guideline/5.4.1.RELEASE/en/ArchitectureInDetail/DataAccessDetail/DataAccessJpa.html#:~:text=QueryHints%20stipulated%20in%20JPA%20specifications%20are%20as,Java%20Persistence%20API%2C%20Version%202.1%20Specification%20%28PDF%29.)
[^4]: [https://thorben-janssen.com](https://thorben-janssen.com/6-hibernate-features-im-missing-jpa/)
[^5]: [https://www.baeldung.com](https://www.baeldung.com/jpa-indexes)
[^6]: [https://thorben-janssen.com](https://thorben-janssen.com/jpa-21-overview/)
[^7]: [https://stackoverflow.com](https://stackoverflow.com/questions/30775827/jpa-2-1-converter-annotation)
[^8]: [https://www.jrebel.com](https://www.jrebel.com/blog/how-to-improve-jpa-performance)
[^9]: [https://docs.spring.io](https://docs.spring.io/spring-data/jpa/docs/1.7.0.DATAJPA-580-SNAPSHOT/reference/html/jpa.repositories.html)
[^10]: [https://github.com](https://github.com/spring-projects/spring-data-examples/blob/master/jpa/jpa21/README.md)
[^11]: [https://developers.redhat.com](https://developers.redhat.com/blog/2016/03/07/whats-new-jpa-2-1-hibernate-5-jboss-eap-7)
[^12]: [https://thorben-janssen.com](https://thorben-janssen.com/define-named-queries-runtime-jpa-2-1/)
[^13]: [https://stackoverflow.com](https://stackoverflow.com/questions/15973361/jpa-independent-custom-type-mapping-javax-persistence-x-alternative-to-org-hib)
[^14]: [https://codemia.io](https://codemia.io/knowledge-hub/path/spring_boot_jpa_column_name_annotation_ignored_1#:~:text=They%20%28%20Spring%20Boot%20and%20JPA%20%28Java,column%20names%20are%20ignored%20during%20JPA%20implementation.)
[^15]: [https://forums.oracle.com](https://forums.oracle.com/ords/apexds/post/what-s-new-in-jpa-1950#:~:text=Attribute%20conversion%20makes%20it%20possible%20to%20easily,muddying%20up%20entity%20classes%20with%20conversion%20logic.)
[^16]: [https://developers.redhat.com](https://developers.redhat.com/blog/2016/03/07/whats-new-jpa-2-1-hibernate-5-jboss-eap-7#:~:text=Since%20the%20JPA%20%28%20Java%20Persistence%20API,the%20dynamic%20StoredProcedureQuery%20and%20the%20declarative%20@NamedStoredProcedureQuery.)
