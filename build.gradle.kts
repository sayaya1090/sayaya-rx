plugins {
    id("java")
    // id("org.docstr.gwt") version "2.1.0"
    id("org.docstr.gwt") version "1.1.31"
    id("maven-publish")
}
repositories {
    mavenCentral()
    mavenLocal()
}
group = "dev.sayaya"
version = "2.0"

dependencies {
    implementation("org.jboss.elemento:elemento-core:1.6.10")
    implementation("org.gwtproject:gwt-user:2.12.0")
    implementation("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
    testImplementation("org.gwtproject:gwt-dev:2.12.0")
    testImplementation("junit:junit:4.13.2")
}
gwt {
    sourceLevel = "auto"
}
tasks {
    jar {
        from(sourceSets.main.get().allSource)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    test {
        useJUnit()
    }

    publishing {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/sayaya1090/maven")
                credentials {
                    username = project.findProperty("github_username") as String
                    password = project.findProperty("github_password") as String
                }
            }
        }
        publications {
            register("maven", MavenPublication::class) {
                groupId = "dev.sayaya"
                artifactId = "rx"
                version = "2.0"
                from(project.components["java"])
            }
        }
    }
}
