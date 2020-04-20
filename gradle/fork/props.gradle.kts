import com.cognifide.gradle.aem.common.instance.local.Source
import com.cognifide.gradle.aem.common.instance.local.OpenMode
import com.neva.gradle.fork.ForkExtension

configure<ForkExtension> {
    properties {
        define("Build", mapOf(
                "webpackMode" to {
                    label = "Webpack Mode"
                    description = "Controls optimization of front-end resources (CSS/JS/assets) "
                    select("dev", "prod")
                },
                "testBrowser" to {
                    label = "Test Browser"
                    description = "Browser used when running functional tests powered by Cypress"
                    select("auto", "chrome", "chrome:canary", "chromium", "electron", "edge", "edge:canary", "firefox", "firefox:nightly")
                }
        ))

        define("Instance", mapOf(
                "instanceType" to {
                    label = "Type"
                    select("local", "remote")
                    description = "Local - instance will be created on local file system\nRemote - connecting to remote instance only"
                    controller { toggle(value == "local", "instanceRunModes", "instanceJvmOpts", "localInstance*") }
                },
                "instanceAuthorHttpUrl" to {
                    label = "Author HTTP URL"
                    url("http://localhost:4502")
                    optional()
                    description = "For accessing AEM author instance (leave empty to skip creating it)"
                },
                "instancePublishHttpUrl" to {
                    label = "Publish HTTP URL"
                    url("http://localhost:4503")
                    optional()
                    description = "For accessing AEM publish instance (leave empty to skip creating it)"
                },
                "instanceAuthorOnly" to {
                    label = "Author Only"
                    description = "Limits instances to work with to author instance only."
                    checkbox(false)
                    controller { other("instancePublishOnly").enabled = !value.toBoolean() }
                },
                "instancePublishOnly" to {
                    label = "Publish Only"
                    description = "Limits instances to work with to publish instance only."
                    checkbox(false)
                    controller { other("instanceAuthorOnly").enabled = !value.toBoolean() }
                },
                "instanceSatisfierEnabled" to {
                    label = "Satisfier Enabled"
                    description = "Turns on/off automated package pre-installation."
                    checkbox(true)
                },
                "instanceProvisionerEnabled" to {
                    label = "Provisioner Enabled"
                    description = "Turns on/off automated instance configuration."
                    checkbox(true)
                },
                "instanceAwaitUpHelpEnabled" to {
                    label = "Await Up Helping"
                    description = "Tries to start bundles automatically when instance is not stable longer time"
                    checkbox(true)
                }
        ))

        define("Local instance", mapOf(
                "localInstanceSource" to {
                    label = "Source"
                    description = "Controls how instances will be created (from scratch, backup or any available source)"
                    select(Source.values().map { it.name.toLowerCase() }, Source.AUTO.name.toLowerCase())
                },
                "localInstanceQuickstartJarUri" to {
                    label = "Quickstart URI"
                    description = "For file named 'cq-quickstart-x.x.x.jar'"
                },
                "localInstanceQuickstartLicenseUri" to {
                    label = "Quickstart License URI"
                    description = "For file named 'license.properties'"
                },
                "localInstanceBackupDownloadUri" to {
                    label = "Backup Download URI"
                    description = "For backup file. Protocols supported: SMB/SFTP/HTTP"
                    optional()
                },
                "localInstanceBackupUploadUri" to {
                    label = "Backup Upload URI"
                    description = "For directory containing backup files. Protocols supported: SMB/SFTP"
                    optional()
                },
                "localInstanceRunModes" to {
                    label = "Run Modes"
                    text("local")
                },
                "localInstanceJvmOpts" to {
                    label = "JVM Options"
                    text("-server -Xmx2048m -XX:MaxPermSize=512M -Djava.awt.headless=true")
                },
                "localInstanceOpenMode" to {
                    label = "Open Automatically"
                    description = "Open web browser when instances are up."
                    select(OpenMode.values().map { it.name.toLowerCase() }, OpenMode.ALWAYS.name.toLowerCase())
                },
                "localInstanceOpenPath" to {
                    label = "Open Path"
                    text("/")
                }
        ))

        define("Package", mapOf(
                "packageValidatorEnabled" to {
                    label = "Validator Enabled"
                    description = "Turns on/off package validation using OakPAL."
                    checkbox(true)
                },
                "packageNestedValidation" to {
                    label = "Nested Validation"
                    description = "Turns on/off separate validation of built subpackages."
                    checkbox(true)
                },
                "packageBundleTest" to {
                    label = "Bundle Test"
                    description = "Turns on/off running tests for built bundles put under install path."
                    checkbox(true)
                },
                "packageDamAssetToggle" to {
                    label = "Deploy Without DAM Worklows"
                    description = "Turns on/off temporary disablement of assets processing for package deployment time.\n" +
                            "Useful to avoid redundant rendition generation when package contains renditions synchronized earlier."
                    checkbox(true)
                }
        ))

        define("Authorization", mapOf(
                "companyUser" to {
                    label = "User"
                    description = "Authorized to access AEM files"
                    defaultValue = System.getProperty("user.name").orEmpty()
                    optional()
                },
                "companyPassword" to {
                    label = "Password"
                    description = "For above user"
                    optional()
                },
                "companyDomain" to {
                    label = "Domain"
                    description = "Needed only when accessing AEM files over SMB"
                    defaultValue = System.getenv("USERDOMAIN").orEmpty()
                    optional()
                }
        ))

        define("Other", mapOf(
                "notifierEnabled" to {
                    label = "Notifications"
                    description = "Controls displaying of GUI notifications (baloons)"
                    checkbox(true)
                }
        ))
    }
}
