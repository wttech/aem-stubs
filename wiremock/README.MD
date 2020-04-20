# AEM Stubs - Wiremock

AEM Stubs implementation for [Wiremock](http://wiremock.org).

## Getting Started

### Prerequisites

It is required [AEM Groovy Console](https://github.com/icfnext/aem-groovy-console)

### Instalation

The tool is delivered as a typical AEM (CRX) package ready to be installed using [Package Manager](https://docs.adobe.com/content/help/en/experience-manager-65/administering/contentmanagement/package-manager.html#installing-packages)

The newest AEM packages are available [here](https://). 

There two distributions variants:

| Package name | Description 
 :---: | :---: | 
| stubs-all-wiremock | Package includes [AEM Groovy Console](https://github.com/icfnext/aem-groovy-console) 
| stubs-wiremock | Only AEM Stubs - Wiremock


## OSGi Configuration

AEM Stubs - Wiremock Servlet Configuration (com.cognifide.aem.stubs.wiremock.servlet.WiremockServletRegister)

| Property | Description | Default |
 :---: | :---: | :---: |
| path | URL prefix for AEM Stubs.  | /wiremock 


## How to use

All stubs need to be defined via a groovy script. For example:

```
import static com.github.tomakehurst.wiremock.client.WireMock.*;

stubs.define "example", { wm ->
    wm.stubFor(get("/json")
            .willReturn(okJson("{ \"message\": \"Hello\" }")))
}

```
Above stub will be available under `http://localhost:4502/wiremock/json`
### Scripts location
All scripts need to be placed under `/var/groovyconsole/scripts/stubs/` Every script change will automatically reload stubs definitions.

### Examples
More examples can be found under `/var/groovyconsole/scripts/stubs/samples`.


## Supported features

Almost all Wiremock Java features are supported. Code snippets can be found directly on Wiremock documentation:
 
 - [Wiremock stubbing](http://wiremock.org/docs/stubbing/)  
 - [Request Matching](http://wiremock.org/docs/request-matching/)
 - [Proxying](http://wiremock.org/docs/proxying/)
 
## Not supported features

 - JSON API 
 - [Simulating Faults](http://wiremock.org/docs/simulating-faults/)
 

## Comparison to other tools
There no other tools for stubbing API availbe for AEM
