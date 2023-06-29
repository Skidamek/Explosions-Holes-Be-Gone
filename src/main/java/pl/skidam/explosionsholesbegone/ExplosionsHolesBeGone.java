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

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.skidam.explosionsholesbegone.config.Config;
import pl.skidam.explosionsholesbegone.data.BlockSave;
import pl.skidam.explosionsholesbegone.data.RebuildExplosion;

import java.util.*;

//#if MC >= 11900
//$$ import net.minecraft.registry.entry.RegistryEntry;
//$$ import net.minecraft.sound.SoundEvent;
//#endif

//#if MC < 11900
import net.minecraft.util.registry.RegistryKey;
//#else
//$$import net.minecraft.registry.RegistryKey;
//#endif


public class ExplosionsHolesBeGone implements ModInitializer {
	public static final String MOD_ID = "explosions-holes-be-gone";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static Map<Integer, RebuildExplosion> explosions = Collections.synchronizedMap(new HashMap<>());
	public static String VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).isPresent() ? FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata().getVersion().getFriendlyString() : null;
	public static Config config;

	@Override
	public void onInitialize() {
		config = new Config();
		Commands.register();
		LOGGER.info("Explosions Holes Be Gone initialized");
	}

	public static void rebuildExplosion(MinecraftServer server) {
		if (explosions.size() > 0) {

			// loop of explosions maps, we need to use iterator because we are removing elements from a map
			Iterator<Integer> explosionsIterator = explosions.keySet().iterator();
			while (explosionsIterator.hasNext()) {
				int i = explosionsIterator.next();

				int tick = explosions.get(i).getTicks() + 1;
				explosions.get(i).setTicks(tick);

				// if the explosion is reversing, then continue
				if (explosions.get(i).isReBuilding()) {

					if (tick % Config.fields.rebuildOneBlockEvery != 0) { // by default every 5 tick, rebuilding one block
						continue;
					}

					List<BlockSave> blockSaves = explosions.get(i).getBlockSaves();
					Set<BlockPos> affectedBlocks = new HashSet<>();
					for (BlockSave blockSave : blockSaves) {
						affectedBlocks.add(blockSave.getPos());
					}
					RegistryKey<World> worldKey = explosions.get(i).getWorld();

					// remove explosion from map if it's empty
					if (blockSaves.size() == 0 || worldKey == null) {
						explosionsIterator.remove();
						continue;
					}

					ServerWorld world = server.getWorld(worldKey);
					if (world == null) {
						explosionsIterator.remove();
						continue;
					}

					// get lowest block (y) from affected blocks
					BlockPos blockPos = Collections.min(affectedBlocks, Comparator.comparingInt(BlockPos::getY));
					BlockSave blockSaveOfPos = blockSaves.stream()
							.filter(blockSave -> blockSave.getPos().equals(blockPos))
							.toList().get(0);

					BlockState blockState = blockSaveOfPos.getState();

					if (blockState != null) {

						int x = blockPos.getX();
						int y = blockPos.getY();
						int z = blockPos.getZ();

						List<Entity> entities = world.getOtherEntities(null, new Box(x+1,y+5,z+1,x-1, y,z-1));

						// don't set block if entity is near, but drop item
						if (entities.stream().anyMatch(entity ->
								entity instanceof LivingEntity &&
										entity.getBlockPos().getX() == x &&
										entity.getBlockPos().getY() <= y &&
										entity.getBlockPos().getZ() == z)) {

							Block.dropStacks(blockState, world, blockPos, null, null, ItemStack.EMPTY);
							blockSaves.remove(blockSaveOfPos);

							explosions.get(i).setBlockSaves(blockSaves);
							world.getProfiler().pop();
							break;
						}

						// rebuilding sound
						// check if player is near
						world.getPlayers().forEach(player -> {
							if (player.squaredDistanceTo(x, y, z) < 256) { // 256 == (box) 16^2
								double e = x - player.getX();
								double f = y - player.getY();
								double g = z - player.getZ();
								double h = e * e + f * f + g * g;
								double k = Math.sqrt(h);
								Vec3d vec3d = new Vec3d(player.getX() + e / k * 2.0, player.getY() + f / k * 2.0, player.getZ() + g / k * 2.0);
								long l = world.getRandom().nextLong();

//#if MC < 11900
								player.networkHandler.sendPacket(new PlaySoundS2CPacket(SoundEvents.BLOCK_BUBBLE_COLUMN_BUBBLE_POP, SoundCategory.BLOCKS, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 2.0F, 1.0F));
//#else
//$$            				RegistryEntry<SoundEvent> sound = RegistryEntry.of(SoundEvent.of(SoundEvents.BLOCK_BUBBLE_COLUMN_BUBBLE_POP.getId()));
//$$            				player.networkHandler.sendPacket(new PlaySoundS2CPacket(sound, SoundCategory.BLOCKS, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 2.0F, 1.0F, l));
//#endif

							}
						});

						// drop item if block is not air to don't lose items
						BlockState currentBlockState = world.getBlockState(blockPos);
						if (!currentBlockState.isAir()) {
							Block.dropStacks(blockState, world, blockPos, null, null, ItemStack.EMPTY);
						} else {
							// if currentBlock is some fluid, remove it
							FluidState fluidState = currentBlockState.getFluidState();
							if (fluidState != null) {
								world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
							}

							// set block with nbt if possible
							world.setBlockState(blockPos, blockState, 3);
							BlockEntity blockEntity = world.getBlockEntity(blockPos);
							if (blockEntity != null) {
								blockEntity.readNbt(blockSaveOfPos.getNbt());
								blockEntity.markDirty();
							}
						}
					} else {
						LOGGER.error("BlockState is null for blockPos: " + blockPos);
					}

					// remove block from lists
					blockSaves.remove(blockSaveOfPos);
					explosions.get(i).setBlockSaves(blockSaves);
					continue;
				}

				// By default, if tick is 200 (10 sec after boom), then start rebuilding explosion
				if (tick >= Config.fields.startRebuildingAfter) {
					explosions.get(i).setReBuilding(true);
				}
			}
		}
	}
}