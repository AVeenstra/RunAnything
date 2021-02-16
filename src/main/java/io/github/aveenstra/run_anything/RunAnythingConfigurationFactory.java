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

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RunAnythingConfigurationFactory extends ConfigurationFactory {
    private static final String FACTORY_ID = "RunAnythingFactory";
    private static final String FACTORY_NAME = "Run anything configuration factory";

    protected RunAnythingConfigurationFactory(@NotNull ConfigurationType type) {
        super(type);
    }

    @Override
    public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new RunAnythingConfiguration(project, this, "Run command");
    }

    @Override
    public @NotNull String getName() {
        return FACTORY_NAME;
    }

    @Override
    public @NotNull String getId() {
        return FACTORY_ID;
    }

    @Override
    public @Nullable Class<? extends BaseState> getOptionsClass() {
        return RunAnythingConfigurationOptions.class;
    }
}
