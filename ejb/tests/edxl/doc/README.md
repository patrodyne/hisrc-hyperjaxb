# Analyze/Fix the EDXL XML Schemas

	1) CreateToplevelXJBindings.sh: Create toplevel binding and copy/paste to toplevel.xjb.

Step #1 is needed to resolve naming conflicts due to the many nested complex types in the
EDXL schemas; and, to eliminate this warning...

```
[WARNING] According to the Java Persistence API specification, section 2.1, entities must be top-level classes.
```
