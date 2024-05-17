package net.voxelpi.vire.engine.kernel.custom

public abstract class CustomKernel {

    public open fun initialize() {}

    public open fun update() {}

    public companion object {
        public const val INSTANCE_FIELD_NAME: String = "__instance__"
    }
}
