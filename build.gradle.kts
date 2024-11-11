plugins {
    id("java")
    id("org.docstr.gwt") version "2.0.12"
    id("maven-publish")
}
repositories {
    mavenCentral()
    mavenLocal()
}
group = "net.sayaya"
version = "2.0"

dependencies {
    implementation("org.jboss.elemento:elemento-core:1.6.10")
    implementation("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
}
tasks {
    gwt {
        sourceLevel = "auto"
    }
    jar {
        from(sourceSets.main.get().allSource)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
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
                groupId = "net.sayaya"
                artifactId = "rx"
                version = "2.0"
                from(project.components["java"])
            }
        }
    }
}
