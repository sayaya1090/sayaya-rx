plugins {
    kotlin("jvm") version "2.4.21"
    id("dev.sayaya.gwt") version "2.4.0"
    signing
    id("maven-publish")
    id("com.vanniktech.maven.publish") version "0.37.0"
}
kotlin {
    jvmToolchain(21)
}
repositories {
    mavenCentral()
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
version = "3.0.0"

dependencies {
    implementation("org.jboss.elemento:elemento-core:2.5.14")
    testImplementation("dev.sayaya:gwt-test:2.4.0")
}
tasks {
    gwt {
        gwtVersion = "2.13.1"
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
    javadoc {
        // GWT용 의존성 jar(jsinterop base 등)는 .java 소스를 함께 담고 있다. -sourcepath가 없으면 javadoc이
        // classpath의 그 소스까지 파싱하다가 소스 전용 의존성(errorprone 어노테이션)을 찾지 못해 실패하므로
        // 프로젝트 소스만 소스 경로로 지정한다.
        (options as StandardJavadocDocletOptions).addStringOption(
            "sourcepath", sourceSets.main.get().java.srcDirs.joinToString(File.pathSeparator)
        )
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