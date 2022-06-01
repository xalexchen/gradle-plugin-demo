# 创建自定义Gradle Plugin的3中方式



## 0x01. Build Script

脚本的源码直接卸载build.gradle或build.gradle.kts里面

| 优势                                          | 不足                                         |
| --------------------------------------------- | -------------------------------------------- |
| 插件脚本会被自动编译并添加到模块的classpath中 | 无法复用到其它模块，只适合项目独有的简单任务 |

为了更好地关注插件怎么编写，我们使用独立的文件来区分，可以参考工程中的

> app/greeting.gradle.kts （演示了基本的插件）
>
> app/greeting_dsl.gradle.kts （DSL Extenstions）

**greeting.gradle.kts**

```
// app/greeting.gradle.kts

class GreetingPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.task("hello") {
            group = "gradle-plugin"
            doLast {
                println("Hello from the GreetingPlugin")
            }
        }
    }
}

// Apply the plugin
apply<GreetingPlugin>()
```

**greeting_dsl.gradle.kts**

```
// app/greeting_dsl.gradle.kts

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
```



在需要引入的模块build.gradle.kts中引入文件

```
// app/build.gradle.kts

apply(from = "greeting.gradle.kts")
apply(from = "greeting_dsl.gradle.kts")
```

在gradle任务列表中，可以看到新增了两个任务，分别执行查看结果

```
✗ ./gradlew -q app:hello
Hello from the GreetingPlugin

✗ ./gradlew -q app:helloDsl
hello from dsl
```



## 0x02. BuildSrc

源码要求放置在 ${rootProject}/buildSrc/src/main目录下，根据实现语言，建立不同的目录

```
java
groovy
kotlin
```

当然java目录也可以防止kotlin代码，本项目使用kotlin进行实现，首先需要在buildSrc目录下新建build.gradle.kts文件

```
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
```



在需要使用的模块的build.gradle.kts模块中引入

```
// app/build.gradle.kts
plugins {
    id("dev.alex.gradle.plugin.buildsrc")
}
```

同步gradle后，即可在app模块中查看到新的helloBuildSrc的task，执行查看结果

```
✗ ./gradlew -q app:helloBuildSrc
Hello from the build src
```



## 0x03. Standalone

### 构建和发布到本地

1. 新建Java or Kotlin Library，模块名为plugin

2. 在plugin:build.gradle.kts文件中，增加以下插件

```
plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}
```

- java-gradle-plugin：

可以省去添加java-library插件和 gradleApi（如org.gradle.api.Project/Plugin）依赖，因为gradle插件的api是使用java编写的。并且会自动生成resources目录下的META-INF目录和jar的描述信息，省去手动创建的步骤

参考链接：https://docs.gradle.org/current/userguide/java_gradle_plugin.html#java_gradle_plugin



- kotlin-dsl

支持使用kotlin编写插件



- maven-publish

实现将插件推送到本地仓库或远端仓库



3. 添加插件信息

```
gradlePlugin {
    plugins {
        create("simplePlugin") {
            id = "dev.alex.gradle.plugin.standalone"
            implementationClass = "dev.alex.gradle.plugin.standalone.MyClass"
            version = "1.0.0"
        }
    }
}
```



4. 添加发布信息

这里将发布包推送到本地目录

```
publishing {
    repositories {
        mavenLocal {
            name = "localPluginRepository"
            url = uri("../local-plugin-repository")
        }
    }
}
```

5. 实现MyClass.kt

```
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
```

6. 执行publish进行发布

```
 ✗ ./gradlew -q plugin:publish 
```



### 项目引入插件

1. 首先需要在setting.gradle中添加本地仓库的地址

```
pluginManagement {
    repositories {
        maven {
            url = uri("local-plugin-repository")
        }
    }
}
```

2. 在模块的build.gradle.kts中引入模块

```
plugins {
    id("dev.alex.gradle.plugin.standalone") version "1.0.0"
}
```

3. 同步gradle后，即可在app模块中看到helloBuildStandalone的任务了

4. 执行查看结果

   ```
   ./gradlew -q app:helloBuildStandalone
   Hello from the build standalone!!
   ```

   