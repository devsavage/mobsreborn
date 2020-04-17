package io.savagedev.mobsreborn.crafting.recipe;

/*
 * MobDustSmelterRecipe.java
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.savagedev.mobsreborn.init.ModRecipeSerializers;
import io.savagedev.mobsreborn.util.LogHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class MobDustSmelterRecipe implements ISpecialRecipe
{
    private final ResourceLocation recipeId;
    private final NonNullList<Ingredient> inputs;
    private final ItemStack output;

    public MobDustSmelterRecipe(ResourceLocation resourceLocation, NonNullList<Ingredient> inputs, ItemStack output) {
        this.recipeId = resourceLocation;
        this.inputs = inputs;
        this.output = output;
    }

    @Override
    public ItemStack getCraftingResult(IItemHandler inventory) {
        return this.output.copy();
    }

    @Override
    public boolean canFit(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.output;
    }

    @Override
    public ResourceLocation getId() {
        return this.recipeId;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.mob_dust_smelter;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypes.mob_dust_smelter;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.inputs;
    }

    @Override
    public boolean matches(IItemHandler inventory) {
        ItemStack inputStack = inventory.getStackInSlot(1);

        return this.inputs.get(0).test(inputStack);
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<MobDustSmelterRecipe> {
        @Override
        public MobDustSmelterRecipe read(ResourceLocation recipeId, JsonObject json) {
            NonNullList<Ingredient> inputs = NonNullList.create();

            JsonArray ingredients = JSONUtils.getJsonArray(json, "ingredients");
            for (int i = 0; i < ingredients.size(); i++) {
                inputs.add(Ingredient.deserialize(ingredients.get(i)));
            }

            ItemStack output = ShapedRecipe.deserializeItem(json.getAsJsonObject("result"));

            return new MobDustSmelterRecipe(recipeId, inputs, output);
        }

        @Override
        public MobDustSmelterRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            int size = buffer.readVarInt();

            NonNullList<Ingredient> inputs = NonNullList.withSize(size, Ingredient.EMPTY);
            for (int i = 0; i < size; i++) {
                inputs.set(i, Ingredient.read(buffer));
            }

            ItemStack output = buffer.readItemStack();

            return new MobDustSmelterRecipe(recipeId, inputs, output);
        }

        @Override
        public void write(PacketBuffer buffer, MobDustSmelterRecipe recipe) {
            buffer.writeVarInt(recipe.inputs.size());

            for (Ingredient ingredient : recipe.inputs) {
                ingredient.write(buffer);
            }

            buffer.writeItemStack(recipe.output);
        }
    }
}
