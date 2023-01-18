# HiSrc HyperJAXB EJB Test

## Project: [issues-po-issues-pre]

The artifact produced by this project is a pre-requisite for the other projects in its parent project **issues-po**.

### Maven Build

Maven's *Reactor* collects all the available modules to build, sorts the projects into the correct build order and and dynamically adds the generated artifacts onto the class path. When Maven is invoked at the top level or in an appropriate sub-tree level of a multi-module build, it is able to resolve artifacts without the artifacts being installed in the local or remote repository.

### Eclipse Build

The *M2Eclipse* plugin provides tight integration for _Apache Maven_ into the Eclipse IDE but differs in some ways. It may be necessary to **install** this project in your local repository before *M2E* can resolve it.
