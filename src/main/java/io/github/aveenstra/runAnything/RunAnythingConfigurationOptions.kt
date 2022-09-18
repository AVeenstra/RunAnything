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
import com.intellij.util.xmlb.annotations.OptionTag
import com.jetbrains.rd.util.put

class RunAnythingConfigurationOptions : RunConfigurationOptions() {

    @get:OptionTag
    var command by string("")

    @get:OptionTag
    var arguments by string("")

    @get:OptionTag
    var workingDirectory by string("")

    @get:OptionTag
    var environmentVariables by map<String, String>()

    @get:OptionTag
    var isPassParentEnvs by property(true)

    @get:OptionTag
    var inputEnabled by property(false)

    @get:OptionTag
    var inputText by string("")

    @get:OptionTag
    var inputClose by property(true)

    @Throws(ConfigurationException::class)
    fun validateCommand(command: String?) {
        if (command.isNullOrBlank()) {
            throw ConfigurationException("No command given")
        }
    }

    @set:Throws(ConfigurationException::class)
    var environmentVariablesData: EnvironmentVariablesData
        get() = EnvironmentVariablesData.create(environmentVariables, isPassParentEnvs)
        set(environmentVariablesData) {
            environmentVariables = validateEnvironmentVariables(environmentVariablesData.envs)
            isPassParentEnvs = environmentVariablesData.isPassParentEnvs
        }

    @Throws(ConfigurationException::class)
    fun validateEnvironmentVariables(newEnvironment: Map<String, String>): MutableMap<String, String> {
        val result = HashMap<String, String>(newEnvironment.size)
        for (entry in newEnvironment.entries) {
            if (entry.key.isBlank()) {
                throw ConfigurationException("Empty environment keys are not allowed")
            }
            result.put(entry)
        }
        return result
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