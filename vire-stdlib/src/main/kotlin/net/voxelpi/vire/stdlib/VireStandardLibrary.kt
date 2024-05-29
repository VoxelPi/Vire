package net.voxelpi.vire.stdlib

import net.voxelpi.vire.engine.environment.library.KotlinLibrary
import net.voxelpi.vire.stdlib.kernel.AndGate
import net.voxelpi.vire.stdlib.kernel.BufferGate
import net.voxelpi.vire.stdlib.kernel.Clock
import net.voxelpi.vire.stdlib.kernel.DFlipFlop
import net.voxelpi.vire.stdlib.kernel.DLatch
import net.voxelpi.vire.stdlib.kernel.Demultiplexer
import net.voxelpi.vire.stdlib.kernel.FullAdder
import net.voxelpi.vire.stdlib.kernel.HalfAdder
import net.voxelpi.vire.stdlib.kernel.Input
import net.voxelpi.vire.stdlib.kernel.JKFlipFlop
import net.voxelpi.vire.stdlib.kernel.JKLatch
import net.voxelpi.vire.stdlib.kernel.Memory
import net.voxelpi.vire.stdlib.kernel.Multiplexer
import net.voxelpi.vire.stdlib.kernel.NandGate
import net.voxelpi.vire.stdlib.kernel.NorGate
import net.voxelpi.vire.stdlib.kernel.NotGate
import net.voxelpi.vire.stdlib.kernel.OrGate
import net.voxelpi.vire.stdlib.kernel.Output
import net.voxelpi.vire.stdlib.kernel.Packager
import net.voxelpi.vire.stdlib.kernel.SRFlipFlop
import net.voxelpi.vire.stdlib.kernel.SRLatch
import net.voxelpi.vire.stdlib.kernel.TFlipFlop
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

        // Latches
        register(DLatch)
        register(SRLatch)
        register(JKLatch)

        // Flip-Flops
        register(DFlipFlop)
        register(TFlipFlop)
        register(SRFlipFlop)
        register(JKFlipFlop)

        // Utilities
        register(FullAdder)
        register(HalfAdder)
        register(Clock)
        register(Memory)
        register(Packager)
        register(Unpackager)
        register(Multiplexer)
        register(Demultiplexer)
    }
}
