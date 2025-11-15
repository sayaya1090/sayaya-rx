plugins {
    kotlin("jvm") version "2.2.21"
    id("dev.sayaya.gwt") version "2.2.7"
    id("maven-publish")
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
version = "2.1.4"

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
    publications {
        register("maven", MavenPublication::class) {
            groupId = project.group.toString()
            artifactId = "rx"
            version = project.version.toString()
            from(project.components["java"])
        }
    }
}
