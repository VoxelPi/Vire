package net.voxelpi.vire.stdlib

import net.voxelpi.vire.engine.environment.library.KotlinLibrary
import net.voxelpi.vire.engine.kernel.registered.LibraryKernel
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

    // IO
    public val INPUT_KERNEL: LibraryKernel = register("input", Input)
    public val OUTPUT_KERNEL: LibraryKernel = register("output", Output)

    // Logic gates
    public val BUFFER_GATE_KERNEL: LibraryKernel = register("buffer", BufferGate)
    public val NOT_GATE_KERNEL: LibraryKernel = register("not", NotGate)
    public val AND_GATE_KERNEL: LibraryKernel = register("and", AndGate)
    public val OR_GATE_KERNEL: LibraryKernel = register("or", OrGate)
    public val XOR_GATE_KERNEL: LibraryKernel = register("xor", XorGate)
    public val NAND_GATE_KERNEL: LibraryKernel = register("nand", NandGate)
    public val NOR_GATE_KERNEL: LibraryKernel = register("nor", NorGate)
    public val XNOR_GATE_KERNEL: LibraryKernel = register("xnor", XnorGate)

    // Latches
    public val D_LATCH_KERNEL: LibraryKernel = register("d_latch", DLatch)
    public val SR_LATCH_KERNEL: LibraryKernel = register("sr_latch", SRLatch)
    public val JK_LATCH_KERNEL: LibraryKernel = register("jk_latch", JKLatch)

    // Flip-Flops
    public val D_FLIP_FLOP_KERNEL: LibraryKernel = register("d_flip_flop", DFlipFlop)
    public val T_FLIP_FLOP_KERNEL: LibraryKernel = register("t_flip_flop", TFlipFlop)
    public val SR_FLIP_FLOP_KERNEL: LibraryKernel = register("sr_flip_flop", SRFlipFlop)
    public val JK_FLIP_FLOP_KERNEL: LibraryKernel = register("jk_flip_flop", JKFlipFlop)

    // Utilities
    public val FULL_ADDER_KERNEL: LibraryKernel = register("full_adder", FullAdder)
    public val HALF_ADDER_KERNEL: LibraryKernel = register("half_adder", HalfAdder)
    public val CLOCK_KERNEL: LibraryKernel = register("clock", Clock)
    public val MEMORY_KERNEL: LibraryKernel = register("memory", Memory)
    public val PACKAGER_KERNEL: LibraryKernel = register("packager", Packager)
    public val UNPACKAGER_KERNEL: LibraryKernel = register("unpackager", Unpackager)
    public val MULTIPLEXER_KERNEL: LibraryKernel = register("mux", Multiplexer)
    public val DEMULTIPLEXER_KERNEL: LibraryKernel = register("demux", Demultiplexer)
}
