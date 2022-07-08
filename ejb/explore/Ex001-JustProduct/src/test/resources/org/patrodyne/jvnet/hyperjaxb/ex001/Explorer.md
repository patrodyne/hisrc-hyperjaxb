# HiSrc HyperJAXB Ex001 - Product Model

This project is the first exploration of the HiSrc HyperJAXB Maven Plug-in. It includes a Swing application named *Explorer* to demonstrate features of the HiSrc HyperJAXB plug-in. This *Explorer* application is designed to present a narrative lesson together with dynamic output for real-time experimentation. Feel free to modify your copy of the [Explorer.java][5] source file by adding or modifying the action methods with your own investigative code. The `Explorer` class is an extension of `AbstractExplorer` which contains the more boring mechanics of this implementation. Feel free to create an `Explorer` class in your own projects to help explain the purpose of your work too.

> This **Explorer** application is a *development tool* and is not included in your Maven generated artifact; although, you could place it in your main package if your goal is to make a Swing based application.

> **About AbstractExplorer:** Projects can create their own custom Explorer by extending `AbstractExplorer` and providing an HTML lesson page plus `JMenuItem`(s) to trigger exploratory code.

> An `AbstractExplorer` implementation (like the one you see here) displays three panels: an HTML lesson, a print console and an error console. The lesson file is read as a resource relative to the implementation (i.e. `Explorer`) class. Text is sent to the print console by calling `println(text)` and error messages are sent to the error console by calling `errorln(msg)`. Additionally, 'standard out' / 'standard error' streams are sent to their respective consoles.

## HiSrc HyperJAXB Maven Plug-in

**pom.xml**

~~~
<plugin>
    <groupId>org.patrodyne.jvnet</groupId>
    <artifactId>hisrc-hyperjaxb-maven-plugin</artifactId>
    ...
</plugin>
~~~

## Product Model

[Product Model][7]

# End of this Exploration

-30-

<!-- References -->

[1]: https://www.w3.org/TR/xml/
[2]: https://www.slf4j.org/
[3]: https://en.wikipedia.org/wiki/ISO_8601
[4]: https://github.com/eclipse-ee4j/jaxb-ri/blob/2.3.2-RI-RELEASE/jaxb-ri/xjc/src/main/schemas/com/sun/tools/xjc/reader/xmlschema/bindinfo/binding.xsd?ts=4
[5]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/explore/Ex001-JustProduct/src/test/java/org/patrodyne/jvnet/hyperjaxb/ex001/Explorer.java?ts=4
[6]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/explore/Ex001-JustProduct/project-pom.xml?ts=4
[7]: https://raw.githubusercontent.com/patrodyne/hisrc-hyperjaxb/master/ejb/explore/Ex001-JustProduct/src/test/resources/JustProduct.svg
[8]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/explore/Ex001-JustProduct/src/main/resources/Product.xsd?ts=4
[9]: https://github.com/patrodyne/hisrc-hyperjaxb/blob/master/ejb/explore/Ex001-JustProduct/src/main/resources/Product.xjb?ts=4
