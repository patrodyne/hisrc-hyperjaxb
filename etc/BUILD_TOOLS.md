# Build Tools for HiSrc Projects

The build tools for the HiSrc projects are Maven and Ant.

## Maven

[Maven][1] is a build automation tool used primarily for Java projects. The Maven project is hosted by the [Apache Software Foundation][2], where it was formerly part of the Jakarta Project.

Maven addresses two aspects of building software: how software is built and its dependencies. Unlike earlier tools like [Apache Ant][4], it uses conventions for the build procedure. Only exceptions need to be specified. An XML file (pom.xml) describes the software project being built, its dependencies on other external modules and components, the build order, directories, and required plug-ins. It comes with pre-defined goals for performing certain well-defined tasks such as compilation of code and its packaging. Maven dynamically downloads Java libraries and Maven plug-ins from one or more repositories such as the Maven 2 Central Repository, and stores them in a local cache. This local cache of downloaded artifacts can also be updated with artifacts created by local projects. Public repositories can also be updated.

### Installation

Although **Maven** can be installed using OS specific package managers like `apt` and `yum`, these instructions describe how to install Maven from a [bash shell][6], generically.

#### INSTALL_MAVEN.sh
~~~
#!/bin/bash

# Maven Properties
MVN_VERS="3.8.4"
MVN_PATH="/opt/java/Apache/Maven"
MVN_HOME="${MVN_PATH}/apache-mvn-${MVN_VERS}"
MVN_FILE="apache-maven-${MVN_VERS}-bin.tar.gz"
MVN_REPO="https://dlcdn.apache.org/maven/maven-3"
MVN_LINK="${MVN_REPO}/${MVN_VERS}/binaries/${MVN_FILE}"

echo "Maven Vers: ${MVN_VERS}"
echo "Maven Path: ${MVN_PATH}"
echo "Maven Home: ${MVN_HOME}"
echo "Maven File: ${MVN_FILE}"
echo "Maven Repo: ${MVN_REPO}"
echo "Maven Link: ${MVN_LINK}"

# Download and extract Apache Maven
cd "${MVN_PATH}"
curl --remote-name "${MVN_LINK}"
tar -xaf "${MVN_FILE}"

echo "Maven installed at ${MVN_HOME}"
echo "Please add 'MAVEN_HOME=${MVN_HOME}' into your OS environment!"
~~~

#### Generate man page and set Maven alternative

> [help2man][7] produces simple manual pages from the `--help` and `--version`
> output of other commands.

#### INSTALL_MAVEN_ALT.sh
~~~
#!/bin/bash

# Maven Install properties
MVN_BIN_PATH="/usr/bin"
MVN_MAN_PATH="/usr/share/man/man1"

echo "Maven Home....: ${MAVEN_HOME}"
echo "Maven Bin Path: ${MVN_BIN_PATH}"
echo "Maven Man Path: ${MVN_MAN_PATH}"

# Use 'help2man' to generate man page
mkdir "${MAVEN_HOME}/man"
help2man -o "${MAVEN_HOME}/man/mvn.1" "${MAVEN_HOME}/bin/mvn"
gzip "${MAVEN_HOME}/man/mvn.1"

sudo update-alternatives --install "${MVN_BIN_PATH/mvn}" "mvn" "${MAVEN_HOME}/bin/mvn" 500 \
  --slave "${MVN_MAN_PATH/mvn.1.gz}" "mvn.1.gz" "${MAVEN_HOME}/man/mvn.1.gz"

# Optionally, switch Maven alternatives using:
# sudo update-alternatives --list mvn
# sudo update-alternatives --display mvn
# sudo update-alternatives --config mvn
~~~

## Ant

[Apache Ant][4] is a software tool for automating software build processes which originated in early 2000 as a replacement for the Make build tool of Unix. It is similar to Make, but is implemented using the Java language and requires the Java platform. Unlike Make, which uses the Makefile format, Ant uses XML to describe the code build process and its dependencies.

At one time (2002), Ant was the build tool used by most Java development projects. For example, most open source Java developers included `build.xml` files with their distribution. Because Ant made it trivial to integrate [JUnit][5] tests with the build process, Ant allowed developers to adopt test-driven development and extreme programming.

### Installation

Although **Ant** can be installed using OS specific package managers like `apt` and `yum`, these instructions describe how to install Ant from a [bash shell][6], generically.

#### INSTALL_ANT.sh
~~~
#!/bin/bash

# Ant Properties
ANT_VERS="1.10.11"
ANT_PATH="/opt/java/Apache/Ant"
ANT_HOME="${ANT_PATH}/apache-ant-${ANT_VERS}"
ANT_FILE="apache-ant-${ANT_VERS}-bin.tar.gz"
ANT_REPO="https://archive.apache.org/dist/ant/binaries"
ANT_LINK="${ANT_REPO}/${ANT_FILE}"

echo "Ant Vers: ${ANT_VERS}"
echo "Ant Path: ${ANT_PATH}"
echo "Ant Home: ${ANT_HOME}"
echo "Ant File: ${ANT_FILE}"
echo "Ant Repo: ${ANT_REPO}"
echo "Ant Link: ${ANT_LINK}"

# Download and extract Apache Ant
cd "${ANT_PATH}"
curl --remote-name "${ANT_LINK}"
tar -xaf "${ANT_FILE}"

echo "Ant installed at ${ANT_HOME}"
echo "Please add 'ANT_HOME=${ANT_HOME}' into your OS environment!"
~~~

### Generate man page and set Ant alternative

> [help2man][7] produces simple manual pages from the `--help` and `--version`
> output of other commands.

#### INSTALL_ANT_ALT.sh
~~~
#!/bin/bash

# Ant Install properties

ANT_BIN_PATH="/usr/bin"
ANT_MAN_PATH="/usr/share/man/man1"

echo "Ant Home....: ${ANT_HOME}"
echo "Ant Bin Path: ${ANT_BIN_PATH}"
echo "Ant Man Path: ${ANT_MAN_PATH}"

# Use 'help2man' to generate man page
mkdir "${ANT_HOME}/man"
help2man -o "${ANT_HOME}/man/ant.1" "${ANT_HOME}/bin/ant"
gzip "${ANT_HOME}/man/ant.1"

sudo update-alternatives --install "${ANT_BIN_PATH/ant}" "ant" "${ANT_HOME}/bin/ant" 500 \
  --slave "${ANT_MAN_PATH/ant.1.gz}" "ant.1.gz" "${ANT_HOME}/man/ant.1.gz"

# Optionally, switch Ant alternatives using:
# sudo update-alternatives --list ant
# sudo update-alternatives --display ant
# sudo update-alternatives --config ant
~~~

### Install JUnit5 option for Ant

[JUnit 5][5] introduced a newer set of APIs to write and launch tests. It also introduced the concept of test engines. Test engines decide which classes are considered as test cases and how they are executed.

#### INSTALL_ANT_JUNIT.sh
~~~
#!/bin/bash

# JUnit Platform Console (Standalone) Properties
JPCS_VERS="1.9.0"
JPCS_NAME="junit-platform-console-standalone"
JPCS_PATH="org/junit/platform/${JPCS_NAME}/${JPCS_VERSION}"
JPCS_FILE="${JPCS_NAME}-${JPCS_VERSION}.jar"
JPCS_REPO="https://repo1.maven.org/maven2"
JPCS_LINK="${JPCS_REPO}/${JPCS_PATH}/${JPCS_FILE}"
JPCS_HOME="${ANT_HOME}/lib"

echo "JUnit Vers: ${JPCS_VERS}"
echo "JUnit Name: ${JPCS_NAME}"
echo "JUnit Path: ${JPCS_PATH}"
echo "JUnit File: ${JPCS_FILE}"
echo "JUnit Repo: ${JPCS_REPO}"
echo "JUnit Link: ${JPCS_LINK}"
echo "JUnit Home: ${JPCS_HOME}"

# Download and extract JUnit Platform Console (Standalone)
cd "${JPCS_HOME}"
curl --remote-name  "${JPCS_LINK}"
~~~

<!-- References -->

  [1]: https://en.wikipedia.org/wiki/Apache_Maven
  [2]: https://en.wikipedia.org/wiki/Apache_Software_Foundation
  [3]: https://en.wikipedia.org/wiki/Jakarta_Project
  [4]: https://en.wikipedia.org/wiki/Apache_Ant
  [5]: https://en.wikipedia.org/wiki/JUnit
  [6]: https://en.wikipedia.org/wiki/Bash_(Unix_shell)
  [7]: https://www.gnu.org/software/help2man
