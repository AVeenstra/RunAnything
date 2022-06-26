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

import com.intellij.execution.configuration.EnvironmentVariablesComponent
import com.intellij.ide.macro.MacrosDialog
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.fields.ExpandableTextField
import com.intellij.ui.components.fields.ExtendableTextField
import javax.swing.JComponent
import javax.swing.JPanel

class RunAnythingSettingsEditor : SettingsEditor<RunAnythingConfiguration>() {
    private val topPanel: JPanel? = null
    private var commandField: TextFieldWithBrowseButton? = null
    private var argumentsField: ExpandableTextField? = null
    private val environmentField: EnvironmentVariablesComponent? = null
    private var workingDirectoryField: TextFieldWithBrowseButton? = null
    private var enableInputCheckBox: JBCheckBox? = null
    private val closeInputCheckBox: JBCheckBox? = null
    private var inputTextComponent: JBTextArea? = null

    override fun resetEditorFrom(s: RunAnythingConfiguration) {
        val options = s.options

        commandField!!.text = options.command.orEmpty()
        argumentsField!!.text = options.arguments.orEmpty()
        environmentField!!.envData = options.environmentVariablesData
        workingDirectoryField!!.text = options.workingDirectory.orEmpty()
        enableInputCheckBox!!.isSelected = options.inputEnabled
        closeInputCheckBox!!.isSelected = options.inputClose
        inputTextComponent!!.text = options.inputText.orEmpty()

        setInputFieldsEnabled(options.inputEnabled)
    }

    @Throws(ConfigurationException::class)
    override fun applyEditorTo(s: RunAnythingConfiguration) {
        val options = s.options
        options.command = commandField!!.text
        options.arguments = argumentsField!!.text
        options.environmentVariablesData = environmentField!!.envData
        options.workingDirectory = workingDirectoryField!!.text
        options.inputEnabled = enableInputCheckBox!!.isSelected
        options.inputClose = closeInputCheckBox!!.isSelected
        options.inputText = inputTextComponent!!.text
    }

    override fun createEditor(): JComponent {
        return topPanel!!
    }

    private fun setInputFieldsEnabled(enabled: Boolean) {
        closeInputCheckBox!!.isEnabled = enabled
        inputTextComponent!!.isEnabled = enabled
    }

    private fun createUIComponents() {
        val commandFieldFilter = FileChooserDescriptor(true, false, false, true, false, false)
        commandField = TextFieldWithBrowseButton()
        commandField!!.addBrowseFolderListener("Select a Program", "", null, commandFieldFilter)
        MacrosDialog.addTextFieldExtension((commandField!!.textField as ExtendableTextField))
        argumentsField = ExpandableTextField()
        MacrosDialog.addTextFieldExtension(argumentsField!!)
        val workingDirectoryFieldFilter = FileChooserDescriptor(false, true, false, false, false, false)
        workingDirectoryField = TextFieldWithBrowseButton()
        workingDirectoryField!!.addBrowseFolderListener("Select a Directory", "", null, workingDirectoryFieldFilter)
        MacrosDialog.addTextFieldExtension((workingDirectoryField!!.textField as ExtendableTextField))
        enableInputCheckBox = JBCheckBox()
        enableInputCheckBox!!.addActionListener {
            setInputFieldsEnabled(
                enableInputCheckBox!!.isSelected
            )
        }
        inputTextComponent = JBTextArea(5, 80)
        inputTextComponent!!.autoscrolls = true
        inputTextComponent!!.lineWrap = true
    }
}