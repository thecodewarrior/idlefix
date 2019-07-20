import java.net.URI

pluginManagement {
    repositories {
        jcenter()
        maven {
            name = "Fabric"
            url = URI.create("https://maven.fabricmc.net/")
        }
        gradlePluginPortal()
    }
}

rootProject.name = "idlefix"
