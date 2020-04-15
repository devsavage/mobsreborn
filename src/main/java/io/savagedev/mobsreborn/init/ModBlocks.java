package io.savagedev.mobsreborn.init;

/*
 * ModBlocks.java
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
import io.savagedev.mobsreborn.blocks.BaseBlock;
import io.savagedev.mobsreborn.blocks.mobdustsmelter.BlockMobDustSmelter;
import io.savagedev.mobsreborn.items.BaseBlockItem;
import io.savagedev.mobsreborn.reference.ModReference;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModBlocks
{
    public static final List<Supplier<? extends Block>> ENTRIES = new ArrayList<>();

    public static final RegistryObject<BlockMobDustSmelter> mob_dust_smelter = register("mob_dust_smelter", () -> new BlockMobDustSmelter());

    @SubscribeEvent
    public void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        ENTRIES.stream().map(Supplier::get).forEach(registry::register);
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block) {
        return register(name, block, b -> () -> new BaseBlockItem(b.get(), p -> p.group(MobsReborn.modTab)));
    }

    private static <T extends Block> RegistryObject<T> register(String name, Supplier<T> block, Function<RegistryObject<T>, Supplier<? extends BlockItem>> item) {
        ResourceLocation loc = new ResourceLocation(ModReference.mod_id, name);
        ENTRIES.add(() -> block.get().setRegistryName(loc));
        RegistryObject<T> registryObject = RegistryObject.of(loc, ForgeRegistries.BLOCKS);
        ModItems.BLOCK_ENTRIES.add(() -> item.apply(registryObject).get().setRegistryName(loc));

        return registryObject;
    }

    public static <T extends Block> RegistryObject<T> registerNoItem(String name, Supplier<T> block) {
        ResourceLocation loc = new ResourceLocation(ModReference.mod_id, name);
        ENTRIES.add(() -> block.get().setRegistryName(loc));
        return RegistryObject.of(loc, ForgeRegistries.BLOCKS);
    }
}
