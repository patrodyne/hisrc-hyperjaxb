# HiSrc HyperJAXB

Maven and XJC plugins to add JPA annotations to JAXB.

## Description

This repository is a fork of [hyperjaxb3][25]. The original project was
developed by the admirable Alexey Valikov (a.k.a. [Highsource][2]). This
repository contains Java projects to build Maven artifact(s) related to the
Java Architecture for XML Binding (JAXB) framework. It is one of a family of
repositories forked from [Highsource][2] that provide tools for JAXB and JPA
processing. Repo and artifact names have been changed to reflect the familial
connection between the repositories and to fix a conformance issue with the
original `maven-jaxb2-plugin` name.

### List of repositories in this family

| Patrodyne                   | Highsource                  | Purpose                                                |
| --------------------------- | --------------------------- | ------------------------------------------------------ |
| [hisrc-basicjaxb-annox][11] | [annox][21]                 | Parse XML Schema to find Java annotation declarations. |
| [hisrc-basicjaxb][12]       | [jaxb2-basics][22]          | A library of XJC plugins and tools to extend JAXB.     |
| [hisrc-higherjaxb][13]      | [maven-jaxb2-plugin][23]    | Maven plugin to generated Java source from XML Schema. |
| [hisrc-hyperjaxb-annox][14] | [jaxb2-annotate-plugin][24] | XJC plugin to add arbitrary Java annotations to JAXB.  |
| [hisrc-hyperjaxb][15]       | [hyperjaxb3][25]            | Maven and XJC plugins to add JPA annotations to JAXB.  |

### Graph of repository relationships

![Patrodyne-Highsource Graph][1]

### Build Sequence

1. hisrc-basicjaxb-annox
2. hisrc-basicjaxb
3. hisrc-higherjaxb
4. hisrc-hyperjaxb-annox
5. hisrc-hyperjaxb

### Releases

#### GitHub Releases, Demonstrations

* [HiSrc HyperJAXB v2.1.1, Explorations][34]
* [HiSrc HyperJAXB v2.1.0, Explorations][33]
* [HiSrc HyperJAXB v2.0.0, Explorations][32]
* [HiSrc HyperJAXB v0.6.4, Explorations][31]

#### Maven Repositories

* Search
	* [MVN Repository](https://mvnrepository.com/artifact/org.patrodyne.jvnet?sort=popular)
	* [Central Repository](https://central.sonatype.com/search?q=org.patrodyne.jvnet+hisrc-hyperjaxb&sort=name)
* Index
	* [Central Repository](https://repo1.maven.org/maven2/org/patrodyne/jvnet/)

### Goals

New goals for the next release are:

* Replace `org.apache.maven:maven-compat` with Maven 3 dependencies.

### Status

* Review in progress.

#### Completed

* Review in progress.

#### Future

* Replace `org.apache.maven:maven-compat` with Maven 3 dependencies.

### Fork History

#### Version 2.1.1

* Standardized the plugin option name and usage for all XJC plugins.
* Standardized logging and error handling for all XJC plugins.
* Added `generateValueConstructor` param to `HyperjaxbMojo`.
* Adding sample: [publication](https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/assembly/samples/publication/README.md).
* Removed inappropriate precision/scale for `double`/`float` defaults.
* Enable all tests to use either *Hibernate* or *EclipseLink* JPA providers.
* Limit inheritance to extended roots.
* Update Hibernate to 6.x series.
* Read JVM properties from main or test.
* Update plugin and dependency versions.
* Resolved deprecated method(s).

#### Version 2.1.0

* Make `maxIdentifierLength` configurable from Maven Mojo and XJC Plugin.
* Clean up Java compiler _lint_ warnings.
* Replace deprecated XJC plugin strategies v1 with v2.
* Recheck Dependency Management for newer versions.
* Configure menu, log and Maven options from build-CFG.sh.
* Include JVM system arguments from `src/test/resources/jvmsystem.arguments`.
* Restored integration tests in 'assembly' folder.
* Set log levels for basic strategies in simplelogger.properties.
* Centralized createEntityManagerFactoryProperties method.
* Added HyperjaxbMojo properties for Copyable and Mergeable.
* Compile sources and generate classes for Java release v11 using JDK 17.
* Replaced `maven.compiler.source/target="11"` with `maven.compiler.release="11"`.
* Update JAXB dependencies from 3.x to 4.x.

#### Version 2.0.0

* Update dependencies with newer versions *including* the Jakarta namespace.
* Replace 'eclipse-only' lifecyle profile with 'm2e' XML directive.
* Convert DOS line endings to Unix newlines.
* Update JUnit v4 to JUnit v5.
* Replace Spring DI with Jakarta CDI.
* Rename packages:
    * BasicJAXB Annotations
        * `OLD: org.jvnet.annox`
        * `NEW: org.jvnet.basicjaxb_annox`
    * BasicJAXB XJC Plugin
        * `OLD: org.jvnet.jaxb2_commons`
        * `NEW: org.jvnet.basicjaxb`
    * HigherJAXB Maven Plugin
        * `OLD: org.jvnet.mjiip`
        * `NEW: org.jvnet.higherjaxb`
    * HigherJAXB Maven Mojo
        * `OLD: org.jvnet.jaxb2.maven2`
        * `NEW: org.jvnet.higherjaxb.mojo`
    * HyperJAXB Persistence
        * `OLD: org.jvnet.hyperjaxb[23]`
        * `NEW: org.jvnet.hyperjaxb`
* Rename namespaces:
    * BasicJAXB XJC Annotations
        * `OLD: http://annox.dev.java.net`
        * `NEW: http://jvnet.org/basicjaxb/xjc/annox`
    * BasicJAXB XJC Plugin
        * `OLD: http://jaxb2-commons.dev.java.net/basic`
        * `NEW: http://jvnet.org/basicjaxb/xjc`
    * HyperJAXB Persistence
        * `OLD: http://hyperjaxb3.jvnet.org/ejb/schemas/customizations`
        * `NEW: http://jvnet.org/hyperjaxb/jpa`
* Rename parameter property prefix:
    * `OLD: maven.hj3`
    * `NEW: org.jvnet.hyperjaxb.mojo.xjc`
* Update version to 2.0.0 due to jakarta and other name changes.

#### Version 0.6.4

* Added sample projects to GitHub releases.
* Updated dependencies with newer versions *excluding* the Jakarta namespace.
* Replaced [HSQLDB](http://hsqldb.org/) with [H2](https://www.h2database.com/) database engine.
* Added option to run tests using a local H2 database or a remote PostgreSQL server.

#### Version 0.6.3

* Obsolete build scripts have been removed.
* New build scripts have been added.
* POMs have been refactored with renamed artifacts.
* POMs have been updated to reduce warnings and errors.
* Configured SLF4J with SimpleLogger as the log framework.
* Changes to Java sources to resolve warnings/errors.
* Verification of unit and integration tests.

<!-- References -->

  [1]: https://raw.githubusercontent.com/patrodyne/hisrc-hyperjaxb/master/etc/hisrc-repositories.svg
  [2]: https://github.com/highsource
  [11]: https://github.com/patrodyne/hisrc-basicjaxb-annox#readme
  [12]: https://github.com/patrodyne/hisrc-basicjaxb#readme
  [13]: https://github.com/patrodyne/hisrc-higherjaxb#readme
  [14]: https://github.com/patrodyne/hisrc-hyperjaxb-annox#readme
  [15]: https://github.com/patrodyne/hisrc-hyperjaxb#readme
  [21]: https://github.com/highsource/annox#readme
  [22]: https://github.com/highsource/jaxb2-basics#readme
  [23]: https://github.com/highsource/maven-jaxb2-plugin#readme
  [24]: https://github.com/highsource/jaxb2-annotate-plugin#readme
  [25]: https://github.com/highsource/hyperjaxb3#readme
  [31]: https://github.com/patrodyne/hisrc-hyperjaxb/releases/tag/0.6.4
  [32]: https://github.com/patrodyne/hisrc-hyperjaxb/releases/tag/2.0.0
  [33]: https://github.com/patrodyne/hisrc-hyperjaxb/releases/tag/2.1.0
  [34]: https://github.com/patrodyne/hisrc-hyperjaxb/releases/tag/2.1.1
