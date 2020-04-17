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
import io.savagedev.mobsreborn.init.ModItems;
import io.savagedev.mobsreborn.init.ModTileEntities;
import io.savagedev.mobsreborn.util.BaseItemStackHandler;
import io.savagedev.mobsreborn.util.LogHelper;
import io.savagedev.mobsreborn.util.SidedItemStackHandlerWrapper;
import io.savagedev.mobsreborn.util.helper.StackHelper;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
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
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import sun.rmi.runtime.Log;

import javax.annotation.Nullable;
import java.util.Properties;
import java.util.function.Function;

public class TileEntityMobDustSmelter extends BaseInventoryTileEntity implements INamedContainerProvider, ITickableTileEntity
{
    private final BaseItemStackHandler inventory = new BaseItemStackHandler(4);
    private int progress;
    private int totalFuelStored;

    private final IIntArray data = new IIntArray() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return TileEntityMobDustSmelter.this.getProgress();
                case 1:
                    return TileEntityMobDustSmelter.this.getFuelStored();
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {}

        @Override
        public int size() {
            return 2;
        }
    };

    public TileEntityMobDustSmelter() {
        super(ModTileEntities.mob_dust_smelter.get());
        this.inventory.setOutputSlots(3);
    }

    @Override
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.progress = compound.getInt("Progress");
        this.totalFuelStored = compound.getInt("StoredFuel");
        ItemStackHelper.loadAllItems(compound, this.getInventory().getStacks());
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("Progress", this.progress);
        compound.putInt("StoredFuel", this.totalFuelStored);
        ItemStackHelper.saveAllItems(compound, this.getInventory().getStacks());
        return super.write(compound);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.mobsreborn.mob_dust_smelter");
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

        // Temp recipe handler

        if(this.hasFuel()) {
            ItemStack input1 = this.inventory.getStackInSlot(1);
            ItemStack input2 = this.inventory.getStackInSlot(2);
            ItemStack output = this.inventory.getStackInSlot(3);

            if(!input1.isEmpty() && !input2.isEmpty()) {
                ItemStack outputStack = this.getRecipeOutput(input1, input2);
                if(!outputStack.isEmpty()) {
                    if(!outputStack.isEmpty() && (output.isEmpty() || StackHelper.canCombineStacks(output, outputStack))) {
                        this.progress++;

                        LogHelper.debug(this.progress);

                        if(this.progress >= this.getOperationTime()) {
                            this.totalFuelStored -= this.getFuelCost();
                            if(this.totalFuelStored <= 0) {
                                this.totalFuelStored = 0;
                            }
                            this.inventory.extractItemSuper(1, 1, false);
                            this.inventory.extractItemSuper(2, 1, false);

                            if(output.isEmpty()) {
                                this.inventory.setStackInSlot(3, outputStack.copy());
                            } else {
                                output.grow(outputStack.getCount());
                            }

                            this.progress = 0;
                        }

                        dirty = true;
                    }
                }
            } else {
                if(this.progress > 0) {
                    this.progress = 0;
                    dirty = true;
                }
            }
        }

        if(dirty) {
            this.markDirty();
        }
    }

    public int getFuelStored() {
        return this.totalFuelStored;
    }

    public int getProgress() {
        return this.progress;
    }

    private boolean hasFuel() {
        return this.totalFuelStored > 0;
    }

    private boolean isFuelFull() {
        return this.getFuelStored() >= getFuelCapacity();
    }

    public static int getOperationTime() {
        return 200;
    }

    public int getFuelCost() {
        return 2000;
    }

    public static int getFuelCapacity() {
        return 14000;
    }

    private ItemStack getRecipeOutput(ItemStack stack1, ItemStack stack2) {
        if(!stack1.isEmpty() && !stack2.isEmpty()) {
            if(stack1.getItem() == ModItems.creeper_dust.get()) {
                return new ItemStack(ModItems.creeper_metal.get(), 1);
            } else if(stack1.getItem() == ModItems.enderman_dust.get()) {
                return new ItemStack(ModItems.enderman_metal.get(), 1);
            } else if(stack1.getItem() == ModItems.zombie_dust.get()) {
                return new ItemStack(ModItems.zombie_metal.get(), 1);
            } else if(stack1.getItem() == ModItems.skeleton_dust.get()) {
                return new ItemStack(ModItems.skeleton_metal.get(), 1);
            }
        }

        return ItemStack.EMPTY;
    }
}
