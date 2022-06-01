interface GreetingPluginExtension {
    val message: Property<String>
}

class GreetingDSLPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Add the 'greeting' extension object
        val extension = project.extensions.create<GreetingPluginExtension>("greeting")
        // Add a task that uses configuration from the extension object
        project.task("helloDsl") {
            group = "gradle-plugin"
            doLast {
                println("hello from ${extension.message.get()}")
            }
        }
    }
}

// Apply the plugin
apply<GreetingDSLPlugin>()

// Configure the extension using a DSL block
configure<GreetingPluginExtension> {
    message.set("dsl")
}
