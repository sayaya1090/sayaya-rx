plugins {
    kotlin("jvm") version "2.2.21"
    id("dev.sayaya.gwt") version "2.2.7"
    signing
    id("maven-publish")
    id("com.vanniktech.maven.publish") version "0.35.0"
}
kotlin {
    jvmToolchain(21)
}
repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/sayaya1090/maven")
        credentials {
            username = project.findProperty("github_username") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("github_password") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}
group = "dev.sayaya"
version = "2.2.0"

dependencies {
    implementation("org.jboss.elemento:elemento-core:2.3.2")
    implementation("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    testImplementation("dev.sayaya:gwt-test:2.2.7")
}
tasks {
    gwt {
        gwtVersion = "2.12.2"
        sourceLevel = "auto"
        modules = listOf("dev.sayaya.Rx")
        devMode {
            modules = listOf(
                "dev.sayaya.ObservableGeneratorTest",
                "dev.sayaya.OperatorTest",
                "dev.sayaya.SubjectTest",
                "dev.sayaya.SchedulerTest"
            )
            war = file("src/test/webapp")
        }
        generateJsInteropExports = true
        compiler {
            strict = true
            draftCompile = true
        }
    }
    test {
        useJUnitPlatform()
    }
    jar {
        from(sourceSets.main.get().allSource)
    }
    afterEvaluate {
        named<Jar>("sourcesJar") {
            dependsOn(tasks.compileJava)
        }
    }
}
signing {
    val signingKey = project.findProperty("signing.secretKey") as String? ?: System.getenv("GPG_PRIVATE_KEY")
    val signingPassword = project.findProperty("signing.passphrase") as String? ?: System.getenv("GPG_PASSWORD")
    useInMemoryPgpKeys(signingKey, signingPassword)
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/sayaya1090/maven")
            credentials {
                username = project.findProperty("github_username") as String? ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("github_password") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
    coordinates(group.toString(), "rx", version.toString())

    pom {
        name.set("sayaya-rx")
        description.set("Reactive programming library for GWT")
        url.set("https://github.com/sayaya1090/sayaya-rx")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("sayaya1090")
                name.set("sayaya")
                email.set("sayaya1090@gmail.com")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/sayaya1090/sayaya-rx.git")
            developerConnection.set("scm:git:ssh://github.com/sayaya1090/sayaya-rx.git")
            url.set("https://github.com/sayaya1090/sayaya-rx")
        }
    }
}