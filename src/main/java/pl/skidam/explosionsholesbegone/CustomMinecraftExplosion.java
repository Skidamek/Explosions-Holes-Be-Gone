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

package pl.skidam.explosionsholesbegone;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TntBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.EntityExplosionBehavior;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;
import pl.skidam.explosionsholesbegone.data.BlockSave;
import pl.skidam.explosionsholesbegone.data.RebuildExplosion;

//#if MC >= 11900
//$$ import net.minecraft.registry.entry.RegistryEntry;
//$$ import net.minecraft.sound.SoundEvent;
//#endif

/**
 * Edited default minecraft Explosion class
 * See {@link Explosion}
 */

public class CustomMinecraftExplosion extends Explosion {

    private static final ExplosionBehavior DEFAULT_BEHAVIOR = new ExplosionBehavior();
    private final World world;
    private final double x;
    private final double y;
    private final double z;
    @Nullable
    private final Entity entity;
    private final float power;
    private final DamageSource damageSource;
    private final ExplosionBehavior behavior;
    private final ObjectArrayList<BlockPos> affectedBlocks = new ObjectArrayList<>();
    private final Map<PlayerEntity, Vec3d> affectedPlayers = Maps.newHashMap();

    public CustomMinecraftExplosion(
            World world,
            @Nullable Entity entity,
            @Nullable DamageSource damageSource,
            @Nullable ExplosionBehavior behavior,
            double x,
            double y,
            double z,
            float power,
            boolean createFire,
            DestructionType destructionType
    ) {
        super(world, entity, damageSource, behavior, x, y, z, power, createFire, destructionType);
        this.world = world;
        this.entity = entity;
        this.power = power;
        this.x = x;
        this.y = y;
        this.z = z;
        this.damageSource = damageSource == null ? explosion(this) : damageSource;
        this.behavior = behavior == null ? this.chooseBehavior(entity) : behavior;
    }

    private DamageSource explosion(@Nullable Explosion explosion) {
//#if MC < 11900
        return DamageSource.explosion(this);
//#else
//$$    return world.getDamageSources().explosion(this);
//#endif
    }

    private ExplosionBehavior chooseBehavior(@Nullable Entity entity) {
        return entity == null ? DEFAULT_BEHAVIOR : new EntityExplosionBehavior(entity);
    }

    public static float getExposure(Vec3d source, Entity entity) {
        Box box = entity.getBoundingBox();
        double d = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
        double e = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
        double f = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);
        double g = (1.0 - Math.floor(1.0 / d) * d) / 2.0;
        double h = (1.0 - Math.floor(1.0 / f) * f) / 2.0;
        if (!(d < 0.0) && !(e < 0.0) && !(f < 0.0)) {
            int i = 0;
            int j = 0;

            for(double k = 0.0; k <= 1.0; k += d) {
                for(double l = 0.0; l <= 1.0; l += e) {
                    for(double m = 0.0; m <= 1.0; m += f) {
                        double n = MathHelper.lerp(k, box.minX, box.maxX);
                        double o = MathHelper.lerp(l, box.minY, box.maxY);
                        double p = MathHelper.lerp(m, box.minZ, box.maxZ);
                        Vec3d vec3d = new Vec3d(n + g, o, p + h);
                        if (entity.getWorld().raycast(new RaycastContext(vec3d, source, ShapeType.COLLIDER, FluidHandling.NONE, entity)).getType() == Type.MISS) {
                            ++i;
                        }

                        ++j;
                    }
                }
            }

            return (float)i / (float)j;
        } else {
            return 0.0F;
        }
    }

    public void collectBlocksAndDamageEntities() {
//#if MC < 11900
        var explosionPos = new BlockPos(this.x, this.y, this.z);
//#else
//$$    var explosionPos = new Vec3d(this.x, this.y, this.z);
//#endif
        this.world.emitGameEvent(this.entity, GameEvent.EXPLODE, explosionPos);
        Set<BlockPos> set = Sets.newHashSet();

        for(int j = 0; j < 16; ++j) {
            for(int k = 0; k < 16; ++k) {
                for(int l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d = (float)j / 15.0F * 2.0F - 1.0F;
                        double e = (float)k / 15.0F * 2.0F - 1.0F;
                        double f = (float)l / 15.0F * 2.0F - 1.0F;
                        double g = Math.sqrt(d * d + e * e + f * f);
                        d /= g;
                        e /= g;
                        f /= g;
                        float h = this.power * (0.7F + this.world.random.nextFloat() * 0.6F);
                        double m = this.x;
                        double n = this.y;
                        double o = this.z;

                        for(float p = 0.3F; h > 0.0F; h -= 0.22500001F) {
                            BlockPos blockPos = new BlockPos((int) m, (int) n, (int) o);
                            BlockState blockState = this.world.getBlockState(blockPos);
                            FluidState fluidState = this.world.getFluidState(blockPos);
                            if (!this.world.isInBuildLimit(blockPos)) {
                                break;
                            }

                            Optional<Float> optional = this.behavior.getBlastResistance(this, this.world, blockPos, blockState, fluidState);
                            if (optional.isPresent()) {
                                h -= (optional.get() + 0.3F) * 0.3F;
                            }

                            if (h > 0.0F) {
                                set.add(blockPos);
                            }

                            m += d * 0.3F;
                            n += e * 0.3F;
                            o += f * 0.3F;
                        }
                    }
                }
            }
        }

        this.affectedBlocks.addAll(set);
        float q = this.power * 2.0F;
        int k = MathHelper.floor(this.x - (double)q - 1.0);
        int l = MathHelper.floor(this.x + (double)q + 1.0);
        int r = MathHelper.floor(this.y - (double)q - 1.0);
        int s = MathHelper.floor(this.y + (double)q + 1.0);
        int t = MathHelper.floor(this.z - (double)q - 1.0);
        int u = MathHelper.floor(this.z + (double)q + 1.0);
        List<Entity> list = this.world.getOtherEntities(this.entity, new Box(k, r, t, l, s, u));
        Vec3d vec3d = new Vec3d(this.x, this.y, this.z);

        for (Entity entity : list) {
            if (!entity.isImmuneToExplosion()) {
                double w = Math.sqrt(entity.squaredDistanceTo(vec3d)) / (double) q;
                if (w <= 1.0) {
                    double x = entity.getX() - this.x;
                    double y = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - this.y;
                    double z = entity.getZ() - this.z;
                    double aa = Math.sqrt(x * x + y * y + z * z);
                    if (aa != 0.0) {
                        x /= aa;
                        y /= aa;
                        z /= aa;
                        double ab = getExposure(vec3d, entity);
                        double ac = (1.0 - w) * ab;
                        entity.damage(this.getDamageSource(), (float) ((int) ((ac * ac + ac) / 2.0 * 7.0 * (double) q + 1.0)));
                        double ad = ac;
                        if (entity instanceof LivingEntity) {
                            ad = ProtectionEnchantment.transformExplosionKnockback((LivingEntity) entity, ac);
                        }

                        entity.setVelocity(entity.getVelocity().add(x * ad, y * ad, z * ad));
                        if (entity instanceof PlayerEntity playerEntity
                                && !playerEntity.isSpectator()
                                && (!playerEntity.isCreative() || !playerEntity.getAbilities().flying)) {
                            this.affectedPlayers.put(playerEntity, new Vec3d(x * ac, y * ac, z * ac));
                        }
                    }
                }
            }
        }
    }

    public void affectWorld(boolean particles) {
        // explode sound
        this.world.getPlayers().forEach(player -> {
            if (player.squaredDistanceTo(x, y, z) < 256) { // 256 == (box) 16^2
                double e = x - player.getX();
                double f = y - player.getY();
                double g = z - player.getZ();
                double h = e * e + f * f + g * g;
                double k = Math.sqrt(h);
                Vec3d vec3d = new Vec3d(player.getX() + e / k * 2.0, player.getY() + f / k * 2.0, player.getZ() + g / k * 2.0);
                long l = this.world.getRandom().nextLong();
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;

//#if MC < 11900
                serverPlayerEntity.networkHandler.sendPacket(new PlaySoundS2CPacket(SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 4.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F));
//#else
//$$            RegistryEntry<SoundEvent> sound = RegistryEntry.of(SoundEvent.of(SoundEvents.ENTITY_GENERIC_EXPLODE.getId()));
//$$            serverPlayerEntity.networkHandler.sendPacket(new PlaySoundS2CPacket(sound, SoundCategory.BLOCKS, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 4.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F, l));
//#endif
            }
        });

        if (particles) {
            ParticleEffect particleEffect;

            if (!(this.power < 2.0F)) {
                particleEffect = ParticleTypes.EXPLOSION_EMITTER;
            } else {
                particleEffect = ParticleTypes.EXPLOSION;
            }

            for (ServerPlayerEntity serverPlayerEntity : this.world.getServer().getPlayerManager().getPlayerList()) {
                this.world.getServer().getWorld(this.world.getRegistryKey()).spawnParticles(serverPlayerEntity, particleEffect, false, this.x, this.y, this.z, 0, 0.0, 0.0, 0.0, (double)0.0F);
            }
        }

        Collections.shuffle(this.affectedBlocks, new Random());
        List<BlockSave> blockSaveList = new ObjectArrayList<>();

        for (BlockPos blockPos : getAffectedBlocks()) {
            BlockState blockState = this.world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (!blockState.isAir()) {
                BlockEntity blockEntity = this.world.getBlockEntity(blockPos);

                // read nbt data and save it
                NbtCompound nbtCompound = new NbtCompound();
                if (blockEntity != null) {
                    blockEntity.writeNbt(nbtCompound);
                }

                if (!(blockState.getBlock() instanceof TntBlock)) {
                    blockSaveList.add(new BlockSave(blockPos, blockState, nbtCompound));
                }

                // we reset nbt data to prevent dropping items etc.
                if (blockEntity != null) {
                    blockEntity.readNbt(new NbtCompound());
                }

                block.onDestroyedByExplosion(this.world, blockPos, this);
                this.world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
            }
        }

        // Rebuild explosion stuff
        if (getAffectedBlocks().size() > 0 && blockSaveList.size() > 0) {
            RebuildExplosion rebuildExplosion = new RebuildExplosion(false, 0, blockSaveList, this.world.getRegistryKey());
            ExplosionsHolesBeGone.explosions.put(ExplosionsHolesBeGone.explosions.size(), rebuildExplosion);
        }
    }


    public DamageSource getDamageSource() {
        return this.damageSource;
    }

    public Map<PlayerEntity, Vec3d> getAffectedPlayers() {
        return this.affectedPlayers;
    }

    public List<BlockPos> getAffectedBlocks() {
        return this.affectedBlocks;
    }
}