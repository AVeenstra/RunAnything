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

import com.intellij.execution.configuration.EnvironmentVariablesData;
import com.intellij.execution.configurations.RunConfigurationOptions;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.openapi.components.StoredProperty;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class RunAnythingConfigurationOptions extends RunConfigurationOptions {
    private final StoredProperty<String> command = string("").provideDelegate(this, "command");
    private final StoredProperty<String> arguments = string("").provideDelegate(this, "arguments");
    private final StoredProperty<String> workingDirectory = string("").provideDelegate(this, "workingDirectory");
    private final StoredProperty<Map<String, String>> environmentVariables = map();
    private final StoredProperty<Boolean> isPassParentEnvs = property(true).provideDelegate(this, "isPassParentEnvs");
    private final StoredProperty<Boolean> inputEnabled = property(false).provideDelegate(this, "inputEnabled");
    private final StoredProperty<String> inputText = string("").provideDelegate(this, "inputText");
    private final StoredProperty<Boolean> inputClose = property(true).provideDelegate(this, "inputClose");

    public RunAnythingConfigurationOptions() {
        environmentVariables.setName("environmentVariables");
    }

    public @NotNull String getCommand() {
        var result = command.getValue(this);
        return result == null ? "" : result;
    }

    public void validateCommand(@NotNull String command) throws ConfigurationException {
        if (command.isEmpty()) throw new ConfigurationException("No command given");
    }

    public void setCommand(@NotNull String newCommand) throws ConfigurationException {
        validateCommand(newCommand);
        command.setValue(this, newCommand);
    }

    public @NotNull String getArguments() {
        var result = arguments.getValue(this);
        return result == null ? "" : result;
    }

    public void setArguments(@NotNull String newArguments) {
        arguments.setValue(this, newArguments);
    }

    public @NotNull EnvironmentVariablesData getEnvironmentVariablesData() {
        var env = environmentVariables.getValue(this);
        if (env == null) return EnvironmentVariablesData.DEFAULT;
        else return EnvironmentVariablesData.create(env, isPassParentEnvs.getValue(this));
    }

    public void validateEnvironmentVariables(@NotNull Map<String, String> newEnvironment) throws ConfigurationException {
        for (var key : newEnvironment.keySet()) {
            if (key.isEmpty()) throw new ConfigurationException("Empty environment keys are not allowed");
        }
    }

    public void setEnvironmentVariablesData(@NotNull EnvironmentVariablesData environmentVariablesData) throws ConfigurationException {
        var newEnvironmentVariables = environmentVariablesData.getEnvs();
        validateEnvironmentVariables(newEnvironmentVariables);
        environmentVariables.setValue(this, newEnvironmentVariables);
        isPassParentEnvs.setValue(this, environmentVariablesData.isPassParentEnvs());
    }

    public @NotNull Map<String, String> getEnvironmentVariables() {
        var result = environmentVariables.getValue(this);
        return result == null ? new HashMap<>() : result;
    }

    @SuppressWarnings("unused")
    public void setEnvironmentVariables(@NotNull Map<String, String> newEnvironment) throws ConfigurationException {
        validateEnvironmentVariables(newEnvironment);
        environmentVariables.setValue(this, newEnvironment);
    }

    public boolean getIsPassParentEnvs() {
        return isPassParentEnvs.getValue(this);
    }

    @SuppressWarnings("unused")
    public void setIsPassParentEnvs(boolean newIsPassParentEnvs) {
        isPassParentEnvs.setValue(this, newIsPassParentEnvs);
    }

    public @NotNull String getWorkingDirectory() {
        var result = workingDirectory.getValue(this);
        return result == null ? "" : result;
    }

    public void setWorkingDirectory(@NotNull String newWorkingDirectory) {
        workingDirectory.setValue(this, newWorkingDirectory);
    }

    public boolean getInputEnabled() {
        return inputEnabled.getValue(this);
    }

    public void setInputEnabled(boolean enabled) {
        inputEnabled.setValue(this, enabled);
    }

    public @NotNull String getInputText() {
        var result = inputText.getValue(this);
        return result == null ? "" : result;
    }

    public void setInputText(@NotNull String newInputText) {
        inputText.setValue(this, newInputText);
    }

    public boolean getInputClose() {
        return inputClose.getValue(this);
    }

    public void setInputClose(boolean close) {
        inputClose.setValue(this, close);
    }

    public void validate_all() throws RuntimeConfigurationException {
        try {
            validateCommand(getCommand());
            validateEnvironmentVariables(getEnvironmentVariables());
        } catch (ConfigurationException e) {
            var new_e = new RuntimeConfigurationException(e.getMessage(), e.getTitle());
            new_e.setOriginator(e.getOriginator());
            new_e.setStackTrace(e.getStackTrace());
            throw new_e;
        }
    }
}
