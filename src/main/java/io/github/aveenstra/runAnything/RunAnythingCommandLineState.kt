/*
 *    Copyright 2022 A Veenstra
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package io.github.aveenstra.runAnything

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.KillableColoredProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.util.ProgramParametersConfigurator
import com.intellij.execution.util.ProgramParametersUtil
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.util.execution.ParametersListUtil
import java.util.*

class RunAnythingCommandLineState(
    environment: ExecutionEnvironment,
    private val options: RunAnythingConfigurationOptions,
) : CommandLineState(environment) {

    private val project = environment.project
    private val module = environment.dataContext?.getData(LangDataKeys.MODULE)

    fun getExpandedCommand() = ProgramParametersUtil.expandPathAndMacros(options.command, module, project)!!

    fun getExpandedParameters(): List<String> {
        return if (!options.arguments.isNullOrBlank()) {
            val parametersText = ProgramParametersUtil.expandPathAndMacros(options.arguments, module, project)
            ParametersListUtil.parse(parametersText)
        } else {
            Collections.emptyList()
        }
    }

    private fun getCwd() =
        ProgramParametersConfigurator().getWorkingDir(CommonProgramOptions(project, options), project, module)

    private fun getParentEnvironmentType(): GeneralCommandLine.ParentEnvironmentType {
        return if (options.isPassParentEnvs) {
            GeneralCommandLine.ParentEnvironmentType.CONSOLE
        } else {
            GeneralCommandLine.ParentEnvironmentType.NONE
        }
    }

    @Throws(ExecutionException::class)
    override fun startProcess(): ProcessHandler {
        val commandLine = GeneralCommandLine(getExpandedCommand())
            .withParameters(getExpandedParameters())
            .withWorkDirectory(getCwd())
            .withEnvironment(options.environmentVariables)
            .withParentEnvironmentType(getParentEnvironmentType())

        val processHandler = KillableColoredProcessHandler(commandLine)

        if (options.inputEnabled) {
            val inputText = options.inputText ?: ""
            val inputClose = options.inputClose

            val inputWriter = RunAnythingProcessAdapter(this, processHandler, inputText, inputClose)
            processHandler.addProcessListener(inputWriter)
        }

        ProcessTerminatedListener.attach(processHandler)
        return processHandler
    }
}
