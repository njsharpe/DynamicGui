package me.virustotal.dynamicgui.server;

import java.util.Collection;
import java.util.UUID;

import me.virustotal.dynamicgui.entity.player.PlayerWrapper;
import me.virustotal.dynamicgui.scheduler.Scheduler;

public abstract class FakeServer {

	private Scheduler scheduler;
	public FakeServer(Scheduler scheduler)
	{
		this.scheduler = scheduler;
	}
	
	public Scheduler getScheduler()
	{
		return this.scheduler;
	}
	
	public abstract void broadcastMessage(String message);
	public abstract void dispatchServerCommand(String command);
	public abstract PlayerWrapper<?> getPlayer(UUID uuid);
	public abstract PlayerWrapper<?> getPlayer(String name);
	public abstract Collection<PlayerWrapper<?>> getOnlinePlayers();
	public abstract int getGlobalPlayerCount();
	public abstract ServerType getType();
}