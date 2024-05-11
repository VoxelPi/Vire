package net.voxelpi.vire.engine.kernel.variable

import net.voxelpi.vire.engine.LogicState

internal typealias VectorSizeMap = Map<String, Int>

internal typealias MutableVectorSizeMap = MutableMap<String, Int>

internal typealias ParameterStateMap = Map<String, Any?>

internal typealias MutableParameterStateMap = MutableMap<String, Any?>

internal typealias SettingStateMap = Map<String, Any?>

internal typealias MutableSettingStateMap = MutableMap<String, Any?>

internal typealias FieldStateMap = Map<String, Any?>

internal typealias MutableFieldStateMap = MutableMap<String, Any?>

internal typealias InputStateMap = Map<String, Array<LogicState>>

internal typealias MutableInputStateMap = MutableMap<String, Array<LogicState>>

internal typealias OutputStateMap = Map<String, Array<LogicState>>

internal typealias MutableOutputStateMap = MutableMap<String, Array<LogicState>>
