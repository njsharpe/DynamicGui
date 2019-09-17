/*
   Copyright 2019 Club Obsidian and contributors.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.clubobsidian.dynamicgui.listener.bukkit;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.clubobsidian.dynamicgui.DynamicGui;
import com.clubobsidian.dynamicgui.entity.EntityWrapper;
import com.clubobsidian.dynamicgui.entity.PlayerWrapper;
import com.clubobsidian.dynamicgui.entity.bukkit.BukkitEntityWrapper;
import com.clubobsidian.dynamicgui.entity.bukkit.BukkitPlayerWrapper;

public class EntityClickListener implements Listener {

	@EventHandler
	public void onEntityClick(PlayerInteractEntityEvent e)
	{
		if(e.getRightClicked() != null)
		{
			PlayerWrapper<Player> playerWrapper = new BukkitPlayerWrapper<Player>(e.getPlayer());
			EntityWrapper<Entity> entityWrapper = new BukkitEntityWrapper<Entity>(e.getRightClicked());
			
			DynamicGui.get().getEventBus().callEvent(new com.clubobsidian.dynamicgui.event.inventory.PlayerInteractEntityEvent(playerWrapper, entityWrapper));
		}
	}
}