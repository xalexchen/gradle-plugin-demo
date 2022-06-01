import org.gradle.api.Plugin
import org.gradle.api.Project

class GreetingPlugin3 : Plugin<Project> {
    override fun apply(project: Project) {
        project.task("hello_from_buildSrc") {
            group = "buildSrc"
            doLast {
                println("Hello from the build src")
            }
        }
    }
}