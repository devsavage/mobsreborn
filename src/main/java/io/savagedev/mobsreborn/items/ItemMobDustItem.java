package io.savagedev.mobsreborn.items;

/*
 * ItemMobDustItem.java
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

public enum ItemMobDustItem implements IStringSerializable
{
    CREEPER_DUST(0, "creeper_dust"),
    ZOMBIE_DUST(1, "zombie_dust"),
    ENDERMAN_DUST(2, "enderman_dust"),
    SKELETON_DUST(3, "skeleton_dust");

    private static final ItemMobDustItem[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(ItemMobDustItem::getId)).toArray((dustId) -> {
        return new ItemMobDustItem[dustId];
    });

    private final int id;
    private final String dustName;

    private ItemMobDustItem(int idIn, String dustNameIn) {
        this.id = idIn;
        this.dustName = dustNameIn;
    }

    public int getId() {
        return this.id;
    }

    public String getDustName() {
        return this.dustName;
    }

    public String toString() {
        return this.dustName;
    }

    @Override
    public String getName() {
        return this.dustName;
    }
}
