package io.savagedev.mobsreborn.items;

/*
 * ItemMobMetalItem.java
 * Copyright (C) 2020 Savage - github.com/devsavage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import net.minecraft.util.IStringSerializable;

import java.util.Arrays;
import java.util.Comparator;

public enum ItemMobMetalItem implements IStringSerializable
{
    CREEPER_METAL(0, "creeper_metal"),
    ZOMBIE_METAL(1, "zombie_metal"),
    ENDERMAN_METAL(2, "enderman_metal"),
    SKELETON_METAL(3, "skeleton_metal");

    private static final ItemMobMetalItem[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(ItemMobMetalItem::getId)).toArray((dustId) -> {
        return new ItemMobMetalItem[dustId];
    });

    private final int id;
    private final String metalName;

    private ItemMobMetalItem(int idIn, String metalNameIn) {
        this.id = idIn;
        this.metalName = metalNameIn;
    }

    public int getId() {
        return this.id;
    }

    public String getMetalName() {
        return this.metalName;
    }

    public String toString() {
        return this.metalName;
    }

    @Override
    public String getName() {
        return this.metalName;
    }
}
