package io.savagedev.mobsreborn.init;

/*
 * ModItems.java
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

import io.savagedev.mobsreborn.MobsReborn;
import io.savagedev.mobsreborn.items.*;
import io.savagedev.mobsreborn.reference.ModNames;
import io.savagedev.mobsreborn.reference.ModReference;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModItems
{
    public static final List<Supplier<? extends Item>> BLOCK_ENTRIES = new ArrayList<>();
    public static final List<Supplier<? extends Item>> ENTRIES = new ArrayList<>();

    public static final RegistryObject<ItemMobDust> creeper_dust = registerDust(ModNames.Items.creeper_dust, ItemMobDustItem.CREEPER_DUST);
    public static final RegistryObject<ItemMobDust> zombie_dust = registerDust(ModNames.Items.zombie_dust, ItemMobDustItem.ZOMBIE_DUST);
    public static final RegistryObject<ItemMobDust> skeleton_dust = registerDust(ModNames.Items.skeleton_dust, ItemMobDustItem.SKELETON_DUST);
    public static final RegistryObject<ItemMobDust> enderman_dust = registerDust(ModNames.Items.enderman_dust, ItemMobDustItem.ENDERMAN_DUST);

    public static final RegistryObject<ItemMobMetal> creeper_metal = registerMobMetal(ModNames.Items.creeper_metal, ItemMobMetalItem.CREEPER_METAL);
    public static final RegistryObject<ItemMobMetal> zombie_metal = registerMobMetal(ModNames.Items.zombie_metal, ItemMobMetalItem.ZOMBIE_METAL);
    public static final RegistryObject<ItemMobMetal> skeleton_metal = registerMobMetal(ModNames.Items.skeleton_metal, ItemMobMetalItem.SKELETON_METAL);
    public static final RegistryObject<ItemMobMetal> enderman_metal = registerMobMetal(ModNames.Items.enderman_metal, ItemMobMetalItem.ENDERMAN_METAL);

    @SubscribeEvent
    public void onRegisterItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        BLOCK_ENTRIES.stream().map(Supplier::get).forEach(registry::register);
        ENTRIES.stream().map(Supplier::get).forEach(registry::register);
    }

    private static <T extends Item> RegistryObject<T> register(String name) {
        return register(name, () -> new BaseItem(p -> p.group(MobsReborn.modTab)));
    }

    private static <T extends Item> RegistryObject<T> registerDust(String name, ItemMobDustItem itemIn) {
        return register(name, () -> new ItemMobDust(itemIn, p -> p.group(MobsReborn.modTab)));
    }

    private static <T extends Item> RegistryObject<T> registerMobMetal(String name, ItemMobMetalItem itemIn) {
        return register(name, () -> new ItemMobMetal(itemIn, p -> p.group(MobsReborn.modTab)));
    }

    private static <T extends Item> RegistryObject<T> register(String name, Supplier<? extends Item> item) {
        ResourceLocation loc = new ResourceLocation(ModReference.mod_id, name);
        ENTRIES.add(() -> item.get().setRegistryName(loc));
        return RegistryObject.of(loc, ForgeRegistries.ITEMS);
    }
}
