package net.voxelpi.vire.stdlib

import net.voxelpi.vire.api.simulation.library.Library
import net.voxelpi.vire.stdlib.component.Clock
import net.voxelpi.vire.stdlib.component.FullAdder
import net.voxelpi.vire.stdlib.component.HalfAdder
import net.voxelpi.vire.stdlib.component.Output
import net.voxelpi.vire.stdlib.component.logic.AndGate
import net.voxelpi.vire.stdlib.component.logic.BufferGate
import net.voxelpi.vire.stdlib.component.logic.NandGate
import net.voxelpi.vire.stdlib.component.logic.NorGate
import net.voxelpi.vire.stdlib.component.logic.NotGate
import net.voxelpi.vire.stdlib.component.logic.OrGate
import net.voxelpi.vire.stdlib.component.logic.XnorGate
import net.voxelpi.vire.stdlib.component.logic.XorGate

object VireStandardLibrary : Library("vire", "Vire","The vire standard library") {

    init {
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
        register(Output)
    }
}
