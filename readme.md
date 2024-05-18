# Vire

[![GitHub CI Status](https://img.shields.io/github/actions/workflow/status/voxelpi/vire/ci.yml?branch=main&label=CI&style=for-the-badge)]()
[![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/net.voxelpi.vire/vire-engine?server=https%3A%2F%2Frepo.voxelpi.net&nexusVersion=3&style=for-the-badge&label=stable&color=blue)]()
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/net.voxelpi.vire/vire-engine?server=https%3A%2F%2Frepo.voxelpi.net&nexusVersion=3&style=for-the-badge&label=dev)]()

Vire is a simple but powerful logic simulation library.

## Modules

This project contains the following modules:

| Name          | Description                                                |
|---------------|------------------------------------------------------------|
| `vire-engine` | The vire circuit, kernel and simulation engine.            |
| `vire-stdlib` | A standard library with definitions for common components. |

## Getting Started

build.gradle.kts

```kotlin
repositories {
    maven {
        url = uri("https://repo.voxelpi.net/repository/maven-public/")
    }
}

dependencies {
    implementation("net.voxelpi.vire:vire-engine:<version>") // Simulation engine
    implementation("net.voxelpi.vire:vire-stdlib:<version>") // Standard library
}
```
## Examples

### Creating a kernel

This example creates a custom kernel that has two inputs and one output.
The kernel reads the two inputs and writes the result of the AND operation to its output.

```kotlin
val inputA = input("input_a")
val inputB = input("input_b")
val outputAnd = output("output")

val yourKernel = kernel(Identifier("your-namespace", "custom-and")) {

    update = { context: UpdateContext ->
        context[outputAnd] = context[inputA].booleanState() and context[inputB].booleanState()
    }
}
```
