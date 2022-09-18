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

import com.intellij.execution.configurations.CommandLineState
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.RunContentManager
import java.io.IOException
import java.io.OutputStream

/**
 * This [ProcessAdapter] will handle the output to the process started by this run configuration.
 */
internal class RunAnythingProcessAdapter(
    private val parentState: CommandLineState,
    private val processHandler: OSProcessHandler,
    private val inputText: String,
    private val inputClose: Boolean,
) : ProcessAdapter() {
    private val writer: OutputStream = processHandler.process.outputStream

    override fun startNotified(event: ProcessEvent) {
        val executionConsole = RunContentManager
            .getInstance(parentState.environment.project)
            .findContentDescriptor(parentState.environment.executor, processHandler)
            ?.executionConsole
        if (executionConsole is ConsoleView) {
            executionConsole.print(inputText, ConsoleViewContentType.LOG_INFO_OUTPUT)
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
