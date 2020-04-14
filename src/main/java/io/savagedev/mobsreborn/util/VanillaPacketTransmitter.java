package io.savagedev.mobsreborn.util;

/*
 * VanillaPacketTransmitter.java
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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public final class VanillaPacketTransmitter
{
    public static void transmitTEToNearbyPlayers(TileEntity tileEntity) {
        World world = tileEntity.getWorld();

        if(world == null) {
            return;
        }

        SUpdateTileEntityPacket packet = tileEntity.getUpdatePacket();

        if(packet == null) {
            return;
        }

        List<? extends PlayerEntity> playerEntities = world.getPlayers();
        BlockPos blockPos = tileEntity.getPos();

        for(Object player : playerEntities) {
            if(player instanceof ServerPlayerEntity) {
                ServerPlayerEntity sPlayer = (ServerPlayerEntity) player;
                BlockPos playerPos = sPlayer.getPosition();
                if((float) Math.hypot(playerPos.getX() - playerPos.getZ(), playerPos.getX() + 0.5 - playerPos.getZ() + 0.5) < 64) {
                    sPlayer.connection.sendPacket(packet);
                }
            }
        }
    }

    public static void transmitTEToNearbyPlayers(World worldIn, int posX, int posY, int posZ) {
        TileEntity tileEntity = worldIn.getTileEntity(new BlockPos(posX, posY, posZ));

        if(tileEntity != null) {
            transmitTEToNearbyPlayers(tileEntity);
        }
    }
}
