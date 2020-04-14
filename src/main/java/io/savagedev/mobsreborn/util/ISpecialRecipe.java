package io.savagedev.mobsreborn.util;

/*
 * ISpecialRecipe.java
 * Copyright blakebr0
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

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public interface ISpecialRecipe extends IRecipe<IInventory>
{
    @Override
    default ItemStack getCraftingResult(IInventory inv) {
        return this.getCraftingResult(new InvWrapper(inv));
    }

    @Override
    default boolean matches(IInventory inv, World world) {
        return this.matches(new InvWrapper(inv));
    }

    @Override
    default NonNullList<ItemStack> getRemainingItems(IInventory inv) {
        return this.getRemainingItems(new InvWrapper(inv));
    }

    ItemStack getCraftingResult(IItemHandler inventory);

    default boolean matches(IItemHandler inventory) {
        return this.matches(inventory, 0, inventory.getSlots());
    }

    default boolean matches(IItemHandler inventory, int startIndex, int endIndex) {
        NonNullList<ItemStack> inputs = NonNullList.create();
        for (int i = startIndex; i < endIndex; i++) {
            inputs.add(inventory.getStackInSlot(i));
        }

        return RecipeMatcher.findMatches(inputs, this.getIngredients()) != null;
    }

    default NonNullList<ItemStack> getRemainingItems(IItemHandler inventory) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(inventory.getSlots(), ItemStack.EMPTY);
        for (int i = 0; i < remaining.size(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.hasContainerItem())
                remaining.set(i, stack.getContainerItem());
        }

        return remaining;
    }
}
