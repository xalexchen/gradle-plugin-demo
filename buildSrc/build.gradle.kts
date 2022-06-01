plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("GreetingPlugin3") {
            id = "dev.alex.gradle.plugin"
            implementationClass = "GreetingPlugin3"
            version = "1.0.0"
        }
    }
}
