/*
 * This file is part of the Explosions Holes Be Gone project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Skidam and contributors
 *
 * Explosions Holes Be Gone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Explosions Holes Be Gone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Explosions Holes Be Gone.  If not, see <https://www.gnu.org/licenses/>.
 */

package pl.skidam.explosionsholesbegone.data;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class BlockSave {
    private BlockPos pos;
    private BlockState state;
    private NbtCompound nbt;

    public BlockSave(BlockPos blockPos, BlockState blockState, NbtCompound nbtCompound) {
        pos = blockPos;
        state = blockState;
        nbt = nbtCompound;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public BlockState getState() {
        return state;
    }

    public void setState(BlockState state) {
        this.state = state;
    }

    public NbtCompound getNbt() {
        return nbt;
    }

    public void setNbt(NbtCompound nbt) {
        this.nbt = nbt;
    }
}