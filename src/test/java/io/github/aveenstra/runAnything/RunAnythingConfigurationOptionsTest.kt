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

import com.intellij.execution.RunManager
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl
import com.intellij.openapi.util.JDOMUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jdom.Element
import org.jdom.output.XMLOutputter
import org.junit.Assert
import org.junit.Test

internal class RunAnythingConfigurationOptionsTest : BasePlatformTestCase() {

    private lateinit var settings: RunnerAndConfigurationSettingsImpl

    override fun setUp() {
        super.setUp()

        val factory = RunAnythingConfigurationFactory(RunAnythingConfigurationType())
        val runConfiguration = factory.createTemplateConfiguration(project)
        runConfiguration.name = "name"

        settings = RunManager.getInstance(project)
            .createConfiguration(runConfiguration, factory) as RunnerAndConfigurationSettingsImpl
    }

    @Test
    fun testLoadOldConfig() {
        settings.readExternal(getXmlConfiguration(), true)

        val options = getOptions()
        Assert.assertEquals("command", options.command)
        Assert.assertEquals("arguments", options.arguments)
        Assert.assertTrue(options.inputEnabled)
        Assert.assertEquals("input", options.inputText)
        Assert.assertEquals("working directory", options.workingDirectory)
    }

    @Test
    fun testStoreConfig() {
        val options = getOptions()
        options.command = "command"
        options.arguments = "arguments"
        options.inputEnabled = true
        options.inputText = "input"
        options.workingDirectory = "working directory"

        val element = Element("configuration")
        element.setAttribute("default", "false")

        settings.writeExternal(element)

        val serializer = XMLOutputter()
        val storedXml = serializer.outputString(element)
        val loadedXml = serializer.outputString(getXmlConfiguration())

        Assert.assertEquals(loadedXml, storedXml)
    }

    private fun getOptions() = (settings.configuration as RunAnythingConfiguration).getRunAnythingOptions()

    private fun getXmlConfiguration(): Element {
        return JDOMUtil.load(this.javaClass.getResource("runConfiguration_1.8.xml")!!).getChild("configuration")
    }
}
