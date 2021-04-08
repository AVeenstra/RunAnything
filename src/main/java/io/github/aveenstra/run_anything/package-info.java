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

/**
 * This package is a plugin for Jetbrains IDEs. It provides a very basic run configuration.
 *
 * The {@link io.github.aveenstra.run_anything.RunAnythingConfigurationType} provides the toplevel definition of the run configuration.
 *
 * Options of the run configuration are represented by {@link io.github.aveenstra.run_anything.RunAnythingConfigurationOptions}. Extra fields should be added there with sane defaults.
 *
 * The UI is provided by {@link io.github.aveenstra.run_anything.RunAnythingSettingsEditor} and its form.
 */
package io.github.aveenstra.run_anything;
