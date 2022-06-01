plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

gradlePlugin {
    plugins {
        create("simplePlugin") {
            id = "dev.alex.gradle.plugin.standalone"
            implementationClass = "dev.alex.gradle.plugin.standalone.MyClass"
            version = "1.0.0"
        }
    }
}

publishing {
    repositories {
        mavenLocal {
            name = "localPluginRepository"
            url = uri("../local-plugin-repository")
        }
    }
}