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

import com.intellij.execution.Executor
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.execution.configurations.RunConfigurationBase
import com.intellij.execution.configurations.RuntimeConfigurationException
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.project.Project

/**
 * This class contains the method creating the process in [RunAnythingConfiguration.getState].
 */
class RunAnythingConfiguration(project: Project?, factory: ConfigurationFactory?, name: String?) :
    RunConfigurationBase<RunAnythingConfigurationOptions?>(project!!, factory, name) {

    fun getRunAnythingOptions() = super.getOptions() as RunAnythingConfigurationOptions

    override fun getConfigurationEditor() = RunAnythingSettingsEditor()

    @Throws(RuntimeConfigurationException::class)
    override fun checkConfiguration() = getRunAnythingOptions().validateAll()

    override fun getState(executor: Executor, environment: ExecutionEnvironment) =
        RunAnythingCommandLineState(environment, getRunAnythingOptions())
}