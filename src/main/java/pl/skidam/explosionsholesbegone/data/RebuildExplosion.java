/*
 * This file is part of the TemplateMod project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TemplateMod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TemplateMod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TemplateMod.  If not, see <https://www.gnu.org/licenses/>.
 */

package pl.skidam.explosionsholesbegone.data;

import net.minecraft.world.World;

import java.util.List;

//#if MC < 11900
import net.minecraft.util.registry.RegistryKey;
//#else
//$$import net.minecraft.registry.RegistryKey;
//#endif


public class RebuildExplosion {
    private boolean building;
    private int ticks;
    private List<BlockSave> blockSaves;
    private final RegistryKey<World> world;

    public RebuildExplosion(boolean building, int ticks, List<BlockSave> blockSaves, RegistryKey<World> world) {
        this.building = building;
        this.ticks = ticks;
        this.blockSaves = blockSaves;
        this.world = world;
    }

    public boolean isReBuilding() {
        return building;
    }

    public int getTicks() {
        return ticks;
    }

    public List<BlockSave> getBlockSaves() {
        return blockSaves;
    }
    public void setBlockSaves(List<BlockSave> blockSaves) {
        this.blockSaves = blockSaves;
    }

    public RegistryKey<World> getWorld() {
        return world;
    }

    public void setReBuilding(boolean building) {
        this.building = building;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }
}