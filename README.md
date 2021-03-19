# RunAnything
This repository provides the source of an JetBrains IDE plugin.
It adds a Run Configuration that can run/execute any command/executable.
There is support for setting environment variables and the working directory.
You can also provide program input to save you the hassle of typing in the console.
All but the environment variables and the program input fields have support for macros.

[This plugin on the marketplace](https://plugins.jetbrains.com/plugin/16128-runanything)

## Example

Example of the Run Configuration:

![Run Configuration](.github/JavaConfiguration.png)

Running this configuration provides the following result:

![Running](.github/JavaRun.png)

Run shell scripts without creating any extra files like so:

![Run Configuration](.github/FortuneConfiguration.png)

Which will result in the following run:

![Running](.github/FortuneRun.png)

## Compiling
Use gradle to build the plugin:

```
./gradlew assemble
```

The output should be in `build/distributions`.
