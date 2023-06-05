plugins {
    id("java")
    id("org.wisepersist.gwt") version "1.1.19"
    id("war")
    id("gwt")
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
    implementation(project(":"))
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
        gwt.modules = listOf("net.sayaya.Test")
    }
    val lombok: File = project.configurations.annotationProcessor.get().filter { it.name.startsWith("lombok") }.single()
    compileGwt {
        extraJvmArgs = listOf("-XX:ReservedCodeCacheSize=512M", "-javaagent:${lombok}=ECJ")
    }
    gwtDev {
        minHeapSize = "4096M"
        maxHeapSize = "4096M"
        sourceLevel = "auto"
        extraJvmArgs = listOf("-XX:ReservedCodeCacheSize=512M", "-javaagent:${lombok}=ECJ")
        port = 8888
        war = File("src/main/webapp")
    }
    withType<War> {
        duplicatesStrategy = DuplicatesStrategy.WARN
    }
}
