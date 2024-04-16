package net.voxelpi.vire.stdlib

import net.voxelpi.vire.api.circuit.library.Library
import net.voxelpi.vire.stdlib.statemachine.AndGate
import net.voxelpi.vire.stdlib.statemachine.BufferGate
import net.voxelpi.vire.stdlib.statemachine.Clock
import net.voxelpi.vire.stdlib.statemachine.FullAdder
import net.voxelpi.vire.stdlib.statemachine.HalfAdder
import net.voxelpi.vire.stdlib.statemachine.Input
import net.voxelpi.vire.stdlib.statemachine.Memory
import net.voxelpi.vire.stdlib.statemachine.NandGate
import net.voxelpi.vire.stdlib.statemachine.NorGate
import net.voxelpi.vire.stdlib.statemachine.NotGate
import net.voxelpi.vire.stdlib.statemachine.OrGate
import net.voxelpi.vire.stdlib.statemachine.Output
import net.voxelpi.vire.stdlib.statemachine.Packager
import net.voxelpi.vire.stdlib.statemachine.Unpackager
import net.voxelpi.vire.stdlib.statemachine.XnorGate
import net.voxelpi.vire.stdlib.statemachine.XorGate

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
