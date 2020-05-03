# AEM Stubs - Moco

Allows providing stubs (or mocks) by implementing [Groovy](http://groovy-lang.org/syntax.html) script basing on [Moco framework](https://github.com/dreamhead/moco).

Sample **stub script**:

```groovy
stubs.server
        .get(by(uri("/hello-world")))
        .response("Hello! I am sample stub.")

stubs.server
        .get(
                and(
                        by(uri("/secured")),
                        eq(query("password"), "secret")
                )
        )
        .response("Secured endpoint revealed!")

stubs.server
        .get(by(uri("/current-date")))
        .response(template('Today date is "${date}"', "date", dynamicVar({ new Date() })))
```

## Installation

* all package (all-in-one) - <https://nexus.cognifide.com/content/repositories/cognifide-internal/com/cognifide/aem/stubs/moco-all/>
* app package (without Groovy OSGi bundle) - <https://nexus.cognifide.com/content/repositories/cognifide-internal/com/cognifide/aem/stubs/moco-app/>

## Documentation

To create stubs for web services or more basically mock any data, it is needed to only create a **stub script** under pre-configured path, e.g */var/stubs/hello.groovy*.

### Stub script API

Pre-defined variables:

* [stubs](https://bitbucket.cognifide.com/users/krystian.panek/repos/aem-stubs/browse/moco/src/main/java/com/cognifide/aem/stubs/moco/MocoStubs.java) - Moco Stubs service - use it to control Moco HTTP Server,
* [stubs.server](https://github.com/dreamhead/moco/blob/master/moco-core/src/main/java/com/github/dreamhead/moco/HttpServer.java) - Moco HTTP Server - use it to define stubs,
* [resourceResolver](https://sling.apache.org/apidocs/sling11/org/apache/sling/api/resource/ResourceResolver.html) - for accessing AEM repository,
* [logger](https://github.com/qos-ch/slf4j/blob/master/slf4j-api/src/main/java/org/slf4j/Logger.java) - SLF4j logger.

Default imports:

* _com.github.dreamhead.moco.\*_ - Moco framework API
* _com.cognifide.aem.stubs.moco.Moco.\*_ - AEM related extensions for Moco framework API

## Useful links

* https://github.com/dreamhead/moco/blob/master/moco-doc/apis.md

## Known issues

None.
