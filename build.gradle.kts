// See https://github.com/JetBrains/gradle-intellij-plugin/
plugins {
    kotlin("jvm") version "1.7.0"
    id("org.jetbrains.intellij") version "1.6.0"
}

group = "io.github.aveenstra"
val versionOverride = System.getenv()["VERSION"]
version = versionOverride ?: "development"

repositories {
    mavenCentral()
}

intellij {
    version.set("LATEST-EAP-SNAPSHOT")
    updateSinceUntilBuild.set(false)
}



val changeFile = File("CHANGES.md")
val changesLines: ListProperty<String> = project.objects.listProperty()
changesLines.set(changeFile.readLines())
val changesText = changesLines.map { it.joinToString("\n", postfix = "\n") }
val patchPluginDepends = mutableListOf<TaskProvider<Task>>()

if (!versionOverride.isNullOrEmpty()) {
    patchPluginDepends.add(tasks.register("patchChanges") {
        val changeNotes = System.getenv()["CHANGE_NOTES"]

        inputs.property("VERSION", versionOverride)
        inputs.property("CHANGE_NOTES", changeNotes)
        inputs.file(changeFile)

        outputs.file(changeFile)

        doLast {
            val versionText = "<dt>${versionOverride}</dt>"
            val changesTextValue = changesLines.get().toMutableList()

            if (changesTextValue.contains(versionText)) {
                throw GradleException("The given version should not be used already")
            }

            changesTextValue.add(1, versionText)
            changesTextValue.add(2, "<dd>${changeNotes}</dd>")

            changesLines.set(changesTextValue)

            changeFile.writeText(changesText.get())
        }
    })
}

tasks.patchPluginXml {
    dependsOn.addAll(patchPluginDepends)
    changeNotes.set(changesText)
    sinceBuild.set("201.3803.71")
}

tasks.listProductsReleases {
    doLast {
        val output = outputFile.get().asFile.readLines()
        outputFile.get().asFile.writeText("${output.first()}\n${output.last()}\n")
    }
}

tasks.publishPlugin {
    token.set(System.getenv()["JETBRAINS_TOKEN"])
}

tasks.runPluginVerifier {
    failureLevel.set(org.jetbrains.intellij.tasks.RunPluginVerifierTask.FailureLevel.ALL)
}
