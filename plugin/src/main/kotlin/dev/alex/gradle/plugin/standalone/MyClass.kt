package dev.alex.gradle.plugin.standalone

import org.gradle.api.Plugin
import org.gradle.api.Project

class MyClass : Plugin<Project> {
    override fun apply(project: Project) {
        project.task("helloBuildStandalone") {
            group = "gradle-plugin"
            doLast {
                println("Hello from the build standalone!!")
            }
        }
    }
}