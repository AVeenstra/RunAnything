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

import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.ide.macro.MacroManager
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.testFramework.MapDataContext
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class RunAnythingCommandLineStateTest : BasePlatformTestCase() {

    private lateinit var environment: ExecutionEnvironment
    private lateinit var options: RunAnythingConfigurationOptions
    private lateinit var state: RunAnythingCommandLineState

    override fun setUp() {
        super.setUp()

        val dataContext = MapDataContext()
        dataContext.put(LangDataKeys.MODULE, module)
        dataContext.put(LangDataKeys.PROJECT, project)

        environment = mock {
            on { this.project } doReturn project
            on { this.dataContext } doReturn dataContext
        }

        options = RunAnythingConfigurationOptions()
        state = RunAnythingCommandLineState(environment, options)

        MacroManager.getInstance().cacheMacrosPreview(dataContext)
    }

    @Test
    fun testCommand() {
        options.command = "command"
        Assert.assertEquals("command", state.getExpandedCommand())

        options.command = "\$ProjectName\$"
        Assert.assertEquals(project.name, state.getExpandedCommand())
    }

    @Test
    fun testParameters() {
        Assert.assertEquals(emptyList<String>(), state.getExpandedParameters())

        options.arguments = "1 2 3"
        Assert.assertEquals(listOf("1", "2", "3"), state.getExpandedParameters())

        options.arguments = "\$ProjectName\$"
        Assert.assertEquals(listOf(project.name), state.getExpandedParameters())
    }
}
