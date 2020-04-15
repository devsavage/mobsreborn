package io.savagedev.mobsreborn.blocks.mobdustsmelter;

/*
 * ContainerMobDustSmelter.java
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

import io.savagedev.mobsreborn.init.ModContainers;
import io.savagedev.mobsreborn.init.ModTileEntities;
import io.savagedev.mobsreborn.util.BaseItemStackHandler;
import io.savagedev.mobsreborn.util.BasicSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.FurnaceFuelSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;
import java.util.function.Function;

public class ContainerMobDustSmelter extends Container
{
    private final Function<PlayerEntity, Boolean> isUsableByPlayer;
    private final IIntArray data;

    private ContainerMobDustSmelter(ContainerType<?> type, int id, PlayerInventory playerInventory) {
        this(type, id, playerInventory, p -> false, (new TileEntityMobDustSmelter()).getInventory(), new IntArray(6));
    }

    private ContainerMobDustSmelter(ContainerType<?> type, int id, PlayerInventory playerInventory, Function<PlayerEntity, Boolean> isUsableByPlayer, BaseItemStackHandler inventory, IIntArray data) {
        super(type, id);

        this.isUsableByPlayer = isUsableByPlayer;
        this.data = data;

        this.addSlot(new SlotSmelterFuel(inventory,0, 8, 62));
        this.addSlot(new BasicSlot(inventory,1, 50, 35));
        this.addSlot(new BasicSlot(inventory,2, 73, 35));
        this.addSlot(new SlotSmelterOutput(inventory,3, 135, 35));

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }

        this.trackIntArray(data);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return this.isUsableByPlayer.apply(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        return super.transferStackInSlot(playerIn, index);
    }

    public static ContainerMobDustSmelter create(int windowId, PlayerInventory playerInventory) {
        return new ContainerMobDustSmelter(ModContainers.mob_dust_smelter.get(), windowId, playerInventory);
    }

    public static ContainerMobDustSmelter create(int windowId, PlayerInventory playerInventory, Function<PlayerEntity, Boolean> isUsableByPlayer, BaseItemStackHandler inventory, IIntArray data) {
        return new ContainerMobDustSmelter(ModContainers.mob_dust_smelter.get(), windowId, playerInventory, isUsableByPlayer, inventory, data);
    }

    public int getCookProgressScaled(int pixels) {
        int i = this.getProgress();
        int j = this.getOperationTime();
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }

    public int getFuelBarScaled(int pixels) {
        int i = this.getFuel();
        int j = this.getTotalFuelCapacity();
        return (int) (j != 0 && i != 0 ? (long) i * pixels / j : 0);
    }

    public int getBurnLeftScaled(int pixels) {
        int i = this.getFuelLeft();
        int j = this.getFuelItemValue();
        return (int) (j != 0 && i != 0 ? (long) i * pixels / j : 0);
    }

    @OnlyIn(Dist.CLIENT)
    public int getFuelLeftScaled(int scale) {
        double stored = this.getTotalFuelStored();
        double max = this.getTotalFuelCapacity();
        double value = ((stored /max) * scale);

        return (int)value;
    }

    public boolean isFuelItemValuable() {
        return this.data.get(3) > 0;
    }

    public boolean isSmelting() {
        return this.data.get(0) > 0;
    }

    public int getProgress() {
        return this.data.get(0);
    }

    public int getFuel() {
        return this.data.get(1);
    }

    public int getFuelLeft() {
        return this.data.get(2);
    }

    public int getFuelItemValue() {
        return this.data.get(3);
    }

    public int getOperationTime() {
        return this.data.get(4);
    }

    public int getTotalFuelStored() {
        return this.data.get(5);
    }

    public int getTotalFuelCapacity() {
        return 14000;
    }

    private class SlotSmelterFuel extends SlotItemHandler
    {

        public SlotSmelterFuel(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return AbstractFurnaceTileEntity.isFuel(stack);
        }
    }

    private class SlotSmelterOutput extends SlotItemHandler
    {

        public SlotSmelterOutput(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }
    }
}
