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

import com.intellij.execution.configuration.EnvironmentVariablesData
import com.intellij.execution.configurations.RunConfigurationOptions
import com.intellij.execution.configurations.RuntimeConfigurationException
import com.intellij.openapi.options.ConfigurationException
import com.intellij.util.xmlb.annotations.Attribute

class RunAnythingConfigurationOptions : RunConfigurationOptions() {

    @get:Attribute
    internal var command by string("")

    @get:Attribute
    internal var arguments by string("")

    @get:Attribute
    internal var workingDirectory by string("")

    @get:Attribute
    internal var environmentVariables by map<String, String>()

    @get:Attribute
    internal var isPassParentEnvs by property(true)

    @get:Attribute
    internal var inputEnabled by property(false)

    @get:Attribute
    internal var inputText by string("")

    @get:Attribute
    internal var inputClose by property(true)

    @Throws(ConfigurationException::class)
    fun validateCommand(command: String?) {
        if (command == null || command.isEmpty()) {
            throw ConfigurationException("No command given")
        }
    }

    @set:Throws(ConfigurationException::class)
    var environmentVariablesData: EnvironmentVariablesData
        get() {
            return EnvironmentVariablesData.create(
                environmentVariables,
                isPassParentEnvs
            )
        }
        set(environmentVariablesData) {
            val newEnvironmentVariables = environmentVariablesData.envs
            validateEnvironmentVariables(newEnvironmentVariables)
            environmentVariables = newEnvironmentVariables
            isPassParentEnvs = environmentVariablesData.isPassParentEnvs
        }

    @Throws(ConfigurationException::class)
    fun validateEnvironmentVariables(newEnvironment: Map<String, String>) {
        for (key in newEnvironment.keys) {
            if (key.isEmpty()) throw ConfigurationException("Empty environment keys are not allowed")
        }
    }

    @Throws(RuntimeConfigurationException::class)
    fun validateAll() {
        try {
            validateCommand(command)
            validateEnvironmentVariables(environmentVariables)
        } catch (e: ConfigurationException) {
            val newE = RuntimeConfigurationException(e.message, e.title)
            newE.originator = e.originator
            newE.stackTrace = e.stackTrace
            throw newE
        }
    }
}