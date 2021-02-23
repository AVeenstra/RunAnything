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

import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.ide.macro.MacrosDialog;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.ui.components.fields.ExtendableTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class RunAnythingSettingsEditor extends SettingsEditor<RunAnythingConfiguration> {
    private JPanel topPanel;
    private TextFieldWithBrowseButton commandField;
    private ExpandableTextField argumentsField;
    private EnvironmentVariablesComponent environmentField;
    private TextFieldWithBrowseButton workingDirectoryField;

    @Override
    protected void resetEditorFrom(@NotNull RunAnythingConfiguration s) {
        var options = s.getOptions();
        commandField.setText(options.getCommand());
        argumentsField.setText(options.getArguments());
        environmentField.setEnvData(options.getEnvironmentVariablesData());
        workingDirectoryField.setText(options.getWorkingDirectory());
    }

    @Override
    protected void applyEditorTo(@NotNull RunAnythingConfiguration s) throws ConfigurationException {
        var options = s.getOptions();
        options.setCommand(commandField.getText());
        options.setArguments(argumentsField.getText());
        options.setEnvironmentVariablesData(environmentField.getEnvData());
        options.setWorkingDirectory(workingDirectoryField.getText());
    }

    @Override
    protected @NotNull JComponent createEditor() {
        return topPanel;
    }

    private void createUIComponents() {
        var command_field_filter = new FileChooserDescriptor(true, false, false, true, false, false);
        commandField = new TextFieldWithBrowseButton();
        commandField.addBrowseFolderListener("Select a Program", "", null, command_field_filter);
        MacrosDialog.addMacroSupport((ExtendableTextField) commandField.getTextField(), MacrosDialog.Filters.ALL, () -> false);

        argumentsField = new ExpandableTextField();
        MacrosDialog.addMacroSupport(argumentsField, MacrosDialog.Filters.ALL, () -> false);

        var working_directory_field_filter = new FileChooserDescriptor(false, true, false, false, false, false);
        workingDirectoryField = new TextFieldWithBrowseButton();
        workingDirectoryField.addBrowseFolderListener("Select a Directory", "", null, working_directory_field_filter);
        MacrosDialog.addMacroSupport((ExtendableTextField) workingDirectoryField.getTextField(), MacrosDialog.Filters.ALL, () -> false);
    }
}
