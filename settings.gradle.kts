rootProject.name = "sayaya-rx"
pluginManagement {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/sayaya1090/maven")
            credentials {
                username =
                    if (settings.extra.has("github_username")) settings.extra["github_username"] as String else System.getenv(
                        "GITHUB_USERNAME"
                    )
                password =
                    if (settings.extra.has("github_password")) settings.extra["github_password"] as String else System.getenv(
                        "GITHUB_TOKEN"
                    )
            }
        }
        gradlePluginPortal()
    }
}
