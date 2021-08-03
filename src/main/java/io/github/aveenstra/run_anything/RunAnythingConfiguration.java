/*
 *    Copyright 2021 A Veenstra
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

package io.github.aveenstra.run_anything;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.execution.util.ProgramParametersUtil;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.util.execution.ParametersListUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class contains the method creating the process in {@link RunAnythingConfiguration#getState()}.
 */
public class RunAnythingConfiguration extends RunConfigurationBase<RunAnythingConfigurationOptions> {
    protected RunAnythingConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @NotNull
    @Override
    protected RunAnythingConfigurationOptions getOptions() {
        return (RunAnythingConfigurationOptions) super.getOptions();
    }

    @Override
    public @NotNull SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new RunAnythingSettingsEditor();
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        getOptions().validate_all();
    }

    @Override
    public @Nullable RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        final var options = getOptions();
        final var project = environment.getProject();
        final var dataContext = environment.getDataContext();
        final var module = dataContext != null ? dataContext.getData(LangDataKeys.MODULE) : null;
        final var inputText = options.getInputText();
        final var inputClose = options.getInputClose();
        var cwd_option = options.getWorkingDirectory();
        final var cwd = !cwd_option.isEmpty() ? ProgramParametersUtil.expandPathAndMacros(cwd_option, module, project) : environment.getProject().getBasePath();

        return new CommandLineState(environment) {
            @NotNull
            @Override
            protected ProcessHandler startProcess() throws ExecutionException {
                GeneralCommandLine commandLine = new GeneralCommandLine(ProgramParametersUtil.expandPathAndMacros(options.getCommand(), module, project))
                        .withParameters(ParametersListUtil.parse(ProgramParametersUtil.expandPathAndMacros(options.getArguments(), module, project)))
                        .withEnvironment(options.getEnvironmentVariables())
                        .withWorkDirectory(cwd)
                        .withParentEnvironmentType(options.getIsPassParentEnvs() ? GeneralCommandLine.ParentEnvironmentType.CONSOLE : GeneralCommandLine.ParentEnvironmentType.NONE);

                final var processHandler = new KillableColoredProcessHandler(commandLine);

                if (options.getInputEnabled())
                    processHandler.addProcessListener(new InputWriterProcessAdapter(this, processHandler, inputText, inputClose));

                ProcessTerminatedListener.attach(processHandler);
                return processHandler;
            }
        };
    }

    private static class InputWriterProcessAdapter extends ProcessAdapter {
        private final CommandLineState parentState;
        private final OSProcessHandler processHandler;
        private final OutputStream writer;
        private final String inputText;
        private final boolean inputClose;

        private InputWriterProcessAdapter(CommandLineState parentState, OSProcessHandler processHandler, String inputText, boolean inputClose) {
            this.parentState = parentState;
            this.processHandler = processHandler;
            this.writer = processHandler.getProcess().getOutputStream();
            this.inputText = inputText;
            this.inputClose = inputClose;
        }

        @Override
        public void startNotified(@NotNull ProcessEvent event) {
            RunContentDescriptor contentDescriptor = RunContentManager.getInstance(parentState.getEnvironment().getProject())
                    .findContentDescriptor(parentState.getEnvironment().getExecutor(), processHandler);

            if (contentDescriptor != null && contentDescriptor.getExecutionConsole() instanceof ConsoleView) {
                ((ConsoleView) contentDescriptor.getExecutionConsole()).print(inputText, ConsoleViewContentType.LOG_INFO_OUTPUT);
            }

            try {
                writer.write(inputText.getBytes());
                writer.flush();
                if (inputClose) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void processTerminated(@NotNull ProcessEvent event) {
            if (!inputClose)
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
