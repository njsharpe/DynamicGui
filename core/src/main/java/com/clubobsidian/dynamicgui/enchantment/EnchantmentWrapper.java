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
package com.clubobsidian.dynamicgui.enchantment;

import java.io.Serializable;

public class EnchantmentWrapper implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1056076873542226033L;
    private final String enchant;
    private final int level;

    public EnchantmentWrapper(String enchant, int level) {
        this.enchant = enchant;
        this.level = level;
    }

    public String getEnchant() {
        return this.enchant;
    }

    public int getLevel() {
        return this.level;
    }
}