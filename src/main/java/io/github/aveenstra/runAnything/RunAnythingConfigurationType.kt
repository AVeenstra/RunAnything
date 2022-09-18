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

import com.intellij.execution.configurations.ConfigurationType
import com.intellij.icons.AllIcons

/**
 * This class represents the configuration type of the plugin.
 */
class RunAnythingConfigurationType : ConfigurationType {
    override fun getDisplayName() = "Run Command"

    override fun getConfigurationTypeDescription() = "Run any system command."

    override fun getIcon() = AllIcons.Actions.Run_anything

    override fun getId() = "RUN_ANYTHING_CONFIGURATION"

    override fun getConfigurationFactories() = arrayOf(RunAnythingConfigurationFactory(this))

    override fun getHelpTopic(): Nothing? = null
}