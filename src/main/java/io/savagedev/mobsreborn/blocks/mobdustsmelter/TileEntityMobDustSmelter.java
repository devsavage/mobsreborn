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

import com.sun.java.accessibility.util.java.awt.TextComponentTranslator;
import io.savagedev.mobsreborn.blocks.BaseInventoryTileEntity;
import io.savagedev.mobsreborn.blocks.BaseTileEntityBlock;
import io.savagedev.mobsreborn.init.ModTileEntities;
import io.savagedev.mobsreborn.util.BaseItemStackHandler;
import io.savagedev.mobsreborn.util.ISpecialRecipe;
import io.savagedev.mobsreborn.util.LogHelper;
import io.savagedev.mobsreborn.util.SidedItemStackHandlerWrapper;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import sun.rmi.runtime.Log;

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

    private int totalFuelStored;
    private int totalFuelCapacity = 14000;
    private int fuelCost = 2000;

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
                    return TileEntityMobDustSmelter.this.getTotalFuelStored();
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {}

        @Override
        public int size() {
            return 6;
        }
    };

    public TileEntityMobDustSmelter() {
        super(ModTileEntities.mob_dust_smelter.get());
        this.inventory.setSlotValidator(this::canInsertStack);
        this.inventory.setOutputSlots(1);
    }

    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        return super.write(compound);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.mobsreborn.mob_dust_smelter", new Object[0]);
    }

    @Nullable
    @Override
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
        return ContainerMobDustSmelter.create(p_createMenu_1_, p_createMenu_2_, this::isUsableByPlayer, this.inventory, this.data);
    }

    @Override
    public void tick() {
        boolean dirty = false;

        World world = this.getWorld();
        if (world == null || world.isRemote())
            return;

        if(!this.inventory.getStackInSlot(0).isEmpty()) {

            if(FurnaceTileEntity.isFuel(this.inventory.getStackInSlot(0))) {
                if(!isFuelFull()) {
                    this.totalFuelStored += 200;
                    this.inventory.extractItemSuper(0, 1, false);
                }

                dirty = true;
            }
        }

        if(dirty) {
            this.markDirty();
            LogHelper.debug(this.getTotalFuelStored());
        }
    }

    public int getTotalFuelStored() {
        return this.totalFuelStored;
    }

    private boolean hasFuel() {
        return this.totalFuelStored > 0;
    }

    private boolean isFuelFull() {
        return this.totalFuelStored >= totalFuelCapacity;
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
        return this.fuelCost;
    }

    public int getFuelCapacity() {
        return this.totalFuelCapacity;
    }

    public int getFuelLeft() {
        return this.fuelLeft;
    }

    public int getFuelStored() {
        return this.totalFuelCapacity;
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
        if (slot == 1 && direction == Direction.WEST)
            return FurnaceTileEntity.isFuel(stack);
        return false;
    }
}
