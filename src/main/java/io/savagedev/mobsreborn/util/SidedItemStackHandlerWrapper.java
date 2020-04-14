package io.savagedev.mobsreborn.util;

/*
 * SidedItemStackHandlerWrapper.java
 * Copyright (C) blakebr0
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

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.function.BiFunction;

public class SidedItemStackHandlerWrapper implements IItemHandlerModifiable {
    private final BaseItemStackHandler inventory;
    private final Direction direction;
    private final TriFunction<Integer, ItemStack, Direction, Boolean> canInsert;
    private final BiFunction<Integer, Direction, Boolean> canExtract;

    public SidedItemStackHandlerWrapper(BaseItemStackHandler inventory, Direction direction, TriFunction<Integer, ItemStack, Direction, Boolean> canInsert, BiFunction<Integer, Direction, Boolean> canExtract) {
        this.inventory = inventory;
        this.direction = direction;
        this.canInsert = canInsert;
        this.canExtract = canExtract;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        this.inventory.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return this.inventory.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!this.isItemValid(slot, stack))
            return stack;

        return this.inventory.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (this.canExtract != null && !this.canExtract.apply(slot, this.direction))
            return ItemStack.EMPTY;

        return this.inventory.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.inventory.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return this.canInsert == null || this.canInsert.apply(slot, stack, this.direction);
    }

    @SuppressWarnings("unchecked")
    public static LazyOptional<IItemHandlerModifiable>[] create(BaseItemStackHandler inv, Direction[] sides, TriFunction<Integer, ItemStack, Direction, Boolean> canInsert, BiFunction<Integer, Direction, Boolean> canExtract) {
        LazyOptional<IItemHandlerModifiable>[] ret = new LazyOptional[sides.length];
        for (int x = 0; x < sides.length; x++) {
            final Direction side = sides[x];
            ret[x] = LazyOptional.of(() -> new SidedItemStackHandlerWrapper(inv, side, canInsert, canExtract));
        }

        return ret;
    }
}

