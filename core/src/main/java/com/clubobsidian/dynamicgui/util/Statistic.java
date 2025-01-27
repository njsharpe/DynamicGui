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
package com.clubobsidian.dynamicgui.util;

public enum Statistic {

    ANIMALS_BRED("ANIMALS_BRED", "ANIMALS_BRED"),
    ARMOR_CLEANED("ARMOR_CLEANED", "ARMOR_CLEANED"),
    AVIATE_ONE_CM("AVIATE_ONE_CM", "AVIATE_ONE_CM"),
    BANNER_CLEANED("BANNER_CLEANED", "BANNER_CLEANED"),
    BEACON_INTERACTION("BEACON_INTERACTION", "BEACON_INTERACTION"),
    BOAT_ONE_CM("BOAT_ONE_CM", "BOAT_ONE_CM"),
    BREAK_ITEM("BREAK_ITEM", null, StatisticType.MATERIAL),
    BREWINGSTAND_INTERACTION("BREWINGSTAND_INTERACTION", "BREWINGSTAND_INTERACTION"),
    CAKE_SLICES_EATEN("CAKES_SLICES_EATEN", "CAKES_SLICES_EATEN"),
    CAULDRON_FILLED("CAULDRON_FILLED", "CAULDRON_FILLED"),
    CAULDRON_USED("CAULDRON_USED", "CAULDRON_USED"),
    CHEST_OPENED("CHEST_OPENED", "CHEST_OPENED"),
    CLIMB_ONE_CM("CLIMB_ONE_CM", "CLIMB_ONE_CM"),
    CRAFT_ITEM("CRAFT_ITEM", null, StatisticType.MATERIAL),
    CRAFTING_TABLE_INTERACTION("CRAFTING_TABLE_INTERACTION", "CRAFTING_TABLE_INTERACTION"),
    CROUCH_ONE_CM("CROUCH_ONE_CM", "CROUCH_ONE_CM"),
    DAMAGE_DEALT("DAMAGE_DEALT", "DAMAGE_DEALT"),
    DAMAGE_TAKEN("DAMAGE_TAKEN", "DAMAGE_TAKEN"),
    DEATHS("DEATHS", "DEATHS"),
    DISPENSER_INSPECTED("DISPENSER_INSPECTED", "DISPENSER_INSPECTED"),
    DROP("DROP", "DROP", StatisticType.MATERIAL),
    DROP_COUNT("DROP_COUNT", null),
    DROPPER_INSPECTED("DROPPER_INSPECTED", "DROPPED_INSPECTED"),
    ENDERCHEST_OPENED("ENDERCHEST_OPENED", "ENDERCHEST_OPENED"),
    ENTITY_KILLED_BY("ENTITY_KILLED_BY", null, StatisticType.ENTITY),
    FALL_ONE_CM("FALL_ONE_CM", "FALL_ONE_CM"),
    FISH_CAUGHT("FISH_CAUGHT", "FISH_CAUGHT"),
    FLOWER_POTTED("FLOWER_POTTED", "FLOWER_POTTED"),
    FLY_ONE_CM("FLY_ONE_CM", null),
    FURNACE_INTERACTION("FURNACE_INTERACTION", "FURNACE_INTERACTION"),
    HOPPER_INSPECTED("HOPPER_INSPECTED", "HOPPER_INSPECTED"),
    HORSE_ONE_CM("HORSE_ONE_CM", "HORSE_ONE_CM"),
    ITEM_ENCHANTED("ITEM_ENCHANTED", "ITEM_ENCHANTED"),
    JUMP("JUMP", "JUMP"),
    KILL_ENTITY("KILL_ENTITY", null, StatisticType.ENTITY),
    LEAVE_GAME("LEAVE_GAME", "LEAVE_GAME"),
    MINE_BLOCK("MINE_BLOCK", null, StatisticType.MATERIAL),
    MINECART_ONE_CM("MINECART_ONE_CM", "MINECART_ONE_CM"),
    MOB_KILLS("MOB_KILLS", "MOB_KILLS"),
    NOTEBLOCK_PLAYED("NOTEBLOCK_PLAYED", "NOTEBLOCK_PLAYED"),
    NOTEBLOCK_TUNED("NOTEBLOCK_TUNED", "NOTEBLOCK_TUNED"),
    PICKUP("PICKUP", null, StatisticType.MATERIAL),
    PIG_ONE_CM("PIG_ONE_CM", "PIG_ONE_CM"),
    PLAYER_KILLS("PLAYER_KILLS", "PLAYER_KILLS"),
    RECORD_PLAYED("RECORD_PLAYED", "RECORD_PLAYED"),
    SHULKER_BOX_OPENED("SHULKER_BOX_OPENED", "OPEN_SHULKER_BOX"),
    SLEEP_IN_BED("SLEEP_IN_BED", "SLEEP_IN_BED"),
    SNEAK_TIME("SNEAK_TIME", "SNEAK_TIME"),
    SPRINT_ONE_CM("SPRINT_ONE_CM", "SPRINT_ONE_CM"),
    SWIM_ONE_CM("SWIM_ONE_CM", "SWIM_ONE_CM"),
    TALKED_TO_VILLAGER("TALKED_TO_VILAGER", "TALKED_TO_VILLAGER"),
    TIME_PLAYED_TICK("PLAY_ONE_TICK", "TIME_PLAYED"),
    TIME_PLAYED("PLAY_ONE_MINUTE", "TIME_PLAYED"),
    TIME_SINCE_DEATH("TIME_SINCE_DEATH", "TIME_SINCE_DEATH"),
    TIME_SINCE_REST("TIME_SINCE_REST", "TIME_SINCE_REST"),
    TRADED_WITH_VILLAGER("TRADED_WITH_VILLAGER", "TRADED_WITH_VILLAGER"),
    TRAPPED_CHEST_TRIGGERED("TRAPPED_CHEST_TRIGGERED", "TRAPPED_CHEST_TRIGGERED"),
    USE_ITEM("USE_ITEM", null, StatisticType.MATERIAL),
    WALK_ON_WATER_ONE_CM("WALK_ON_WATER_ONE_CM", null),
    WALK_ONE_CM("WALK_ONE_CM", "WALK_ONE_CM"),
    WALK_UNDER_WATER_ONE_CM("WALK_UNDER_WATER_ONE_CM", null);

    private final String bukkitID;
    private final String spongeID;
    private final StatisticType statisticType;

    Statistic(String bukkitID, String spongeID) {
        this(bukkitID, spongeID, StatisticType.NONE);
    }

    Statistic(String bukkitID, String spongeID, StatisticType statisticType) {
        this.bukkitID = bukkitID;
        this.spongeID = spongeID;
        this.statisticType = statisticType;
    }

    public String getBukkitID() {
        return this.bukkitID;
    }

    public String getSpongeID() {
        return this.spongeID;
    }

    public StatisticType getStatisticType() {
        return this.statisticType;
    }

    public enum StatisticType {
        NONE,
        MATERIAL,
        ENTITY
    }
}