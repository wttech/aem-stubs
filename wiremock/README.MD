# AEM Stubs - Wiremock

Implementation for [Wiremock](http://wiremock.org).

## Getting Started

### Instalation

It is a typical AEM package ready to be installed in CRX Package Manage [Package Manager](https://docs.adobe.com/content/help/en/experience-manager-65/administering/contentmanagement/package-manager.html#installing-packages)

The newest AEM packages are available [here](https://). 

There two distributions variants:

| Package name | Description 
 :---: | :---: | 
| stubs-all-wiremock | Package includes [AEM Groovy Console](https://github.com/icfnext/aem-groovy-console) 
| stubs-wiremock | Only AEM Stubs - Wiremock


<a id="osgi-configuration"/>
## OSGi Configuration

AEM Stubs - Wiremock Servlet Configuration (com.cognifide.aem.stubs.wiremock.servlet.WiremockServletRegister)

| Property | Description | Default |
 :---: | :---: | :---: |
| path | URL prefix for AEM Stubs.  | /wiremock 


## How to use

All stubs need to be defined via a groovy script. For example:

```
import static com.cognifide.aem.stubs.wiremock.Wiremock.*

stubs.define "hello-world", {
    it.with {
        stubFor(get("/json")
            .willReturn(okJson("{ \"message\": \"Hello\" }")))
    }
}
```
Above stub will be accessible via HTTP at `http://localhost:4502/wiremock/json`. Note that prefix `/wiremock` is configurable. See [OSGi Configuration](osgi-configuration)

### Scripts location
All scripts need to be placed under `/var/groovyconsole/scripts/stubs/` Every script change will automatically reload stubs definitions.

### Examples
More examples can be found under `/var/groovyconsole/scripts/stubs/samples`.


## Supported features

Almost all Wiremock Java features are supported. Code snippets can be found directly on Wiremock documentation:
 
 - [Wiremock stubbing](http://wiremock.org/docs/stubbing/)  
 - [Request Matching](http://wiremock.org/docs/request-matching/)
 - [Proxying](http://wiremock.org/docs/proxying/)

Code snippets can be copied directly from [Wiremock](http://wiremock.org) into [SNIPPET] placeholder.

```
import static com.cognifide.aem.stubs.wiremock.Wiremock.*
stubs.define "my-definition", {
    it.with {
        [SNIPPET]
    }
}
```

## Not supported features

 - JSON API 
 - [Simulating Faults](http://wiremock.org/docs/simulating-faults/)

Not sure if it will be a part of next version of AEM Stub due to complexity and much effort to be done. 

## Comparison to other tools
There no other tools for stubbing API availbe for AEM
