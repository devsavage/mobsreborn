package io.savagedev.mobsreborn.blocks.mobdustsmelter;

/*
 * TileEntityMobDustSmelter.java
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

import io.savagedev.mobsreborn.blocks.BaseInventoryTileEntity;
import io.savagedev.mobsreborn.blocks.BaseTileEntityBlock;
import io.savagedev.mobsreborn.util.BaseItemStackHandler;
import io.savagedev.mobsreborn.util.ISpecialRecipe;
import io.savagedev.mobsreborn.util.SidedItemStackHandlerWrapper;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.Properties;
import java.util.function.Function;

public class TileEntityMobDustSmelter extends BaseInventoryTileEntity implements INamedContainerProvider, ITickableTileEntity
{
    private final BaseItemStackHandler inventory = new BaseItemStackHandler(4);
    private final LazyOptional<IItemHandlerModifiable>[] handlers = SidedItemStackHandlerWrapper.create(this.inventory, new Direction[] { Direction.UP, Direction.DOWN, Direction.NORTH }, this::canInsertStackSided, null);
    private ISpecialRecipe recipe;
    private int progress;
    private int fuel;
    private int fuelLeft;
    private int fuelItemValue;

    private final IIntArray data = new IIntArray() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return TileEntityMobDustSmelter.this.getProgress();
                case 1:
                    return TileEntityMobDustSmelter.this.getFuel();
                case 2:
                    return TileEntityMobDustSmelter.this.getFuelLeft();
                case 3:
                    return TileEntityMobDustSmelter.this.getFuelItemValue();
                case 4:
                    return TileEntityMobDustSmelter.this.getOperationTime();
                case 5:
                    return TileEntityMobDustSmelter.this.getFuelCapacity();
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {

        }

        @Override
        public int size() {
            return 6;
        }
    };

    public TileEntityMobDustSmelter(TileEntityType<?> type) {
        super(type);
        this.inventory.setSlotValidator(this::canInsertStack);
        this.inventory.setOutputSlots(1);
    }

    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }

    @Nullable
    @Override
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
        return null;
    }

    @Override
    public void tick() {

    }

    public int getProgress() {
        return this.progress;
    }

    public int getOperationTime() {
        return 200;
    }

    public int getFuel() {
        return this.fuel;
    }

    public int getFuelUsage() {
        return 1;
    }

    public int getFuelCapacity() {
        return 1600;
    }

    public int getFuelLeft() {
        return this.fuelLeft;
    }

    public int getFuelItemValue() {
        return this.fuelItemValue;
    }

    private boolean canInsertStack(int slot, ItemStack stack) {
        return this.canInsertStackSided(slot, stack, null);
    }

    public boolean canInsertStackSided(int slot, ItemStack stack, Direction direction) {
        if (direction == null)
            return true;
        if (slot == 0 && direction == Direction.UP)
            return true;
        if (slot == 1 && direction == Direction.NORTH)
            return FurnaceTileEntity.isFuel(stack);
        return false;
    }
}
