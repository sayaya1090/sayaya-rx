plugins {
    id("java")
    id("org.docstr.gwt") version "2.2.1"
    id("maven-publish")
}
repositories {
    mavenCentral()
    mavenLocal()
}
group = "dev.sayaya"
version = "2.1.1"

dependencies {
    implementation("org.jboss.elemento:elemento-core:1.7.0")
    implementation("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
    testImplementation("junit:junit:4.13.2")
}
gwt {
    modules = listOf("dev.sayaya.Rx")
    gwtVersion = "2.12.2"
    sourceLevel = "auto"
    compiler.strict = true
    compiler.draftCompile = true
    gwtTest.sourceLevel = "auto"
}
tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    jar {
        from(sourceSets.main.get().allSource)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    test {
        useJUnit()
    }
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
            version = "2.1.1"
            from(project.components["java"])
        }
    }
}
