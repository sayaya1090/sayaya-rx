import org.wisepersist.gradle.plugins.gwt.GwtDev

val isTest = project.gradle.startParameter.taskNames.contains("gwtDev")
plugins {
    id("java")
    id("org.wisepersist.gwt") version "1.1.19"
    id("maven-publish")
}
if(isTest) {
    apply(plugin="war")
    apply(plugin="gwt")
} else {
    apply(plugin="gwt-base")
}
repositories {
    mavenCentral()
    mavenLocal()
}
group = "net.sayaya"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

dependencies {
    implementation("org.jboss.elemento:elemento-core:1.0.11")
    implementation("org.gwtproject:gwt-user:2.10.0")
    compileOnly("org.gwtproject:gwt-dev:2.10.0")
    implementation("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
}
tasks {
    withType<Delete> { doFirst { delete("build/") } }
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    gwt {
        minHeapSize = "1024M"
        maxHeapSize = "2048M"
        sourceLevel = "auto"
        if (isTest) {
            sourceSets.main.get().java.apply {
                srcDirs.addAll(srcDir("src/test/java"))
            }
            gwt.modules = listOf("net.sayaya.Test")
        }
    }
    val lombok: File = project.configurations.annotationProcessor.get().filter { it.name.startsWith("lombok") }.single()
    compileGwt {
        extraJvmArgs = listOf("-XX:ReservedCodeCacheSize=512M", "-javaagent:${lombok}=ECJ")
    }
    if (isTest) {
        getByName<GwtDev>("gwtDev") {
            minHeapSize = "4096M"
            maxHeapSize = "4096M"
            sourceLevel = "auto"
            extraJvmArgs = listOf("-XX:ReservedCodeCacheSize=512M", "-javaagent:${lombok}=ECJ")
            port = 8888
            war = File("src/test/webapp")
        }
    } else {
        jar {
            from(sourceSets.main.get().allSource)
        }
        publishing {
            publications {
                register("maven", MavenPublication::class) {
                    groupId = "net.sayaya"
                    artifactId = "rx"
                    version = "1.0"
                    from(project.components["java"])
                }
            }
        }
    }
}
