[![Cognifide logo](docs/cognifide-logo.png)](http://cognifide.com)

![GitHub tag (latest SemVer)](https://img.shields.io/github/v/tag/Cognifide/aem-stubs)
![GitHub All Releases](https://img.shields.io/github/downloads/Cognifide/aem-stubs/total)
![Java CI with Gradle](https://github.com/Cognifide/aem-stubs/workflows/Java%20CI%20with%20Gradle/badge.svg)
[![Apache License, Version 2.0, January 2004](docs/apache-license-badge.svg)](http://www.apache.org/licenses/)

<p>
  <img src="docs/logo-text.svg" alt="AEM Stubs" width="300"/>
</p>

Tool for providing sample data for AEM applications in a simple and flexible way.

By providing JSON mapping file or implementing a [Groovy](http://groovy-lang.org/syntax.html) script using [WireMock](http://wiremock.org) or [Moco](https://github.com/dreamhead/moco) framework be able to mock your 3rd party services within minutes without a need to set up separate mock servers (infrastructure).

Simply [install](#installation) ready-to-use CRX package on AEM instance and start stubbing!

![Screenshot](docs/screenshot2.jpg)

Main concepts of AEM Stubs tool are:

* **Simplicity** 
    * Creating stubs should be as much simple as it is possible,
    * Reusing widely-known and battle-tested stubbing technology (avoiding reinventing a wheel), 
* **Reactivity**
    * Trivial tool deployment - installable on AEM via all-in-one CRX package in the time when stubs are actually needed,
    * Trivial stubs deployment - reloading stubs tied to regular AEM application deployment.
    
## Table of contents

  * [Frameworks](#frameworks)
  * [Installation](#installation)
     * [Post installation steps](#post-installation-steps)
  * [Compatibility](#compatibility)
  * [Documentation](#documentation)
     * [Basics](#basics)
     * [OSGi configuration](#osgi-configuration)
     * [Stub script API](#stub-script-api)
        * [Pre-defined variables](#pre-defined-variables)
        * [Framework-specific stub script API](#framework-specific-stub-script-api)
  * [Other tools](#other-tools)
  * [Contributing](#contributing)
  * [Building](#building)
  * [License](#license)

## Frameworks

At first, it is needed to decide which stubbing framework is more preferred to use.

Both frameworks have their own pros and cons, see comparison:

| Topic \ Framework | WireMock                   | Moco                                                   | Comments                                                                                                                                                                                                                                                                                                                                                                                                                    |
|-------------------|----------------------------|--------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Installation      | All-in-one AEM package     | All-in-one AEM package                                 | Both distributions could be easily deployed via single package installation in CRX Package Manager.                                                                                                                                                                                                                                                                                                                         |
| HTTP server       | AEM built-in (Jetty)       | Separate running also on AEM at dedicated port (Netty) | As of Moco is not using AEM HTTP Server but running its own (faster, asynchronous), it is required to ensure that port<br>used by Moco is free and not blocked by firewall on some deployment servers.<br>As of WireMock is running on same server as AEM, all mocked paths need to be prefixed (by default with `stubs/`).<br>When using Moco prefixes are not needed as of it is a dedicated HTTP server with mocks only. |
| Popularity        | Big - 3.9k stars on GitHub | Big - 3.1k stars on GitHub                             | WireMock is a little bit more popular and has probably better documentation and more materials available on the web.                                                                                                                                                                                                                                                                                                        |
| API               | Rich, old-fashioned        | Rich, modern                                           | Moco API seems to be written with extendability in mind. Has many interfaces, builders etc available. WireMock API has less such extension points and generally does not look so elegant.                                                                                                                                                                                                                                   |

After choosing stubbing framework, it is recommended to explore dedicated AEM Stubs **framework related documentation**:

* [AEM Stubs - WireMock](wiremock)
* [AEM Stubs - Moco](moco)

However, keep in mind using both frameworks on AEM is also supported. It may be useful when some features are missing in one framework, but they are available in another one.

## Installation

Simply install ready to use AEM package downloaded from GitHub [releases](https://github.com/Cognifide/aem-stubs/releases) section.

Distribution types:

* all - both frameworks with Groovy OSGi bundle included,
* app - both frameworks without Groovy OSGi bundle included.
* wiremock-all - WireMock framework only with Groovy OSGi bundle included,
* wiremock-app - WireMock framework only without Groovy OSGi bundle included.
* moco-all - Moco framework only with Groovy OSGi bundle included,
* moco-app - Moco framework only without Groovy OSGi bundle included.

### Post installation steps

Remember that using AEM Stubs on AEM Publish instances may require an additional configuration.

In case of WireMock, ensure that WireMock Servlet prefix `/stubs` is not filtered by AEM Dispatcher configuration. Alternatively, by creating OSGi configuration, update that path prefix to path which is already accessible.

When using Moco, ensure that Moco Stubs Server (running by default on port 5555) is not blocked by firewall. Alternatively, by creating OSGi configuration, update that port number to port which is already accessible.

## Compatibility

| AEM Stubs      | AEM           |
|----------------|---------------|
| 1.0.0, 1.0.1   | 6.4, 6.5      |
| >= 1.0.2       | 6.3, 6.4, 6.5 |

Note that AEM Stubs is using Groovy scripts concept. However it is **not** using [AEM Groovy Console](https://github.com/icfnext/aem-groovy-console). It is done intentionally, because Groovy Console has close dependencies to concrete AEM version.
AEM Stubs tool is implemented in a AEM version agnostic way, to make it more universal and more error-prone when AEM version is changing. 
It is compatible with AEM Groovy Console - simply install one of AEM Stubs distributions without Groovy console OSGi bundle included as it is usually provided by Groovy Console AEM package. 

## Documentation

### Basics

Stubs could be provided by:

* JSON mapping files (_*.stub.json_) so called stub mappings e.g file named _my-feature.stub.json_,
* Groovy Scripts (_*.stub.groovy_) so called stub scripts e.g file named _my-feature.stub.groovy_.

Format of contents of these files depends on chosen [framework](#frameworks).
 
For example, when decided to choose WireMock framework, then these files should be put into AEM under path _/conf/stubs/wiremock/*_ 
via CRX package with corresponding Vault workspace filter:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<workspaceFilter version="1.0">
    <filter root="/apps/example"/>
    <filter root="/conf/stubs/wiremock/example"/>
</workspaceFilter>
```

Stub mappings or scripts could be organized into folders tree. 
Any other assets used by scripts like images, extra JSON files could be also freely put close to stub scripts or mappings.
Indeed, dedicated extensions _*.stub.json_ and _*.stub.groovy_ are introduced to distinguish these assets from automatically loaded scripts and mappings.

Note that mappings and scripts under path _/conf/stubs/wiremock/samples_ will not be considered as of they are excluded by default configuration of Stub Manager OSGi service.
Which means to use samples, it is needed to remove path exclusion, rename a folder or copy sample files to other folder.

### OSGi configuration

There are framework specific OSGi configurations ([WireMock](moco#osgi-configuration), [Moco](moco#osgi-configuration)) and common listed below:

[Stub Manager](http://localhost:4502/system/console/configMgr/com.cognifide.aem.stubs.core.ConfigurableStubManager)

<img src="docs/osgi-config-stub-manager2.png" width="50%">
  
### Stub script API

#### Pre-defined variables:

* [stubs](core/src/main/java/com/cognifide/aem/stubs/core/Stubs.java) - use it to control actual Stubs server ([WireMock](wiremock/src/main/java/com/cognifide/aem/stubs/wiremock/WireMockStubs.java) or [Moco](moco/src/main/java/com/cognifide/aem/stubs/moco/MocoStubs.java)),
* [resourceResolver](https://sling.apache.org/apidocs/sling11/org/apache/sling/api/resource/ResourceResolver.html) - for accessing AEM repository,
* [repository](core/src/main/java/com/cognifide/aem/stubs/core/script/Repository.java) - for easier using repository e.g reading file contents,
* [logger](https://github.com/qos-ch/slf4j/blob/master/slf4j-api/src/main/java/org/slf4j/Logger.java) - SLF4j logger connected to script being run.

#### Framework-specific stub script API

See also framework-specific stub script API: 

* [Wiremock](wiremock#stub-script-api)
* [Moco](moco#stub-script-api).

## Other tools

There are no other dedicated tools for stubbing data available for AEM and it was main purpose to create AEM Stubs tool.

## Authors

* [Krystian Panek](mailto:krystian.panek@cognifide.com) - Project Founder, Main Developer,
* [Piotr Marcinkowski](mailto:piotr.marcinkowski@cognifide.com) - Main Developer.

## Contributing

Issues reported or pull requests created will be very appreciated. 

1. Fork plugin source code using a dedicated GitHub button.
2. Do code changes on a feature branch created from *develop* branch.
3. Create a pull request with a base of *develop* branch.

## Building

Project build is based on [Gradle](https://docs.gradle.org/current/userguide/userguide.html) and using [Gradle AEM Plugin](https://github.com/Cognifide/gradle-aem-plugin).

To setup AEM instance, provide details required by running command:

```bash
sh gradlew props
```

Fill opened GUI dialog, then click execute button.

To build CRX package and deploy it on AEM instance, simply run command:

```bash
sh gradlew
```

That's it.

## License

**AEM Stubs** is licensed under the [Apache License, Version 2.0 (the "License")](https://www.apache.org/licenses/LICENSE-2.0.txt)
