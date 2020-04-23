# AEM Stubs - WireMock

Implementation for [WireMock](http://wiremock.org).

## Getting Started

### Installation

It is a typical AEM package ready to be installed in CRX Package Manage [Package Manager](https://docs.adobe.com/content/help/en/experience-manager-65/administering/contentmanagement/package-manager.html#installing-packages)

The newest AEM packages are available [here](https://). 

There two distributions variants:

| Package name | Description 
 :---: | :---: | 
| stubs-wiremock-all | Package includes [AEM Groovy Console](https://github.com/icfnext/aem-groovy-console) 
| stubs-wiremock-app | Only AEM Stubs - WireMock


<a id="osgi-configuration"/>
## OSGi Configuration

AEM Stubs - Wiremock Server 

| Property | Description | Default |
 :---: | :---: | :---: |
| Servlet Prefix | URL prefix for AEM Stubs.  | /stubs 

AEM Stubs - Groovy Script Manager

| Property | Description | Default |
 :---: | :---: | :---: |
| Scripts Root Path | Root path for groovy stub scripts  | /var/groovyconsole/scripts/stubs
| Scripts Excluded Paths | Excluded paths from automatic rerun stub script after change  | \*\*/internals/\*

## How to use

All stubs need to be defined via a stub script. For example:

```
import static com.cognifide.aem.stubs.wiremock.Wiremock.*

stubs.server.with {
        stubFor(get("/json")
            .willReturn(okJson("{ \"message\": \"Hello\" }")))
}
```
Above stub will be accessible via HTTP at `http://localhost:4502/stubs/json`. Note that prefix `/stubs` is configurable. See [OSGi Configuration](osgi-configuration)

### Stub scripts location

By default stub scripts are located `/var/groovyconsole/scripts/stubs` as subfolder [Groovy Console](https://github.com/icfnext/aem-groovy-console) defualt path.
It can be changed by [OSGi Configuration](osgi-configuration) 

### Examples

More examples can be found under `/var/groovyconsole/scripts/stubs/samples`.

## Supported features

Almost all WireMock Java features are supported. Code snippets can be found directly on WireMock documentation:
```
import static com.cognifide.aem.stubs.wiremock.Wiremock.*
stubs.server.with {
        [SNIPPET]
}
```
Placeholder [SNIPPET] can be replaced directly by code examples from [WireMock](http://wiremock.org)
 
### Stubbing

A core feature of WireMock is the ability to return canned HTTP responses for requests matching criteria.
```
import static com.cognifide.aem.stubs.wiremock.Wiremock.*
stubs.server.with {
    stubFor(get(urlEqualTo("/some/thing"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "text/plain")
                .withBody("Hello world!")));
}
```
WireMock [stubbing](http://wiremock.org/docs/stubbing/) documentation

### Request Matching

WireMock supports matching of requests to stubs and verification queries using many attributes. Detailed documentation available [here](http://wiremock.org/docs/request-matching/) 

### Proxying

WireMock can selectively proxy requests through to other hosts. Code snippets can be found [here](http://wiremock.org/docs/proxying/)

### Response Templating
[WireMock](http://wiremock.org) supports by default [handlebars](http://handlebarsjs.com/) as templating engine.
[Handlebars](http://handlebarsjs.com/) is not `OSGI ready` library and is not supported by AEM Stubs.
Current implementation supports basic functionality of [Pebble v3.1.0](https://pebbletemplates.io/) templating engine.
It implemented by WireMock [transformer extension](http://wiremock.org/docs/extending-wiremock/) named `pebble-response-template`  
```
import static com.cognifide.aem.stubs.wiremock.Wiremock.*
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
import static com.cognifide.aem.stubs.wiremock.Wiremock.*
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
import static com.cognifide.aem.stubs.wiremock.Wiremock.*
stubs.server.with {
    stubFor(get(urlPathEqualTo("/templated-dynamic"))
            .willReturn(aResponse()
                    .withBody("{{parameters.date}}")
                    .withTransformerParameter("date", {new Date()})
                    .withTransformers("pebble-response-template")))

}
```

## Not supported features

 - JSON API 
 - [Simulating Faults](http://wiremock.org/docs/simulating-faults/)

Not sure if it will be a part of the next version of AEM Stub due to complexity and much effort to be done. 

## Comparison to other tools

There no other tools for stubbing API available for AEM
