package io.github.aveenstra.runAnything

import com.intellij.execution.CommonProgramRunConfigurationParameters
import com.intellij.openapi.project.Project

class CommonProgramOptions(
    private val myProject: Project,
    private val options: RunAnythingConfigurationOptions,
) : CommonProgramRunConfigurationParameters {

    override fun getProject() = myProject

    override fun getProgramParameters() = options.arguments
    override fun getWorkingDirectory() = options.workingDirectory
    override fun getEnvs() = options.environmentVariables
    override fun isPassParentEnvs() = options.isPassParentEnvs

    override fun setProgramParameters(value: String?) = throw IllegalStateException()
    override fun setWorkingDirectory(value: String?) = throw IllegalStateException()
    override fun setEnvs(envs: MutableMap<String, String>) = throw IllegalStateException()
    override fun setPassParentEnvs(passParentEnvs: Boolean) = throw IllegalStateException()
}