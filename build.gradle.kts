plugins {
    // See https://github.com/JetBrains/kotlin/
    kotlin("jvm") version "1.7.10"

    // See https://github.com/JetBrains/gradle-intellij-plugin/
    id("org.jetbrains.intellij") version "1.9.0"
}

group = "io.github.aveenstra"
val versionOverride = System.getenv()["VERSION"]
version = versionOverride ?: "development"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = JavaVersion.VERSION_17
}

intellij {
    version.set("2020.1")
    updateSinceUntilBuild.set(false)
}

val changeFile = File("CHANGES.md")
val changesLines: ListProperty<String> = project.objects.listProperty()
changesLines.set(changeFile.readLines())
val changesText = changesLines.map { it.joinToString("\n", postfix = "\n") }
val patchPluginDepends = mutableListOf<TaskProvider<Task>>()

if (!versionOverride.isNullOrBlank()) {
    val patchChanges = tasks.register("patchChanges") {
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
    }
    patchPluginDepends.add(patchChanges)
}

tasks.buildSearchableOptions {
    enabled = false
}

tasks.patchPluginXml {
    dependsOn.addAll(patchPluginDepends)
    changeNotes.set(changesText)
    sinceBuild.set("201.3803.71")
}

tasks.publishPlugin {
    token.set(System.getenv()["JETBRAINS_TOKEN"])
}

tasks.runPluginVerifier {
    failureLevel.set(org.jetbrains.intellij.tasks.RunPluginVerifierTask.FailureLevel.ALL)
}
