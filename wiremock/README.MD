# AEM Stubs - WireMock

Allows providing stubs (or mocks) by implementing [Groovy](http://groovy-lang.org/syntax.html) script basing on [WireMock](http://wiremock.org) framework.

Sample **stub mappings** (_hello-world.stub.json_):

```json
{
  "mappings": [
    {
      "request": {
        "method": "GET",
        "url": "/hello-world"
      },
      "response": {
        "status": 200,
        "body": "Hello! I am sample stub.",
        "headers": {
          "Content-Type": "text/plain"
        }
      }
    }
  ]
}
```

Sample **stub script** (_hello-world.stub.groovy_):

```groovy
stubs.server.with {
    stubFor(get("/hello-world")
        .willReturn(ok("Hello! I am sample stub.")))

    stubFor(get(urlPathEqualTo("/protected-resource"))
  		.withQueryParam("secret", equalTo("WireMock"))
  		.withBasicAuth("foo", "bar")
        .willReturn(ok("Now you now that I am a secret!"))) 
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

Download appropriate distribution from [releases](https://github.com/Cognifide/aem-stubs/releases) section:

* wiremock-all - WireMock framework only with Groovy OSGi bundle included,
* wiremock-app - WireMock framework only without Groovy OSGi bundle included.
* all - WireMock and Moco frameworks with Groovy OSGi bundle included,
* app - WireMock and Moco frameworks without Groovy OSGi bundle included.

## Documentation

All stubs need to be defined via one or many stub scripts located in AEM repository under path */var/stubs/wiremock* (by default).
For example, consider contents of file */var/stubs/wiremock/hello.stub.groovy*:

```
stubs.server.with {
        stubFor(get("/json")
            .willReturn(okJson("{ \"message\": \"Hello\" }")))
}
```

Above stub will be accessible at URL <http://localhost:4502/stubs/json>. 
Note that prefix `/stubs` is configurable via OSGi configuration.

### OSGi Configuration

Be aware of common / WireMock agnostic [OSGi configuration](../README.MD#osgi-configuration) at first.

Then check see Wiremock specific OSGi configuration below:

[WireMock Stubs](http://localhost:4502/system/console/configMgr/com.cognifide.aem.stubs.wiremock.WiremockStubs)

<img src="docs/osgi-config-wiremock-stubs.png" width="50%">

### Stub script API

At first, see [common](../#stub-script-api) script API.

Pre-defined variables:

* [stubs.server](src/main/java/com/cognifide/aem/stubs/wiremock/WireMockApp.java) - use it to define WireMock stubs.

Default imports (not needed to be specified explicitly in scripts):

* [_com.cognifide.aem.stubs.wiremock.WireMockUtils.\*_](src/main/java/com/cognifide/aem/stubs/wiremock/WireMockUtils.java) - proxied WireMock API.
* [_com.github.tomakehurst.wiremock.http.Request.\*_](https://github.com/tomakehurst/wiremock/blob/master/src/main/java/com/github/tomakehurst/wiremock/http/Request.java) - WireMock request methods & constants.

### Supported features

Almost all WireMock Java features are supported. Code snippets could be found directly in WireMock documentation:

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

Consider discovering related WireMock [stubbing](http://wiremock.org/docs/stubbing/) documentation.

#### Request Matching

WireMock supports matching of requests to stubs and verification queries using many attributes. Detailed documentation is available [here](http://wiremock.org/docs/request-matching/).

#### Proxying

WireMock can selectively proxy requests through to other hosts. Code snippets could be found [here](http://wiremock.org/docs/proxying/)

#### Response Templating

[WireMock](http://wiremock.org) supports by default [handlebars](http://handlebarsjs.com/) as templating engine.
[Handlebars](http://handlebarsjs.com/) is not an OSGi-ready library and is not supported by AEM Stubs.

Current implementation supports basic functionality of [Pebble](https://pebbletemplates.io/) templating engine.

```
stubs.server.with {
    stubFor(get(urlPathEqualTo("/templated"))
            .willReturn(aResponse()
                    .withBody("{{request.path[0]}}")
}
```

The request model available from a template is the same as described on WireMock response templating [documentation](http://wiremock.org/docs/response-templating/).

#### Templating parameters

Parameters can be passed to WireMock extension `pebble-response-template` (loaded by default) by calling `withTransformerParameter` method:

```
stubs.server.with {
    stubFor(get(urlPathEqualTo("/templated-file"))
            .willReturn(aResponse()
                    .withBodyFile("samples/template.json")
                    .withHeader("Content-Type", "application/json")
                    .withTransformerParameter("message", "Hello Templates!")

}
```

Also, dynamic values are possible:

```
stubs.server.with {
    stubFor(get(urlPathEqualTo("/templated-dynamic"))
            .willReturn(aResponse()
                    .withBody("{{parameters.date}}")
                    .withTransformerParameter("date", {new Date()})

}
```

### Not supported features

* JSON API - preloading stubs defined as JSON files are not supported (yet),
* [Simulating Faults](http://wiremock.org/docs/simulating-faults/).
