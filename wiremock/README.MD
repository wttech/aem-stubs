# AEM Stubs - WireMock

Allows providing stubs (or mocks) by implementing [Groovy](http://groovy-lang.org/syntax.html) script basing on [WireMock](http://wiremock.org) framework.

Sample **stub script**:

```groovy
stubs.server.with {
    stubFor(get("/fine-with-body")
        .willReturn(ok("body content")))

    stubFor(any(urlPathEqualTo("/everything"))
  		.withQueryParam("secret", equalTo("WireMock"))
  		.withBasicAuth("foo", "bar")
        .willReturn(ok("body content under auth"))) 
}
```

## Table of contents

  * [Installation](#installation)
  * [Documentation](#documentation)
     * [OSGi Configuration](#osgi-configuration)
     * [Supported features](#supported-features)
        * [Stubbing](#stubbing)
        * [Request Matching](#request-matching)
        * [Proxying](#proxying)
        * [Response Templating](#response-templating)
        * [Templating parameters](#templating-parameters)
     * [Not supported features](#not-supported-features)

## Installation

* all package (all-in-one) - <https://nexus.cognifide.com/content/repositories/cognifide-internal/com/cognifide/aem/stubs/wiremock-all/>
* app package (without Groovy OSGi bundle) - <https://nexus.cognifide.com/content/repositories/cognifide-internal/com/cognifide/aem/stubs/wiremock-app/>

## Documentation

All stubs need to be defined via one or many stub scripts located in AEM repository under path */var/stubs/wiremock* (by default).
For example, consider contents of file */var/stubs/wiremock/samples/hello.groovy*:

```
stubs.server.with {
        stubFor(get("/json")
            .willReturn(okJson("{ \"message\": \"Hello\" }")))
}
```

Above stub will be accessible at URL <http://localhost:4502/stubs/json>. 
Note that prefix `/stubs` is configurable via OSGi configuration.

### OSGi Configuration

Be aware of common / WireMock agnostic [OSGi configuration](../#osgi-configuration) at first.
Then check see Wiremock specific OSGi configuration below:

* [WireMock Stubs](http://localhost:4502/system/console/configMgr/com.cognifide.aem.stubs.wiremock.WiremockStubs)

    ![OSGi config - WireMock Stubs](docs/osgi-config-wiremock-stubs.png)

### Stub script API

At first, see [common](../#stub-script-api) script API.

Pre-defined variables:

* [stubs.server](https://bitbucket.cognifide.com/users/krystian.panek/repos/aem-stubs/browse/wiremock/src/main/java/com/cognifide/aem/stubs/wiremock/WireMockApp.java) - use it to define WireMock stubs.

Default imports (not needed to be specified explicitly in scripts):

* [_com.cognifide.aem.stubs.wiremock.WireMockUtils.\*_](https://bitbucket.cognifide.com/users/krystian.panek/repos/aem-stubs/browse/wiremock/src/main/java/com/cognifide/aem/stubs/wiremock/WireMockUtils.java) - proxied WireMock API.
* [_com.github.tomakehurst.wiremock.http.Request.\*_](https://github.com/tomakehurst/wiremock/blob/master/src/main/java/com/github/tomakehurst/wiremock/http/Request.java) - WireMock request methods & constants.

### Supported features

Almost all WireMock Java features are supported. Code snippets can be found directly on WireMock documentation:
```
stubs.server.with {
        /* use WireMock snippets here */
}
```
 
#### Stubbing

A core feature of WireMock is the ability to return canned HTTP responses for requests matching criteria.

```
stubs.server.with {
    stubFor(get(urlEqualTo("/some/thing"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "text/plain")
                .withBody("Hello world!")));
}
```

WireMock [stubbing](http://wiremock.org/docs/stubbing/) documentation

#### Request Matching

WireMock supports matching of requests to stubs and verification queries using many attributes. Detailed documentation available [here](http://wiremock.org/docs/request-matching/) 

#### Proxying

WireMock can selectively proxy requests through to other hosts. Code snippets can be found [here](http://wiremock.org/docs/proxying/)

#### Response Templating

[WireMock](http://wiremock.org) supports by default [handlebars](http://handlebarsjs.com/) as templating engine.
[Handlebars](http://handlebarsjs.com/) is not `OSGI ready` library and is not supported by AEM Stubs.

Current implementation supports basic functionality of [Pebble v3.1.0](https://pebbletemplates.io/) templating engine.
It implemented by WireMock [transformer extension](http://wiremock.org/docs/extending-wiremock/) named `pebble-response-template`  

```
stubs.server.with {
    stubFor(get(urlPathEqualTo("/templated"))
            .willReturn(aResponse()
                    .withBody("{{request.path[0]}}")
                    .withTransformers("pebble-response-template")))
}
```

The request model available from a template is the same as described on WireMock response templating [documentaion](http://wiremock.org/docs/response-templating/)

 __Note__ 
 
Method `.withTransformers("pebble-response-template")` need to be call on response builder to specify  [Pebble](https://pebbletemplates.io/) transformer on each stub mapping.
There is no possibility to switch it on globally for AEM Stubs.

#### Templating parameters

Parameters can be passed to WireMock extension `pebble-response-template` by calling `withTransformerParameter` method

```
stubs.server.with {
    stubFor(get(urlPathEqualTo("/templated-file"))
            .willReturn(aResponse()
                    .withBodyFile("samples/template.json")
                    .withHeader("Content-Type", "application/json")
                    .withTransformerParameter("message", "Hello Templates!")
                    .withTransformers("pebble-response-template")))

}
```

Also, dynamic values are possible

```
stubs.server.with {
    stubFor(get(urlPathEqualTo("/templated-dynamic"))
            .willReturn(aResponse()
                    .withBody("{{parameters.date}}")
                    .withTransformerParameter("date", {new Date()})
                    .withTransformers("pebble-response-template")))

}
```

### Not supported features

* JSON API - preloading stubs defined as JSON files are not supported (yet),
* [Simulating Faults](http://wiremock.org/docs/simulating-faults/).
