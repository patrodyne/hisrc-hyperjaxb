# HiSrc HyperJAXB

Maven and XJC plugins to add JPA annotations to JAXB.

## Description

This repository is a fork of [hyperjaxb3][25]. The original project was developed by the admirable
Alexey Valikov (a.k.a. [Highsource][2]). This repository contains Java projects to build Maven artifact(s)
related to the Java Architecture for XML Binding (JAXB) framework. It is one of a family of repositories
forked from [Highsource][2] that provide tools for JAXB and JPA processing. Repo and artifact names have
been changed to reflect the familial connection between the repositories and to fix a conformance issue
with the original `maven-jaxb2-plugin` name.

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

### Releases

#### Maven Central Repository

* [Maven Central Repository Search](https://search.maven.org/search?q=g:org.patrodyne.jvnet)
* [Maven Central Repository Index](https://repo1.maven.org/maven2/org/patrodyne/jvnet/)

#### GitHub Zip Releases

* TBD

### Goals

The new goals for this fork are:

* Add sample projects to GitHub releases.
* Update dependencies with newer versions *excluding* the Jakarta namespace.
* Update dependencies with newer versions *including* the Jakarta namespace.
* Replace [HSQLDB](http://hsqldb.org/) with [H2](https://www.h2database.com/) database engine.

### Status

#### Completed

* Planning for next release.

#### In Progress

* Adding sample projects to GitHub releases.

### Fork History

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
