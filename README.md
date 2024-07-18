[![WTT logo](docs/wtt-logo.png)](https://www.wundermanthompson.com)

[![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/wttech/aem-stubs)](https://github.com/wttech/aem-stubs/releases)
[![GitHub All Releases](https://img.shields.io/github/downloads/wttech/aem-stubs/total)](https://github.com/wttech/aem-stubs/releases)
[![Check](https://github.com/wttech/aem-stubs/workflows/Check/badge.svg)](https://github.com/wttech/aem-stubs/actions/workflows/check.yml)
[![Apache License, Version 2.0, January 2004](docs/apache-license-badge.svg)](http://www.apache.org/licenses/)

<p>
  <img src="docs/logo-text.svg" alt="AEM Stubs" width="300"/>
</p>

Tool for providing sample data for AEM applications in a simple and flexible way.

Simply [install](#installation) ready-to-use CRX package on AEM instance and start stubbing!

![Screenshot](docs/screenshot2.jpg)

AdaptTo 2020 Live Demo - <https://adapt.to/2020/en/schedule/lightning-talks/aem-stubs.html>

Main concepts of AEM Stubs tool are:

* **Simplicity**
    * Creating stubs should be as much simple as it is possible,
* **Reactivity**
    * Trivial tool deployment - installable on AEM via all-in-one CRX package in the time when stubs are actually needed,
    * Trivial stubs deployment - reloading stubs tied to regular AEM application deployment.

## Installation

Simply install ready to use AEM package downloaded from GitHub [releases](https://github.com/wttech/aem-stubs/releases) section.

### Post installation steps

Remember that using AEM Stubs on AEM Publish instances may require an additional configuration.

In case of WireMock, ensure that AEM Stubs Filter prefix `/stubs` is not filtered by AEM Dispatcher configuration. Alternatively, by creating OSGi configuration, update that path prefix to path which is already accessible.

## Compatibility

| AEM Stubs    | AEM           | Java  | Groovy |
|--------------|---------------|-------|--------|
| 1.0.0, 1.0.1 | 6.4, 6.5      | 8     | 2.x    |
| >= 1.0.2     | 6.3, 6.4, 6.5 | 8     | 2.x    |
| 2.0.0        | 6.3, 6.4, 6.5 | 11    | 2.x    |
| 2.0.1        | 6.3, 6.4, 6.5 | 8, 11 | 2.x    |
| 3.0.0        | 6.3, 6.4, 6.5 | 8, 11 | 4.x    |
| 4.0.0        | 6.3, 6.4, 6.5 | 8, 11 | 4.x    |

Note that AEM Stubs is using Groovy scripts concept. However it is **not** using [AEM Groovy Console](https://github.com/icfnext/aem-groovy-console). It is done intentionally, because Groovy Console has close dependencies to concrete AEM version.
AEM Stubs tool is implemented in a AEM version agnostic way, to make it more universal and more fault-tolerant when AEM version is changing.
It is compatible with AEM Groovy Console - simply install one of AEM Stubs distributions without Groovy console OSGi bundle included as it is usually provided by Groovy Console AEM package.

From AEM Stubs 3.x onwards, Groovy has been upgraded to version 4.x. Groovy has stopped providing a groovy-all 'uber' bundle. In this package, only the groovy 'core' bundle is included. It is possible to add extra Groovy modules in your own project.
A list of groovy subprojects can be found here at the [Groovy GitHub Project](https://github.com/apache/groovy/tree/master/subprojects)

## Documentation

### Basics

Stubs could be provided by:

* Groovy Scripts (_*.stub.groovy_) so called stub scripts e.g file named _my-feature.stub.groovy_.

For example, when decided to choose WireMock framework, then these files should be put into AEM under path _/conf/stubs/wiremock/*_
via CRX package with corresponding Vault workspace filter:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<workspaceFilter version="1.0">
    <filter root="/apps/mysite"/>
    <filter root="/conf/stubs/mysite"/>
</workspaceFilter>
```

### OSGi configuration

[Stub Filter](http://localhost:4502/system/console/configMgr/com.wttech.aem.stubs.core.StubFilter)
[Stub Repository](http://localhost:4502/system/console/configMgr/com.wttech.aem.stubs.core.StubRepository)

### Stub script API

#### Pre-defined variables:

* [resourceResolver](https://sling.apache.org/apidocs/sling11/org/apache/sling/api/resource/ResourceResolver.html) - for accessing AEM repository,
* [log](https://github.com/qos-ch/slf4j/blob/master/slf4j-api/src/main/java/org/slf4j/Logger.java) - SLF4j logger connected to script being run.
* [template](https://github.com/wttech/aem-stubs/blob/main-v4/core/src/main/java/com/wttech/aem/stubs/core/GroovyTemplate.java) - Template engine-based rendering


## Other tools

There are no other dedicated tools for stubbing data available for AEM and it was main purpose to create AEM Stubs tool.

## Authors

* [Krystian Panek](mailto:krystian.panek@vml.com) - Project Founder, Main Developer,
* [Piotr Marcinkowski](mailto:piotr.marcinkowski@vml.com) - Main Developer.

## Contributing

Issues reported or pull requests created will be very appreciated.

1. Fork plugin source code using a dedicated GitHub button.
2. Do code changes on a feature branch created from *main* branch.
3. Create a pull request with a base of *main* branch.

## Building

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

To build all the modules and deploy the `all` package to a local instance of AEM, run in the project root directory the following command:

    mvn clean install -PautoInstallSinglePackage

Or to deploy it to a publish instance, run

    mvn clean install -PautoInstallSinglePackagePublish

Or alternatively

    mvn clean install -PautoInstallSinglePackage -Daem.port=4503

Or to deploy only the bundle to the author, run

    mvn clean install -PautoInstallBundle

Or to deploy only a single content package, run in the sub-module directory (i.e `ui.apps`)

    mvn clean install -PautoInstallPackage

## License

**AEM Stubs** is licensed under the [Apache License, Version 2.0 (the "License")](https://www.apache.org/licenses/LICENSE-2.0.txt)
