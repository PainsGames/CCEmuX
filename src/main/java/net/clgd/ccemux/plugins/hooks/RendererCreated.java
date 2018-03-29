package net.clgd.ccemux.plugins.hooks;

import net.clgd.ccemux.api.emulation.Emulator;
import net.clgd.ccemux.api.rendering.Renderer;

/**
 * Called immediately after a {@link Renderer} is created.
 * 
 * @author apemanzilla
 * @see CreatingComputer
 * @see ComputerCreated
 * @see ComputerRemoved
 */
@FunctionalInterface
public interface RendererCreated extends Hook {
	public void onRendererCreated(Emulator emu, Renderer renderer);
}
