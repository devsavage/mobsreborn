package io.savagedev.mobsreborn.util;

/*
 * BaseItemStackHandler.java
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
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class BaseItemStackHandler extends ItemStackHandler
{
    private final Runnable onContentsChanged;
    private final Map<Integer, Integer> slotSizeMap;
    private BiFunction<Integer, ItemStack, Boolean> slotValidator = null;
    private int maxStackSize = 64;
    private int[] outputSlots = null;

    public BaseItemStackHandler(int size) {
        this(size, null);
    }

    public BaseItemStackHandler(int size, Runnable onContentsChanged) {
        super(size);
        this.onContentsChanged = onContentsChanged;
        this.slotSizeMap = new HashMap<>();
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (this.outputSlots != null && ArrayUtils.contains(this.outputSlots, slot))
            return stack;
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (this.outputSlots != null && !ArrayUtils.contains(this.outputSlots, slot))
            return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.slotSizeMap.containsKey(slot) ? this.slotSizeMap.get(slot) : this.maxStackSize;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return this.slotValidator == null || this.slotValidator.apply(slot, stack);
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (this.onContentsChanged != null)
            this.onContentsChanged.run();
    }

    public ItemStack insertItemSuper(int slot, ItemStack stack, boolean simulate) {
        return super.insertItem(slot, stack, simulate);
    }

    public ItemStack extractItemSuper(int slot, int amount, boolean simulate) {
        return super.extractItem(slot, amount, simulate);
    }

    public NonNullList<ItemStack> getStacks() {
        return this.stacks;
    }

    public int[] getOutputSlots() {
        return this.outputSlots;
    }

    public void setDefaultSlotLimit(int size) {
        this.maxStackSize = size;
    }

    public void addSlotLimit(int slot, int size) {
        this.slotSizeMap.put(slot, size);
    }

    public void setSlotValidator(BiFunction<Integer, ItemStack, Boolean> validator) {
        this.slotValidator = validator;
    }

    public void setOutputSlots(int... slots) {
        this.outputSlots = slots;
    }

    public IInventory toIInventory() {
        return new Inventory(this.stacks.toArray(new ItemStack[0]));
    }
}
