import cn.lalaki.pub.BaseCentralPortalPlusExtension
import java.net.URI
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

plugins {
    id("maven-publish")
    signing
    id("cn.lalaki.central") version("1.2.5")
}

group = "dk.stuart"
version = "1.0.1"

repositories {
    mavenCentral()
}

class SystemPropertyOrEnvironmentDelegate(private val key: String): ReadOnlyProperty<Nothing?, String?> {
    override fun getValue(thisRef: Nothing?, property: KProperty<*>): String? {
        return System.getProperty(key) ?: System.getenv(key)
    }
}

fun systemPropertyOrEnvironment(key: String) = SystemPropertyOrEnvironmentDelegate(key)


publishing {
    publications {
        create<MavenPublication>("native-agent") {
            group = "dk.stuart"
            artifactId = "jtestmemprofiler-native-agent"
            version = "1.0.1"

            artifact("artifacts/JTestMemProfiler-jdk17-v${version}.dll") {
                classifier = "windows-x86_64-jdk17"
                extension = "dll"
            }

            artifact("artifacts/JTestMemProfiler-jdk21-v${version}.dll") {
                classifier = "windows-x86_64-jdk21"
                extension = "dll"
            }

            artifact("artifacts/libJTestMemProfiler-jdk17-v${version}.dylib") {
                classifier = "osx-x86_64-jdk17"
                extension = "dylib"
            }

            artifact("artifacts/libJTestMemProfiler-jdk21-v${version}.dylib") {
                classifier = "osx-x86_64-jdk21"
                extension = "dylib"
            }

            artifact("artifacts/libJTestMemProfiler-jdk17-v${version}.so") {
                classifier = "linux-x86_64-jdk17"
                extension = "so"
            }

            artifact("artifacts/libJTestMemProfiler-jdk21-v${version}.so") {
                classifier = "linux-x86_64-jdk21"
                extension = "so"
            }

            pom {
                name.set("JTestMemProfiler native agent")
                description.set("Native agent shared objects using JVMTI to track memory allocation")
                url.set("https://github.com/hstuart/jtestmemprofiler-cpp")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("hstuart")
                        name.set("Henrik Stuart")
                        email.set("github@hstuart.dk")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/hstuart/jtestmemprofiler-cpp")
                    developerConnection.set("scm:git:ssh://git@github.com/hstuart/jtestmemprofiler-cpp")
                    url.set("https://github.com/hstuart/jtestmemprofiler-cpp")
                }
            }
        }
    }

    repositories {
        maven {
            url =  project.layout.buildDirectory.dir("nonexist").get().asFile.toURI()
        }
    }
}

signing {
    sign(publishing.publications)
    useInMemoryPgpKeys(System.getenv("GPG_PRIVATE_KEY"), System.getenv("GPG_PASSPHRASE"))
}

centralPortalPlus {
    val mavenUsername by systemPropertyOrEnvironment("MAVEN_CENTRAL_USERNAME")
    val mavenPassword by systemPropertyOrEnvironment("MAVEN_CENTRAL_PASSWORD")

    url = project.layout.buildDirectory.dir("nonexist").get().asFile.toURI()
    username = mavenUsername
    password = mavenPassword
    publishingType = BaseCentralPortalPlusExtension.PublishingType.USER_MANAGED
}
