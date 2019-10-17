package com.clubobsidian.dynamicgui.animation;

public interface AnimationHolder extends Refreshable {

	public int getCurrentTick();
	public int tick();
	public int getFrame();
	public void resetFrame();
	
}