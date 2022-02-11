# Analyze/Fix the OTA XML Schemas

	1) schemas-format.sh: Format the OTA schemas.
	2) schemas-add-namespace.sh: Add missing TNS to some XSDs.
	3) schema-grep-includes.sh: Extract the 'include' dependencies.
	4) schema-ota-digraph.sh: Wrap the dependencies in a 'dot' digraph.
	5) schema-ota-dependencies.sh: Use 'dot' to output an SVG file.
	6) schema-ota-dependencies.svg: View the SVG in a web browser: firefox, chrome, etc.
	7) schema-ota-dependency-order.sh: Sort the schemas into reverse dependency order.
	8) CreateToplevelXJBindings.sh: Create toplevel binding and copy/paste to toplevel.xjb.

After step #2, the formatted and updated XSDs are in the `target/doc` sub-directory. Use these
to replace/update the schemas in the `resource` sub-directory.

Use the output from step #6 to configure the pom.xml to generate the HyperJAXB classes in
dependency order.

Step #8 is needed to resolve naming conflicts due to the many nested complex types in the
OTA schemas; and, to eliminate this warning...

```
[WARNING] According to the Java Persistence API specification, section 2.1, entities must be top-level classes.
```
