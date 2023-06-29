/*
 * This file is part of the Explosions Holes Be Gone project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
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

package pl.skidam.explosionsholesbegone;


import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import pl.skidam.explosionsholesbegone.config.Config;
import pl.skidam.explosionsholesbegone.versioned.VersionedCommandSource;
import pl.skidam.explosionsholesbegone.versioned.VersionedText;

//#if MC < 11902
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
//#else
//$$ import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
//#endif

public class Commands {

    public static void register() {


//#if MC < 11902
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
//#else
//$$    CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> dispatcher.register(
//#endif
                
                literal("explosions-holes-be-gone")
                        .executes(Commands::about)

                        .then(literal("reload")
                                .requires((source) -> source.hasPermissionLevel(3))
                                .executes(Commands::reload)
                        )
                        
                        .then(literal("creeper")
                                .requires((source) -> source.hasPermissionLevel(3))
                                .executes(Commands::creeper)
                        )
                        
                        .then(literal("fireball")
                                .requires((source) -> source.hasPermissionLevel(3))
                                .executes(Commands::fireball)
                        )
                        
                        .then(literal("tnt")
                                .requires((source) -> source.hasPermissionLevel(3))
                                .executes(Commands::tnt)   
                        )
                
                        .then(literal("wither-skull")
                                .requires((source) -> source.hasPermissionLevel(3))
                                .executes(Commands::wither)
                        )
        ));
    }

    private static int reload(CommandContext<ServerCommandSource> context) {

        ExplosionsHolesBeGone.config.load();
        VersionedCommandSource.sendFeedback(context, VersionedText.literal("Reloaded config"), true);
        about(context);

        return Command.SINGLE_SUCCESS;
    }

    private static int wither(CommandContext<ServerCommandSource> context) {

        Config.fields.rebuildWitherSkullExplosion = !Config.fields.rebuildWitherSkullExplosion;
        ExplosionsHolesBeGone.config.save();
        MutableText text = VersionedText.literal("Turned " + (Config.fields.rebuildWitherSkullExplosion ? "on" : "off") + " rebuild Wither Skull explosion");

        Formatting formatting = Config.fields.rebuildWitherSkullExplosion ? Formatting.GREEN : Formatting.RED;

        VersionedCommandSource.sendFeedback(context, text.formatted(formatting), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int tnt(CommandContext<ServerCommandSource> context) {

        Config.fields.rebuildTntExplosion = !Config.fields.rebuildTntExplosion;
        ExplosionsHolesBeGone.config.save();
        MutableText text = VersionedText.literal("Turned " + (Config.fields.rebuildTntExplosion ? "on" : "off") + " rebuild TNT explosion");

        Formatting formatting = Config.fields.rebuildTntExplosion ? Formatting.GREEN : Formatting.RED;

        VersionedCommandSource.sendFeedback(context, text.formatted(formatting), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int fireball(CommandContext<ServerCommandSource> context) {
        // copy from creeper
        Config.fields.rebuildFireballExplosion = !Config.fields.rebuildFireballExplosion;
        ExplosionsHolesBeGone.config.save();
        MutableText text = VersionedText.literal("Turned " + (Config.fields.rebuildFireballExplosion ? "on" : "off") + " rebuild Fireball explosion");

        Formatting formatting = Config.fields.rebuildFireballExplosion ? Formatting.GREEN : Formatting.RED;

        VersionedCommandSource.sendFeedback(context, text.formatted(formatting), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int creeper(CommandContext<ServerCommandSource> context) {

        Config.fields.rebuildCreeperExplosion = !Config.fields.rebuildCreeperExplosion;
        ExplosionsHolesBeGone.config.save();
        MutableText text = VersionedText.literal("Turned " + (Config.fields.rebuildCreeperExplosion ? "on" : "off") + " rebuild Creeper explosion");

        Formatting formatting = Config.fields.rebuildCreeperExplosion ? Formatting.GREEN : Formatting.RED;

        VersionedCommandSource.sendFeedback(context, text.formatted(formatting), true);

        return Command.SINGLE_SUCCESS;
    }

    private static int about(CommandContext<ServerCommandSource> context) {

        VersionedCommandSource.sendFeedback(context, VersionedText.literal("Explosions Holes Be Gone")
                .formatted(Formatting.GREEN)
                .append(VersionedText.literal(" - v" + ExplosionsHolesBeGone.VERSION)
                        .formatted(Formatting.WHITE)
                ), false);
        VersionedCommandSource.sendFeedback(context, VersionedText.literal("Turn off/on rebuild explosions for:")
                .formatted(Formatting.GREEN),
                false);

        // send every explosion type with its current status of on/off
        VersionedCommandSource.sendFeedback(context, VersionedText.literal("Creeper: ")
                .formatted(Formatting.WHITE)
                .append(VersionedText.literal(Config.fields.rebuildCreeperExplosion ? "on" : "off")
                        .formatted(Config.fields.rebuildCreeperExplosion ? Formatting.GREEN : Formatting.RED)
                        .styled((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/explosions-holes-be-gone creeper")))
                        .styled((style) -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, VersionedText.literal(Config.fields.rebuildCreeperExplosion ? "Turn on" : "Turn off").formatted(Formatting.GRAY))))
                ), false);
        VersionedCommandSource.sendFeedback(context, VersionedText.literal("Fireball: ")
                .formatted(Formatting.WHITE)
                .append(VersionedText.literal(Config.fields.rebuildFireballExplosion ? "on" : "off")
                        .formatted(Config.fields.rebuildFireballExplosion ? Formatting.GREEN : Formatting.RED)
                        .styled((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/explosions-holes-be-gone fireball")))
                        .styled((style) -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, VersionedText.literal(Config.fields.rebuildFireballExplosion ? "Turn on" : "Turn off").formatted(Formatting.GRAY))))
                ), false);
        VersionedCommandSource.sendFeedback(context, VersionedText.literal("TNT: ")
                .formatted(Formatting.WHITE)
                .append(VersionedText.literal(Config.fields.rebuildTntExplosion ? "on" : "off")
                        .formatted(Config.fields.rebuildTntExplosion ? Formatting.GREEN : Formatting.RED)
                        .styled((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/explosions-holes-be-gone tnt")))
                        .styled((style) -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, VersionedText.literal(Config.fields.rebuildTntExplosion ? "Turn on" : "Turn off").formatted(Formatting.GRAY))))
                ), false);
        VersionedCommandSource.sendFeedback(context, VersionedText.literal("Wither Skull: ")
                .formatted(Formatting.WHITE)
                .append(VersionedText.literal(Config.fields.rebuildWitherSkullExplosion ? "on" : "off")
                        .formatted(Config.fields.rebuildWitherSkullExplosion ? Formatting.GREEN : Formatting.RED)
                        .styled((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/explosions-holes-be-gone wither-skull")))
                        .styled((style) -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, VersionedText.literal(Config.fields.rebuildWitherSkullExplosion ? "Turn on" : "Turn off").formatted(Formatting.GRAY))))
                ), false);

        return Command.SINGLE_SUCCESS;
    }
}
