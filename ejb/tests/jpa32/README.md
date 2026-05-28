# JPA Changes since v2.1

Since the release of **JPA 2.1** (2013), the specification transitioned into the Jakarta EE ecosystem and is now formally known as **Jakarta Persistence**. Across versions 2.2, 3.0, 3.1, and 3.2, several crucial annotations have been added, modified, or updated to support modern Java features. [^1] [^2]

The evolutionary breakdown of these changes includes:

## 1. Brand New Annotations

* **@EnumeratedValue** (Added in **Jakarta Persistence 3.2**): Used inside a Java enum to specify exactly which internal field or property represents its encoding/mapping value in the database.
* **@TableGenerators** (Added in **JPA 2.2**): Acts as the required container annotation to group multiple `@TableGenerator` mappings on a single entity.
* **@SequenceGenerators** (Added in **JPA 2.2**): Acts as the required container annotation to group multiple `@SequenceGenerator` mappings on a single entity. [^3] [^4] [^5] [^6] 

------------------------------
## 2. Significant Modifications to Existing Annotations

### `@Column` (Updated in Jakarta Persistence 3.2)

The `@Column` annotation received a major structural update to improve automated schema generation and temporal precision: [^7] 

* **comment attribute**: Allows developers to attach a descriptive text comment directly to the database column.
* **check attribute**: Allows embedding standard SQL CHECK constraints directly into the column mapping.
* **secondPrecision attribute**: Controls fractional second precision when mapping Java `java.time` types like `Instant` or `LocalDateTime`. [^2] [^7] [^8] [^9] 

## `@NamedNativeQuery` (Updated in Jakarta Persistence 3.2) [^10] 
To provide superior type safety and configuration flexibility, new structural attributes were introduced: [^2] 

* **entities attribute**: Explicitly arrays the target entity types for the native query mapping.
* **classes attribute**: Maps non-entity unmanaged target projection classes directly.
* **columns attribute**: Supplies direct mapping definitions to the targeted database columns. [^2] 

## @StaticMetamodel (Updated in Jakarta Persistence 3.2) [^11] 

* The specification now explicitly mandates the presence of the  `jakarta.annotation.processing.Generated` annotation on any vendor-generated static metamodel classes. [^2] 

------------------------------
## 3. Structural Capabilities Added Globally

### Java 8 @Repeatable Modernization (Introduced in JPA 2.2) [^12] 

Prior to JPA 2.2, grouping identical metadata required ugly wrapper annotations (e.g., nesting multiple `@NamedQuery` blocks inside an external `@NamedQueries` tag). JPA 2.2 added the `@Repeatable` flag to **16 core annotations**, meaning you can now stack them cleanly directly on top of classes or attributes. [^3] [^6] [^12] [^13] [^14] 

The annotations that became repeatable are: [^14] 

* `@NamedQuery` & `@NamedNativeQuery`
* `@NamedStoredProcedureQuery` & `@NamedEntityGraph`
* `@AttributeOverride` & `@AssociationOverride`
* `@Convert` & `@JoinColumn`
* `@MapKeyJoinColumn` & `@PrimaryKeyJoinColumn`
* `@SecondaryTable` & `@SqlResultSetMapping`
* `@PersistenceContext` & `@PersistenceUnit`
* `@TableGenerator` & `@SequenceGenerator` [^14] [^15] [^16] [^17] [^18] 

## Drop of `@Temporal` Requirements

While not an annotation change, by deprecation, the introduction of full support for the **`java.time` (JSR-310)** API starting in JPA 2.2 means you **no longer use the `@Temporal` annotation** when mapping fields like `LocalDate`, `LocalTime`, `LocalDateTime`, `OffsetTime`, or `OffsetDateTime`. The framework implicitly understands their precision layout. [^19]

<!-- Footnotes -->

[^1]: [https://en.wikipedia.org](https://en.wikipedia.org/wiki/Jakarta_Persistence)
[^2]: [https://jakarta.ee](https://jakarta.ee/specifications/persistence/3.2/)
[^3]: [https://thorben-janssen.com](https://thorben-janssen.com/jpa-2-2-repeatable-annotations/)
[^4]: [https://gavinking.substack.com](https://gavinking.substack.com/p/a-summary-of-jakarta-persistence)
[^5]: [https://www.youtube.com](https://www.youtube.com/watch?v=Au_p26nNRYk)
[^6]: [https://www.scribd.com](https://www.scribd.com/document/473073250/JPA-2-2-Repeatable-Annotations)
[^7]: [https://itnext.io](https://itnext.io/an-introduction-to-jakarta-persistence-3-2-by-examples-69b34adc9c0b)
[^8]: [https://dev.to](https://dev.to/comnori/guide-to-adding-comments-to-tables-and-columns-when-automatically-generating-entities-using-orm-in-17i8)
[^9]: [https://aspectran.com](https://aspectran.com/en/blog/2026-02-21-aspectow-appmon-3-1-release-notes)
[^10]: [https://docs.spring.io](https://docs.spring.io/spring-data/jpa/docs/current-SNAPSHOT/reference/html/)
[^11]: [https://developer.ibm.com](https://developer.ibm.com/articles/j-typesafejpa/)
[^12]: [https://in.relation.to](https://in.relation.to/2018/02/07/hibernate-53-repeating-jpa-annotations/)
[^13]: [https://thorben-janssen.com](https://thorben-janssen.com/whats-new-in-jpa-2-2/)
[^14]: [https://www.infoq.com](https://www.infoq.com/news/2018/01/improvements-jpa-22/)
[^15]: [https://docs.hibernate.org](https://docs.hibernate.org/orm/5.1/userguide/html_single/appendices/Annotations.html)
[^16]: [https://www.baeldung.com](https://www.baeldung.com/java-jpa-join-vs-primarykeyjoin)
[^17]: [https://in.relation.to](https://in.relation.to/2026/01/20/JPA-4-M1/)
[^18]: [https://thorben-janssen.com](https://thorben-janssen.com/jpa-2-2-repeatable-annotations/)
[^19]: [https://www.infoq.com](https://www.infoq.com/articles/JPA-2.2-Brings-Highly-Anticipated-Changes/)
[^20]: [https://hantsy.github.io](https://hantsy.github.io/blog/2022/what-s-new-in-jakarta-persistence-3-1-by-examples/)
[^21]: [https://is.muni.cz](https://is.muni.cz/th/jlwae/Entity_Beans_to_JPA__Migrating_to_Modern_Standards.pdf)

