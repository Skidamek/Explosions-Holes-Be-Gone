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

package pl.skidam.explosionsholesbegone.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import pl.skidam.explosionsholesbegone.ExplosionsHolesBeGone;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private final Gson gson;
    private final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("better-explosions.json");
    public static ConfigFields fields;

    public Config() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        load();
    }

    public void load() {
        try {
            Files.createDirectories(configPath.getParent());
            if (Files.exists(configPath)) {
                String json = Files.readString(configPath);
                fields = gson.fromJson(json, ConfigFields.class);
                checkConfigAndSave();
            } else {
                fields = new ConfigFields();
                checkConfigAndSave();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            String json = gson.toJson(fields);
            Files.writeString(configPath, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkConfigAndSave() {
        if (fields.rebuildOneBlockEvery <= 0) {
            fields.rebuildOneBlockEvery = 5; // 5 is default
            ExplosionsHolesBeGone.LOGGER.warn("rebuildOneBlockEvery cannot be less than 1, setting it to {}", fields.rebuildOneBlockEvery);
        }

        if (fields.startRebuildingAfter < 0) {
            fields.startRebuildingAfter = 200; // 200 is default
            ExplosionsHolesBeGone.LOGGER.warn("startRebuildingAfter cannot be less than 0, setting it to {}", fields.startRebuildingAfter);
        }

        save();
    }

    public static class ConfigFields {
        public boolean rebuildCreeperExplosion = true;
        public boolean rebuildTntExplosion = false;
        public boolean rebuildFireballExplosion = true;
        public boolean rebuildWitherSkullExplosion = false;
        public int startRebuildingAfter = 200; // ticks
        public int rebuildOneBlockEvery = 5; // ticks
    }
}
