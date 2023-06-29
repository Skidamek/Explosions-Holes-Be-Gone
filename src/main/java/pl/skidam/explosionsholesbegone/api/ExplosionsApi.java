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

package pl.skidam.explosionsholesbegone.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;
import pl.skidam.explosionsholesbegone.CustomMinecraftExplosion;

/**
 * This class contains methods which are almost the same as the original ones from {@link World}.
 * The only difference is that they accept {@link World} as the first parameter and don't DestructionType as a last.
 * Its purpose is to make re-buildable explosions easily just by calling this method.
 */

public class ExplosionsApi {
    
    // Our custom createExplosion method.
    public static CustomMinecraftExplosion createExplosion(
            World world, // This is the only difference between this method and the original one.
            @Nullable Entity entity, 
            double x, 
            double y, 
            double z, 
            float power
    ) {
        return createExplosion(world, entity, null, null, x, y, z, power, false);
    }

    public static CustomMinecraftExplosion createExplosion(
            World world, // This is the only difference between this method and the original one.
            @Nullable Entity entity, 
            double x, 
            double y, 
            double z, 
            float power, 
            boolean createFire
    ) {
        return createExplosion(world, entity, null, null, x, y, z, power, createFire);
    }

    public static CustomMinecraftExplosion createExplosion(
            World world, // This is the only difference between this method and the original one.
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionBehavior behavior,
            Vec3d pos,
            float power,
            boolean createFire
    ) {
        return createExplosion(world, entity, damageSource, behavior, pos.getX(), pos.getY(), pos.getZ(), power, createFire);
    }

    public static CustomMinecraftExplosion createExplosion(
            World world, // This is the only difference between this method and the original one.
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionBehavior behavior,
            double x,
            double y,
            double z,
            float power,
            boolean createFire
    ) {
        return createExplosion(world, entity, damageSource, behavior, x, y, z, power, createFire, true);
    }

    public static CustomMinecraftExplosion createExplosion(
            World world, // This is the only difference between this method and the original one.
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionBehavior behavior,
            double x,
            double y,
            double z,
            float power,
            boolean createFire,
            boolean particles
    ) {
        CustomMinecraftExplosion.DestructionType destructionType = CustomMinecraftExplosion.DestructionType.DESTROY;
        CustomMinecraftExplosion explosion = new CustomMinecraftExplosion(world, entity, damageSource, behavior, x, y, z, power, createFire, destructionType);
        explosion.collectBlocksAndDamageEntities();
        explosion.affectWorld(particles);
        return explosion;
    }
}
