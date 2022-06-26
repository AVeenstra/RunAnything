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
import org.jetbrains.annotations.Nls
import com.intellij.icons.AllIcons
import com.intellij.execution.configurations.ConfigurationFactory
import javax.swing.Icon

/**
 * This class represents the configuration type of the plugin.
 */
class RunAnythingConfigurationType : ConfigurationType {
    override fun getDisplayName(): @Nls(capitalization = Nls.Capitalization.Title) String {
        return "Run Command"
    }

    override fun getConfigurationTypeDescription(): @Nls(capitalization = Nls.Capitalization.Sentence) String {
        return "Run any system command."
    }

    override fun getIcon(): Icon {
        return AllIcons.Actions.Run_anything
    }

    override fun getId(): String {
        return "RUN_ANYTHING_CONFIGURATION"
    }

    override fun getConfigurationFactories(): Array<ConfigurationFactory> {
        return arrayOf(RunAnythingConfigurationFactory(this))
    }

    override fun getHelpTopic(): String? {
        return null
    }
}