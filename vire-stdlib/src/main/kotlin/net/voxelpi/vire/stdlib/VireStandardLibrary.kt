package net.voxelpi.vire.stdlib

import net.voxelpi.vire.api.simulation.library.Library
import net.voxelpi.vire.stdlib.component.AndGate
import net.voxelpi.vire.stdlib.component.BufferGate
import net.voxelpi.vire.stdlib.component.Clock
import net.voxelpi.vire.stdlib.component.FullAdder
import net.voxelpi.vire.stdlib.component.HalfAdder
import net.voxelpi.vire.stdlib.component.Input
import net.voxelpi.vire.stdlib.component.Memory
import net.voxelpi.vire.stdlib.component.NandGate
import net.voxelpi.vire.stdlib.component.NorGate
import net.voxelpi.vire.stdlib.component.NotGate
import net.voxelpi.vire.stdlib.component.OrGate
import net.voxelpi.vire.stdlib.component.Output
import net.voxelpi.vire.stdlib.component.Packager
import net.voxelpi.vire.stdlib.component.Unpackager
import net.voxelpi.vire.stdlib.component.XnorGate
import net.voxelpi.vire.stdlib.component.XorGate

object VireStandardLibrary : Library(VIRE_STDLIB_ID, "Vire", "The vire standard library", emptyList()) {

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
        register(Packager.stateMachine)
        register(Unpackager.stateMachine)
    }
}
