/*
 *    Copyright 2021 Club Obsidian and contributors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.clubobsidian.dynamicgui.manager.dynamicgui;

import com.clubobsidian.dynamicgui.DynamicGui;
import com.clubobsidian.dynamicgui.enchantment.EnchantmentWrapper;
import com.clubobsidian.dynamicgui.entity.PlayerWrapper;
import com.clubobsidian.dynamicgui.event.inventory.GuiLoadEvent;
import com.clubobsidian.dynamicgui.event.inventory.GuiPreloadEvent;
import com.clubobsidian.dynamicgui.gui.Gui;
import com.clubobsidian.dynamicgui.gui.ModeEnum;
import com.clubobsidian.dynamicgui.gui.Slot;
import com.clubobsidian.dynamicgui.inventory.InventoryWrapper;
import com.clubobsidian.dynamicgui.logger.LoggerWrapper;
import com.clubobsidian.dynamicgui.manager.entity.EntityManager;
import com.clubobsidian.dynamicgui.manager.material.MaterialManager;
import com.clubobsidian.dynamicgui.manager.world.LocationManager;
import com.clubobsidian.dynamicgui.parser.function.FunctionType;
import com.clubobsidian.dynamicgui.parser.gui.GuiToken;
import com.clubobsidian.dynamicgui.parser.macro.MacroToken;
import com.clubobsidian.dynamicgui.parser.slot.SlotToken;
import com.clubobsidian.dynamicgui.plugin.DynamicGuiPlugin;
import com.clubobsidian.dynamicgui.server.FakeServer;
import com.clubobsidian.dynamicgui.server.ServerType;
import com.clubobsidian.dynamicgui.util.ChatColor;
import com.clubobsidian.dynamicgui.util.FunctionUtil;
import com.clubobsidian.dynamicgui.util.HashUtil;
import com.clubobsidian.dynamicgui.world.LocationWrapper;
import com.clubobsidian.wrappy.Configuration;
import com.clubobsidian.wrappy.ConfigurationSection;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

public class GuiManager {

    private static GuiManager instance;

    private Map<String, Gui> guis;
    private final Map<UUID, Gui> playerGuis;
    private Map<String, Gui> cachedGuis;
    private Map<String, GuiToken> cachedTokens;
    private Map<String, List<MacroToken>> globalMacros;
    private Map<String, List<MacroToken>> cachedGlobalMacros;
    private Map<String, byte[]> guiHashes;
    private Map<String, byte[]> globalMacrosTimestamps;
    private final Set<String> modifiedMacros;

    private GuiManager() {
        this.guis = new HashMap<>();
        this.playerGuis = new HashMap<>();
        this.cachedGuis = new HashMap<>();
        this.cachedTokens = new HashMap<>();
        this.globalMacros = new LinkedHashMap<>();
        this.cachedGlobalMacros = new HashMap<>();
        this.guiHashes = new HashMap<>();
        this.globalMacrosTimestamps = new HashMap<>();
        this.modifiedMacros = new HashSet<>();
    }

    public static GuiManager get() {
        if(instance == null) {
            instance = new GuiManager();
            instance.loadGlobalMacros();
            instance.loadGuis();
        }
        return instance;
    }

    public boolean hasGuiName(String name) {
        return this.guis.containsKey(name);
    }

    public Gui getGuiByName(String name) {
        Gui gui = this.guis.get(name);
        if(gui != null) {
            return gui.clone();
        }

        return null;
    }

    public void reloadGuis(boolean force) {
        DynamicGui.get().getLogger().info("Reloading guis!");
        DynamicGui.get().getPlugin().unloadCommands();
        this.cachedGuis = this.guis;
        this.guis = new HashMap<>();
        this.cachedGlobalMacros = this.globalMacros;

        this.globalMacros = new HashMap<>();
        if(force) {
            this.cachedTokens = new HashMap<>();
            this.cachedGuis = new HashMap<>();
            this.guiHashes = new HashMap<>();
            this.globalMacrosTimestamps = new HashMap<>();
            this.cachedGlobalMacros = new HashMap<>();
        }

        this.loadGlobalMacros();
        this.loadGuis();
    }

    public List<Gui> getGuis() {
        return new ArrayList<>(this.guis.values());
    }

    public Map<UUID, Gui> getPlayerGuis() {
        return this.playerGuis;
    }

    public boolean hasGuiCurrently(PlayerWrapper<?> playerWrapper) {
        return this.playerGuis.get(playerWrapper.getUniqueId()) != null;
    }

    public boolean hasGuiOpen(PlayerWrapper<?> playerWrapper) {
        if(playerWrapper.getOpenInventoryWrapper() == null) {
            return false;
        } else return this.hasGuiCurrently(playerWrapper);
    }

    public void cleanupGui(PlayerWrapper<?> playerWrapper) {
        this.playerGuis.remove(playerWrapper.getUniqueId());
    }

    public Gui getCurrentGui(PlayerWrapper<?> playerWrapper) {
        return this.playerGuis.get(playerWrapper.getUniqueId());
    }

    public boolean openGui(Object player, String guiName) {
        return this.openGui(EntityManager.get().createPlayerWrapper(player), guiName);
    }

    public boolean openGui(Object player, Gui gui) {
        return this.openGui(EntityManager.get().createPlayerWrapper(player), gui);
    }

    public boolean openGui(PlayerWrapper<?> playerWrapper, String guiName) {
        return this.openGui(playerWrapper, guiName, null);
    }

    public boolean openGui(PlayerWrapper<?> playerWrapper, String guiName, Gui back) {
        return this.openGui(playerWrapper, this.getGuiByName(guiName), back);
    }

    public boolean openGui(PlayerWrapper<?> playerWrapper, Gui gui) {
        return this.openGui(playerWrapper, gui, null);
    }

    public boolean openGui(PlayerWrapper<?> playerWrapper, Gui gui, Gui back) {
        if(gui == null) {
            playerWrapper.sendMessage(DynamicGui.get().getNoGui());
            return false;
        }

        Gui clonedGui = gui.clone();
        if(back != null) {
            clonedGui.setBack(back.clone());
        }

        GuiPreloadEvent preloadEvent = new GuiPreloadEvent(clonedGui, playerWrapper);
        DynamicGui.get().getEventBus().callEvent(preloadEvent);

        //Run gui load functions
        boolean ran = FunctionUtil.tryFunctions(clonedGui, FunctionType.LOAD, playerWrapper);
        GuiLoadEvent event = new GuiLoadEvent(clonedGui, playerWrapper);
        if(!ran) {
            event.setCancelled(true);
        }
        DynamicGui.get().getEventBus().callEvent(event);

        if(ran) {
            InventoryWrapper<?> inventoryWrapper = clonedGui.buildInventory(playerWrapper);

            //Run slot load functions
            for(Slot slot : clonedGui.getSlots()) {
                FunctionUtil.tryFunctions(slot, FunctionType.LOAD, playerWrapper);
            }

            if(inventoryWrapper == null) {
                return false;
            }

            FakeServer server = DynamicGui.get().getServer();
            if(server.getType() == ServerType.SPONGE) {
                server.getScheduler().scheduleSyncDelayedTask(DynamicGui.get().getPlugin(), () -> {
                    playerWrapper.openInventory(inventoryWrapper);
                }, 1L);
            } else {
                playerWrapper.openInventory(inventoryWrapper);
            }

            this.playerGuis.put(playerWrapper.getUniqueId(), clonedGui);
            DynamicGui.get().getServer().getScheduler().scheduleSyncDelayedTask(DynamicGui.get().getPlugin(), () -> {
                playerWrapper.updateInventory();
            }, 2L);
        }
        return ran;
    }

    private void loadGlobalMacroFromFile(File file) {
        String macroName = file.getName().substring(0, file.getName().lastIndexOf("."));
        byte[] fileHash = HashUtil.getMD5(file);
        byte[] cachedHash = this.globalMacrosTimestamps.get(macroName);
        if(cachedHash == null || fileHash != cachedHash) {
            List<MacroToken> tokens = new ArrayList<>();
            Configuration config = Configuration.load(file);
            for(String key : config.getKeys()) {
                ConfigurationSection section = config.getConfigurationSection(key);
                MacroToken token = new MacroToken(section);
                tokens.add(token);
            }

            this.modifiedMacros.add(macroName);
            this.globalMacrosTimestamps.put(macroName, fileHash);
            this.globalMacros.put(macroName, tokens);
        } else {
            List<MacroToken> cachedTokens = this.cachedGlobalMacros.get(macroName);
            this.globalMacros.put(macroName, cachedTokens);
        }
    }

    private void loadGlobalMacros() {
        File macroFolder = DynamicGui.get().getPlugin().getMacroFolder();

        Collection<File> macroFiles = FileUtils.listFiles(macroFolder, new String[]{"yml", "json", "conf", "xml"}, true);

        for(File file : macroFiles) {
            this.loadGlobalMacroFromFile(file);
        }

        Iterator<Entry<String, List<MacroToken>>> it = this.cachedGlobalMacros.entrySet().iterator();
        while(it.hasNext()) {
            Entry<String, List<MacroToken>> next = it.next();
            String macroName = next.getKey();
            if(!this.globalMacros.containsKey(macroName)) {
                it.remove();
                this.globalMacrosTimestamps.remove(macroName);
            }
        }
    }

    private void loadGuis() {
        this.loadFileGuis();
        this.loadRemoteGuis();
        this.modifiedMacros.clear();
        this.cleanupGuis();
    }

    private void loadGuiFromFile(Configuration yaml, File file) {
        DynamicGui dynamicGui = DynamicGui.get();
        DynamicGuiPlugin plugin = dynamicGui.getPlugin();

        try {
            String guiName = file.getName().substring(0, file.getName().lastIndexOf("."));
            byte[] cachedHash = this.guiHashes.get(guiName);
            GuiToken token = this.cachedTokens.get(guiName);
            byte[] guiHash = HashUtil.getMD5(file);
            if(token != null && cachedHash != null && cachedHash == guiHash && !hasUpdatedMacro(token)) {
                Gui cachedGui = this.cachedGuis.get(guiName);
                for(String alias : token.getAlias()) {
                    plugin.createCommand(guiName, alias);
                }

                this.guis.put(guiName, cachedGui);
                dynamicGui.getLogger().info("cached gui \"" + guiName + "\" has been loaded!");
            } else {
                this.guiHashes.put(guiName, guiHash);
                this.loadGuiFromConfiguration(guiName, yaml);
            }
        } catch(NullPointerException ex) {
            dynamicGui.getLogger().info("Error loading in file: " + file.getName());
            ex.printStackTrace();
        }
    }

    private void loadFileGuis() {
        DynamicGui dynamicGui = DynamicGui.get();
        DynamicGuiPlugin plugin = dynamicGui.getPlugin();
        File guiFolder = plugin.getGuiFolder();

        Collection<File> ar = FileUtils.listFiles(guiFolder, new String[]{"yml", "json", "conf", "xml"}, true);

        if(ar.size() != 0) {
            for(File file : ar) {
                Configuration config = Configuration.load(file);
                this.loadGuiFromFile(config, file);
            }
        } else {
            DynamicGui.get().getLogger().error("No guis found, please add guis or issues may occur!");
        }
    }

    private boolean hasUpdatedMacro(GuiToken token) {
        List<String> macros = token.getLoadMacros();
        for(String macro : macros) {
            if(this.modifiedMacros.contains(macro)) {
                return true;
            }
        }

        return false;
    }

    private void loadRemoteGui(String guiName, String strUrl) {
        try {
            URL url = new URL(strUrl);
            File file = new File(DynamicGui.get().getPlugin().getGuiFolder(), guiName);
            Configuration yaml = Configuration.load(url, file);
            this.loadGuiFromFile(yaml, file);
        } catch(MalformedURLException e) {
            e.printStackTrace();
            DynamicGui.get().getLogger().error("An error occured when loading from the url " + strUrl + " please ensure you have the correct url.");
        }
    }

    private void loadRemoteGuis() {
        File configFile = new File(DynamicGui.get().getPlugin().getDataFolder(), "config.yml");

        Configuration config = Configuration.load(configFile);
        if(config.get("remote-guis") != null) {
            ConfigurationSection remote = config.getConfigurationSection("remote-guis");
            for(String key : remote.getKeys()) {
                ConfigurationSection guiSection = remote.getConfigurationSection(key);
                String strURL = guiSection.getString("url");
                String guiName = guiSection.getString("file-name");
                this.loadRemoteGui(guiName, strURL);
            }
        }
    }

    private void cleanupGuis() {
        Iterator<Entry<String, Gui>> it = this.cachedGuis.entrySet().iterator();
        while(it.hasNext()) {
            Entry<String, Gui> next = it.next();
            String guiName = next.getKey();
            if(!this.guis.containsKey(guiName)) {
                it.remove();
                this.guiHashes.remove(guiName);
                this.cachedTokens.remove(guiName);
            }
        }
    }

    private void loadGuiFromConfiguration(String guiName, Configuration config) {
        LoggerWrapper<?> logger = DynamicGui.get().getLogger();

        GuiToken guiToken = new GuiToken(config);
        List<MacroToken> guiTokens = new ArrayList<>();
        List<String> loadMacros = guiToken.getLoadMacros();

        if(loadMacros.size() > 0) {
            for(String macro : loadMacros) {
                List<MacroToken> macroTokens = this.globalMacros.get(macro);
                if(macroTokens != null) {
                    for(MacroToken t : macroTokens) {
                        guiTokens.add(t);
                    }
                } else {
                    logger.error("Invalid global macro specified " + macro + " in gui \"" + guiName + "\"");
                }
            }

            guiToken = new GuiToken(config, guiTokens);
        }

        this.cachedTokens.put(guiName, guiToken);
        List<Slot> slots = this.createSlots(guiToken);
        final Gui gui = this.createGui(guiToken, guiName, slots, DynamicGui.get().getPlugin());

        this.guis.put(guiName, gui);
        logger.info("gui \"" + gui.getName() + "\" has been loaded!");
    }

    private List<Slot> createSlots(GuiToken guiToken) {
        List<Slot> slots = new ArrayList<>();
        Iterator<Entry<Integer, SlotToken>> it = guiToken.getSlots().entrySet().iterator();
        while(it.hasNext()) {
            Entry<Integer, SlotToken> next = it.next();
            int index = next.getKey();
            SlotToken slotToken = next.getValue();

            String icon = MaterialManager.get().normalizeMaterial(slotToken.getIcon());
            String name = slotToken.getName();

            if(name != null) {
                name = ChatColor.translateAlternateColorCodes('&', name);
            }

            String nbt = slotToken.getNbt();

            List<String> lore = new ArrayList<>();
            for(String ls : slotToken.getLore()) {
                lore.add(ChatColor.translateAlternateColorCodes('&', ls));
            }

            List<EnchantmentWrapper> enchants = new ArrayList<>();

            for(String ench : slotToken.getEnchants()) {
                String[] args = ench.split(",");
                enchants.add(new EnchantmentWrapper(args[0], Integer.parseInt(args[1])));
            }

            List<String> itemFlags = slotToken.getItemFlags();

            int amount = slotToken.getAmount();

            boolean close = slotToken.isClosed();

            short data = slotToken.getData();

            boolean glow = slotToken.getGlow();
            boolean moveable = slotToken.isMoveable();

            String modelProvider = slotToken.getModelProvider();
            String modelData = slotToken.getModelData();

            int updateInterval = slotToken.getUpdateInterval();

            Map<String, String> metadata = slotToken.getMetadata();

            slots.add(new Slot(index, amount, icon, name, nbt, data, glow, moveable,
                    close, lore, enchants, itemFlags, modelProvider, modelData,
                    slotToken.getFunctionTree(), updateInterval, metadata));
        }

        return slots;
    }

    private Gui createGui(final GuiToken guiToken, final String guiName, final List<Slot> slots, final DynamicGuiPlugin plugin) {
        String type = guiToken.getType();
        String title = guiToken.getTitle();
        int rows = guiToken.getRows();
        List<String> aliases = guiToken.getAlias();

        for(String alias : aliases) {
            plugin.createCommand(guiName, alias);
        }

        boolean close = guiToken.isClosed();

        List<LocationWrapper<?>> locations = new ArrayList<>();
        for(String location : guiToken.getLocations()) {
            locations.add(LocationManager.get().toLocationWrapper(location));
        }

        ModeEnum modeEnum = ModeEnum.valueOf(guiToken.getMode().toString());

        Map<String, List<Integer>> npcIds = guiToken.getNpcs();
        Map<String, String> metadata = guiToken.getMetadata();

        return new Gui(guiName, type, title, rows, close, modeEnum, npcIds, slots, locations, guiToken.getFunctions(), metadata);
    }
}