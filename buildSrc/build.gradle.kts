plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("GreetingPluginBuildSrc") {
            id = "dev.alex.gradle.plugin.buildsrc"
            implementationClass = "GreetingPlugin"
            version = "1.0.0"
        }
    }
}
