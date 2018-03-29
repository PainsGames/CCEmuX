package net.clgd.ccemux.plugins.hooks;

import net.clgd.ccemux.api.emulation.EmulatedComputer;
import net.clgd.ccemux.api.emulation.Emulator;

/**
 * Invoked while an {@link EmulatedComputer} is being created by CCEmuX, but
 * before it is actually constructed. This hook is the last call before the
 * construction of the emulated computer, so the {@link EmulatedComputer.Builder
 * Builder} will not be modified by CCEmuX - but it may be modified by other
 * plugins which also use this hook.
 * 
 * @author apemanzilla
 * @see ComputerCreated
 * @see RendererCreated
 * @see ComputerRemoved
 */
@FunctionalInterface
public interface CreatingComputer extends Hook {
	public void onCreatingComputer(Emulator emu, EmulatedComputer.Builder builder);
}
