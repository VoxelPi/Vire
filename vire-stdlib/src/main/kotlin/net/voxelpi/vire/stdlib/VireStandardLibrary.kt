package net.voxelpi.vire.stdlib

import net.voxelpi.vire.engine.environment.library.KotlinLibrary
import net.voxelpi.vire.stdlib.kernel.AndGate
import net.voxelpi.vire.stdlib.kernel.BufferGate
import net.voxelpi.vire.stdlib.kernel.Clock
import net.voxelpi.vire.stdlib.kernel.FullAdder
import net.voxelpi.vire.stdlib.kernel.HalfAdder
import net.voxelpi.vire.stdlib.kernel.Input
import net.voxelpi.vire.stdlib.kernel.Memory
import net.voxelpi.vire.stdlib.kernel.NandGate
import net.voxelpi.vire.stdlib.kernel.NorGate
import net.voxelpi.vire.stdlib.kernel.NotGate
import net.voxelpi.vire.stdlib.kernel.OrGate
import net.voxelpi.vire.stdlib.kernel.Output
import net.voxelpi.vire.stdlib.kernel.Packager
import net.voxelpi.vire.stdlib.kernel.Unpackager
import net.voxelpi.vire.stdlib.kernel.XnorGate
import net.voxelpi.vire.stdlib.kernel.XorGate

public object VireStandardLibrary : KotlinLibrary(VIRE_STDLIB_ID, "Vire", "The vire standard library") {

    init {
        // IO
        register(Input)
        register(Output)

        // Logic gates
        register(BufferGate)
        register(NotGate)
        register(AndGate)
        register(OrGate)
        register(XorGate)
        register(NandGate)
        register(NorGate)
        register(XnorGate)

        // Utilities
        register(FullAdder)
        register(HalfAdder)
        register(Clock)
        register(Memory)
        register(Packager)
        register(Unpackager)
    }
}
