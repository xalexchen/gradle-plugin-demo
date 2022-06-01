import org.gradle.api.Plugin
import org.gradle.api.Project

class GreetingPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.task("helloBuildSrc") {
            group = "gradle-plugin"
            doLast {
                println("Hello from the build src")
            }
        }
    }
}