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
package com.clubobsidian.dynamicgui.function.impl.meta;

import com.clubobsidian.dynamicgui.DynamicGui;
import com.clubobsidian.dynamicgui.entity.PlayerWrapper;
import com.clubobsidian.dynamicgui.function.Function;
import com.clubobsidian.dynamicgui.gui.FunctionOwner;
import com.clubobsidian.dynamicgui.gui.Gui;
import com.clubobsidian.dynamicgui.gui.Slot;
import com.clubobsidian.dynamicgui.gui.property.MetadataHolder;

public class SetMetadataFunction extends Function {


    /**
     *
     */
    private static final long serialVersionUID = -2376716466726111306L;

    public SetMetadataFunction(String name) {
        super(name);
    }

    //SetMetadata (index/gui),key,value

    @Override
    public boolean function(PlayerWrapper<?> playerWrapper) {
        if(this.getData() == null) {
            return false;
        } else if(!this.getData().contains(",")) {
            return false;
        }

        String[] split = this.getData().split(",");
        FunctionOwner owner = this.getOwner();
        MetadataHolder holder = null;
        String first = split[0];
        Gui gui = null;

        if(owner instanceof Gui && split.length >= 2) {
            if(split.length == 2) {
                holder = (MetadataHolder) owner;
            } else {
                gui = (Gui) owner;
            }
        } else if(owner instanceof Slot && split.length >= 2) {
            if(first.equals("gui")) {
                holder = ((Slot) this.getOwner()).getOwner();
            } else if(split.length == 2) {
                holder = (MetadataHolder) this.getOwner();
            } else if(split.length == 3) {
                gui = ((Slot) owner).getOwner();
            }
        }

        //Check for slots
        if(holder == null) {
            int index = -1;
            try {
                index = Integer.valueOf(first);
            } catch(Exception ex) {
                DynamicGui.get().getLogger().error("Invalid index " + first + " in HasMetadata function");
                return false;
            }
            for(Slot s : gui.getSlots()) {
                if(s.getIndex() == index) {
                    holder = s;
                    break;
                }
            }
        }

        if(holder != null && split.length >= 2) {
            String key = null;
            String value = null;
            if(split.length == 2) {
                key = split[0];
                value = split[1];
            } else if(split.length == 3) {
                key = split[1];
                value = split[2];
            }

            if(key != null) {
                holder.getMetadata().put(key, value);
                return true;
            }
        }

        return false;
    }
}