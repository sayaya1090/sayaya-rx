import org.docstr.gwt.GwtBaseTask

plugins {
    id("java")
    id("org.docstr.gwt") version "2.0.12"
}
repositories {
    mavenCentral()
    mavenLocal()
}
group = "net.sayaya"
version = "2.0"

dependencies {
    implementation(project(":"))
    implementation("org.jboss.elemento:elemento-core:1.6.10")
    implementation("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
}
tasks {
    gwt {
        gwtVersion = "2.12.0"
        gwt.modules = listOf("dev.sayaya.Test")
        sourceLevel = "auto"
        devMode {
            port = 8888
            war = File("src/main/webapp")
        }
    }
    withType<GwtBaseTask> {
        val lombok: File = project.configurations.annotationProcessor.get().filter { it.name.startsWith("lombok") }.single()
        jvmArgs = listOf("-javaagent:${lombok}=ECJ")
    }
}