package net.voxelpi.vire.engine.kernel.variable

public class UninitializedVariableException(
    public val variable: Variable<*>,
) : Exception("The variable \"${variable}\" has not jet been assigned a value.")
