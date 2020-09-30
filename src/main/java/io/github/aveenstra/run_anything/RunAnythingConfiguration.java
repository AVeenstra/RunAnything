/*
 *    Copyright 2020 A Veenstra
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
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessHandlerFactory;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.util.ProgramParametersUtil;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.util.execution.ParametersListUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        return new CommandLineState(environment) {
            @NotNull
            @Override
            protected ProcessHandler startProcess() throws ExecutionException {
                var options = getOptions();
                var project = environment.getProject();
                var dataContext = environment.getDataContext();
                var module = dataContext != null ? dataContext.getData(LangDataKeys.MODULE) : null;

                var cwd = options.getWorkingDirectory();
                if (!cwd.isEmpty()) cwd = ProgramParametersUtil.expandPathAndMacros(cwd, module, project);
                else cwd = environment.getProject().getBasePath();

                GeneralCommandLine commandLine = new GeneralCommandLine(ProgramParametersUtil.expandPathAndMacros(options.getCommand(), module, project))
                        .withParameters(ParametersListUtil.parse(ProgramParametersUtil.expandPathAndMacros(options.getArguments(), module, project)))
                        .withEnvironment(options.getEnvironmentVariables())
                        .withWorkDirectory(cwd)
                        .withParentEnvironmentType(options.getIsPassParentEnvs() ? GeneralCommandLine.ParentEnvironmentType.SYSTEM : GeneralCommandLine.ParentEnvironmentType.NONE);

                OSProcessHandler processHandler = ProcessHandlerFactory.getInstance().createColoredProcessHandler(commandLine);
                ProcessTerminatedListener.attach(processHandler);
                return processHandler;
            }
        };
    }
}
