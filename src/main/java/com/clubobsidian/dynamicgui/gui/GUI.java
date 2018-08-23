package com.clubobsidian.dynamicgui.gui;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;

import com.clubobsidian.dynamicgui.DynamicGUI;
import com.clubobsidian.dynamicgui.api.FunctionApi;
import com.clubobsidian.dynamicgui.entity.player.PlayerWrapper;
import com.clubobsidian.dynamicgui.function.Function;
import com.clubobsidian.dynamicgui.inventory.InventoryWrapper;
import com.clubobsidian.dynamicgui.inventory.ItemStackWrapper;
import com.clubobsidian.dynamicgui.manager.inventory.InventoryManager;
import com.clubobsidian.dynamicgui.objects.ModeEnum;
import com.clubobsidian.dynamicgui.objects.SoundWrapper;
import com.clubobsidian.dynamicgui.util.ChatColor;
import com.clubobsidian.dynamicgui.world.LocationWrapper;


public class GUI implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6294818826223305057L;
	private List<Slot> slots = new ArrayList<>();
	private String name;
	private String title;
	private int rows;
	private String permission;
	private String pMessage;
	private Boolean close;
	private ModeEnum modeEnum;
	private List<LocationWrapper<?>> locations = new ArrayList<>();
	private List<SoundWrapper> openingSounds = new ArrayList<>();
	private List<Integer> npcIds = new ArrayList<>();
	
	public GUI(String name, String title, int rows,String permission, String pMessage, Boolean close, ModeEnum modeEnum, List<Integer> npcIds, List<Slot> slots, List<LocationWrapper<?>> locations, List<SoundWrapper> openingSounds)
	{
		this.name = name;
		this.title = ChatColor.translateAlternateColorCodes(title);
		if(this.title.length() > 32)
			this.title = this.title.substring(0,31);
		this.rows = rows;
		this.slots = slots;
		this.permission = permission;
		this.pMessage = pMessage;
		this.close = close;
		this.modeEnum = modeEnum;
		this.npcIds = npcIds;
		this.locations = locations;
		this.openingSounds = openingSounds;
	}
	
	public GUI(GUI gui) 
	{
		this(gui.getName(),gui.getTitle(),gui.getRows(),gui.getPermission(),gui.getpMessage(),gui.getClose(),gui.getModeEnum(),gui.getNpcIds(),gui.getSlots(),gui.getLocations(), gui.getOpeningSounds());
	}

	public void buildInventory(PlayerWrapper<?> player)
	{	
		Object serverInventory = InventoryManager.get().createInventory(this.rows * 9, this.title);
		InventoryWrapper<?> inv = InventoryManager.get().createInventoryWrapper(serverInventory);
		
		for(int i = 0; i < this.slots.size(); i++)
		{
			System.out.println("Slot: " + i);
			Slot slot = this.slots.get(i);
			if(slot != null)
			{
				System.out.println("Slot is not null");
				slot.setOwner(this);
				if(slot.getPermission() != null)
				{
					if(player.hasPermission(slot.getPermission()))
					{
						boolean run = true;
						ItemStackWrapper<?> item = slot.buildItemStack(player);
						if(this.modeEnum == ModeEnum.ADD)
						{
							int index = inv.addItem(item);
							if(index != -1)
							{
								slot.setIndex(index);
							}
						}
						else
						{
							inv.setItem(slot.getIndex(), item);
						}
						for(Function loadFunction : slot.getLoadFunctions())
						{
							Function func = null;
							try 
							{
								func = FunctionApi.getFunctionByName(loadFunction.getName()).getClass().getConstructor(String.class).newInstance(loadFunction.getName());
								func.setOwner(slot);
							} 
							catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException| NoSuchMethodException | SecurityException e) 
							{
								e.printStackTrace();
							}
							if(func != null)
							{
								func.setData(loadFunction.getData());
								if(!func.function(player))
								{
									run = false;
								}
							}
							else
							{
								DynamicGUI.get().getLogger().error("Load function " + loadFunction.getName() + " does not exist, will not load in slot.");
								run = false;
							}
							if(!run)
							{
								List<Function> failFunctions = slot.getFailLoadFunctions(func.getName());
								if(failFunctions != null)
								{
									for(Function fail : failFunctions)
									{
										if(FunctionApi.getFunctionByName(fail.getName()) == null)
											continue;
										try 
										{
											Function failFunction = FunctionApi.getFunctionByName(fail.getName()).getClass().getConstructor(String.class).newInstance(fail.getName());
											failFunction.setData(fail.getData());
											failFunction.setOwner(slot);
											boolean failResult = failFunction.function(player);
											if(!failResult)
											{
												break;
											}
										} 
										catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e1) 
										{
											e1.printStackTrace();
											break;
										}

									}
								}
								break;
							}
						}
					}
				}
				else
				{
					boolean run = true;
					ItemStackWrapper<?> item = slot.buildItemStack(player);
					if(this.modeEnum == ModeEnum.ADD)
					{
						int index = inv.addItem(item);
						if(index != -1)
						{
							slot.setIndex(index);
						}
					}
					else
					{
						inv.setItem(slot.getIndex(), item);
					}
					for(Function loadFunction : slot.getLoadFunctions())
					{	
						Function func = null;
						try 
						{
							func =  FunctionApi.getFunctionByName(loadFunction.getName()).getClass().getConstructor(String.class).newInstance(loadFunction.getName());
						} 
						catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException| NoSuchMethodException | SecurityException e) 
						{
							DynamicGUI.get().getLogger().error(loadFunction.getName() + " does not exist!");
							e.printStackTrace();
						}
						if(func != null)
						{
							func.setData(loadFunction.getData());
							func.setOwner(slot);
							if(!func.function(player))
							{
								run = false;
							}
						}
						else
						{
							DynamicGUI.get().getLogger().error("Load function " + loadFunction.getName() + " does not exist, will not load in slot.");
							run = false;
						}
						if(!run)
						{
							List<Function> failFunctions = slot.getFailLoadFunctions(func.getName());
							if(failFunctions != null)
							{
								for(Function fail : failFunctions)
								{
									if(FunctionApi.getFunctionByName(fail.getName()) == null)
										continue;
									try 
									{
										Function failFunction = FunctionApi.getFunctionByName(fail.getName()).getClass().getConstructor(String.class).newInstance(fail.getName());
										failFunction.setData(fail.getData());
										failFunction.setOwner(slot);
										boolean failResult = failFunction.function(player);
										if(!failResult)
										{
											break;
										}
									} 
									catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e1) 
									{
										e1.printStackTrace();
										break;
									}

								}
							}
							break;
						}
					}
				}
			}
		}
		player.openInventory(inv);
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	
	public String getPermission()
	{
		return this.permission;
	}
	
	public String getpMessage()
	{
		return this.pMessage;
	}
	
	public int getRows()
	{
		return this.rows;
	}
	
	public List<Slot> getSlots()
	{
		return this.slots;
	}
	
	public Boolean getClose()
	{
		return this.close;
	}
	
	public List<Integer> getNpcIds()
	{
		return this.npcIds;
	}
	
	public List<LocationWrapper<?>> getLocations()
	{
		return this.locations;
	}
	
	public List<SoundWrapper> getOpeningSounds()
	{
		return this.openingSounds;
	}
	
	public ModeEnum getModeEnum()
	{
		return this.modeEnum;
	}
	
	public GUI clone()
	{
		return SerializationUtils.clone(this);
	}
}