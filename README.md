# Vire

[![GitHub CI Status](https://img.shields.io/github/actions/workflow/status/voxelpi/vire/ci.yml?branch=main&label=CI&style=for-the-badge)]()
[![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/net.voxelpi.vire/vire-api?server=https%3A%2F%2Frepo.voxelpi.net&nexusVersion=3&style=for-the-badge&label=stable)]()
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/net.voxelpi.vire/vire-api?server=https%3A%2F%2Frepo.voxelpi.net&nexusVersion=3&style=for-the-badge&label=dev)]()

Vire is a simple but powerful logic simulation library.

## Modules

This project contains the following modules:

| Name          | Description                                                |
|---------------|------------------------------------------------------------|
| `vire-api`    | The api of vire simulation library.                        |
| `vire-engine` | The default implementation of the vire simulation library. |
| `vire-stdlib` | A standard library with definitions for common components. |

## Simulation

There are three main concepts used in the simulation: networks, components and state machines.

### Networks

Networks are used to connect the ports of multiple components together to create complex circuits.
They consist of nodes that are connected to each other. 
All nodes in a single network are connected to each other via some (indirect) path.

A network also carries a state, which is set by the connected component ports 
that have an output assigned to them
The state is then read by all connected component ports that have an input assigned to them.
The state can be one of the following three types:
- `None`: The default state of a network that has not yet been assigned a value
- `Value`: This state is used when data is assigned to the network. 
The data can have multiple channels, where each channel is either `true` or `false`.
- `Invalid`: The resulting state, if two conflicting values are assigned to the same network.
values are conflicting if they have different number of channels or the values on the individual channels differ.

StateMachines are the core of each component. They can have inputs, outputs, variables and parameters 

### Components

These represent the components of a logical circuit. 
Each component has a state machine assigned to them which determines the behaviour of the component.
Additionally, they store the current state of all inputs, output, variables and parameters of the state machine.

Component ports are used to connect the state of an input or output of the state machine with the state of a connected network.
Each port can hold a single variable view instance. This can either be `null`, 
or a pair of a state machine io vector together with an index that indicate which entry of the io vector should be used.

### State Machine

A state machines defines the behaviour of a component. They can read values from assigned networks,
perform logic on these values and write them back to other assigned networks.

They have a number of different variables types in order to exchange state with networks, 
store state between simulation steps or add settings that can be modified from outside the state machine.
The following types exist:

- Input vectors are used to get the state of a connected network.
The elements of the vector are network states, which can each be bound to a different component port (or no port at all).
They have u unique name as well as an initial size. 
- Output vectors are used to modify the state of a connected network.
They are similar to input vectors, in the sense, that the elements of the vector are also network states,
which can be bound to component ports.
Like input vectors, they also have a unique name and an initial size.
- Variables allow the state machine to store state between simulation steps.
They can only be modified by state machine.
They have a unique name, a type, and an initial value.
While the value of a variable may change, their type must remain the same and cannot change.
- Parameters allow the user to configure the state machine to adapt its behaviour.
Therefor their value can be modified externally via the `parameter()` method on the component the state machine is assigned to.
Whenever a parameter of a state machine is modified, its `configure()` method is called.
They are similar to variables, in that they also have a unique name, a type and an initial value.
Additionally, a parameter also has a predicate that determines which values are valid to be assigned to it.

Any implementation of a state machine has the option to implement the following methods:
- The `configure()` method is called whenever the value of a parameter is changed or the component is reset.
The method should be used to perform the initialization logic of the state machine.
- The `tick()` method is called for every simulation step. 
At the beginning of every tick the current states of all assigned networks are available via the input vectors.
Using its variables and parameters a state machine can then update its output vectors accordingly.
