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
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.configurations.GeneralCommandLine.ParentEnvironmentType
import com.intellij.execution.process.*
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.RunContentManager
import com.intellij.execution.util.ProgramParametersUtil
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.util.execution.ParametersListUtil
import java.io.IOException
import java.io.OutputStream

/**
 * This class contains the method creating the process in [RunAnythingConfiguration.getState].
 */
class RunAnythingConfiguration(project: Project?, factory: ConfigurationFactory?, name: String?) :
    RunConfigurationBase<RunAnythingConfigurationOptions?>(project!!, factory, name) {

    public override fun getOptions(): RunAnythingConfigurationOptions {
        return super.getOptions() as RunAnythingConfigurationOptions
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration?> {
        return RunAnythingSettingsEditor()
    }

    @Throws(RuntimeConfigurationException::class)
    override fun checkConfiguration() {
        options.validateAll()
    }

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        val options = options
        val project = environment.project
        val module = environment.dataContext?.getData(LangDataKeys.MODULE)

        val cwd = if (!options.workingDirectory.isNullOrEmpty()) {
            ProgramParametersUtil.expandPathAndMacros(options.workingDirectory, module, project)
        } else {
            environment.project.basePath!!
        }

        return object : CommandLineState(environment) {
            @Throws(ExecutionException::class)
            override fun startProcess(): ProcessHandler {
                val command = ProgramParametersUtil.expandPathAndMacros(options.command, module, project)
                val parametersText = ProgramParametersUtil.expandPathAndMacros(options.arguments, module, project)
                val parameters = ParametersListUtil.parse(parametersText)

                val parentEnvironment = if (options.isPassParentEnvs) {
                    ParentEnvironmentType.CONSOLE
                } else {
                    ParentEnvironmentType.NONE
                }

                val commandLine = GeneralCommandLine(command)
                    .withParameters(parameters)
                    .withWorkDirectory(cwd)
                    .withEnvironment(options.environmentVariables)
                    .withParentEnvironmentType(parentEnvironment)

                val processHandler = KillableColoredProcessHandler(commandLine)

                if (options.inputEnabled) {
                    val inputText = options.inputText ?: ""
                    val inputClose = options.inputClose

                    val inputWriter = InputWriterProcessAdapter(this, processHandler, inputText, inputClose)
                    processHandler.addProcessListener(inputWriter)
                }

                ProcessTerminatedListener.attach(processHandler)
                return processHandler
            }
        }
    }

    private class InputWriterProcessAdapter(
        private val parentState: CommandLineState,
        private val processHandler: OSProcessHandler,
        private val inputText: String,
        private val inputClose: Boolean,
    ) : ProcessAdapter() {
        private val writer: OutputStream = processHandler.process.outputStream

        override fun startNotified(event: ProcessEvent) {
            val contentDescriptor = RunContentManager.getInstance(parentState.environment.project)
                .findContentDescriptor(parentState.environment.executor, processHandler)
            if (contentDescriptor != null && contentDescriptor.executionConsole is ConsoleView) {
                (contentDescriptor.executionConsole as ConsoleView).print(
                    inputText,
                    ConsoleViewContentType.LOG_INFO_OUTPUT
                )
            }
            try {
                writer.write(inputText.toByteArray())
                writer.flush()
                if (inputClose) {
                    writer.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun processTerminated(event: ProcessEvent) {
            if (!inputClose) try {
                writer.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}