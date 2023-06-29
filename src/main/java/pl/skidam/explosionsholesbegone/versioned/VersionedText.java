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

package pl.skidam.explosionsholesbegone.versioned;

import net.minecraft.text.*;

public class VersionedText {

    //#if MC < 11902
//$$
    public static MutableText translatable(String key, Object... args) {
        return new TranslatableText(key, args);
    }

    public static MutableText literal(String string) {
        return new LiteralText(string);
    }

//#else
//$$
//$$public static MutableText translatable(String key, Object... args) {
//$$    return Text.translatable(key, args);
//$$}
//$$
//$$public static MutableText literal(String string) {
//$$    return Text.literal(string);
//$$}
//$$
//#endif
}
