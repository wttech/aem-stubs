[![Cognifide logo](docs/cognifide-logo.png)](http://cognifide.com)

[![Apache License, Version 2.0, January 2004](docs/apache-license-badge.svg)](http://www.apache.org/licenses/)

# AEM Stubs

Tool for providing sample data for AEM applications in a simple and flexible way.

By implementing a script in [Groovy Console](https://github.com/icfnext/aem-groovy-console) using [Wiremock](http://wiremock.org) or [Moco](https://github.com/dreamhead/moco) framework be able to mock your 3rd party services within minutes without a need to set up separate mock servers (infrastructure).

Simply [install](#installation) ready-to-use CRX package on AEM instance and [start stubbing](#getting-started)!

## Frameworks

At first, it is needed to decide which framework is more preferred to use.
Both frameworks have their own pros and cons, see comparison:

| Topic \ Framework   	| Wiremock               	| Moco                                                 	| Comments                                                                                                                                                                     	|   	|
|---------------------	|------------------------	|------------------------------------------------------	|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	|---	|
| Installation        	| All-in-one AEM package 	| All-in-one AEM package                               	|                                                                                                                                                                              	|   	|
| Type of HTTP server 	| AEM built-in (Jetty)   	| Additional deployed on AEM, running on separate port 	| As of Moco is not using AEM HTTP Server but running its own, it is required to ensure that port used by Moco is free and not blocked by firewall on some deployment servers. 	|   	|
|                     	|                        	|                                                      	|                                                                                                                                                                              	|   	| 

## Building

Project build is based on [Gradle](https://docs.gradle.org/current/userguide/userguide.html) and using [Gradle AEM Plugin](https://github.com/Cognifide/gradle-aem-plugin).

To build CRX package and deploy it on AEM instance, simply run command:

```bash
sh gradlew
```

## License

**AEM Stubs** is licensed under the [Apache License, Version 2.0 (the "License")](https://www.apache.org/licenses/LICENSE-2.0.txt)
