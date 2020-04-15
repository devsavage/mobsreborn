package io.savagedev.mobsreborn.blocks.mobdustsmelter;

/*
 * ScreenMobDustSmelter.java
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

import io.savagedev.mobsreborn.reference.ModReference;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ScreenMobDustSmelter extends ContainerScreen<ContainerMobDustSmelter>
{
    private static final ResourceLocation background = new ResourceLocation(ModReference.mod_id, "textures/gui/gui_mob_dust_smelter.png");
    private final PlayerInventory playerInventory;

    public ScreenMobDustSmelter(ContainerMobDustSmelter screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.playerInventory = inv;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void render(int mouseX, int mouseY, float pTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, pTicks);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = this.getTitle().getFormattedText();

        this.font.drawString(title, (float) (this.xSize / 2 - this.font.getStringWidth(title) / 2), 6.0F, 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), this.xSize - 58, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.getMinecraft().getTextureManager().bindTexture(background);

        int widthX = (this.width - this.xSize) / 2;
        int heightY = (this.height - this.ySize) / 2;

        this.blit(widthX, heightY, 0, 0, this.xSize, this.ySize);

        ContainerMobDustSmelter container = this.getContainer();

        int bufferScale = container.getFuelLeftScaled(39);
        this.blit(widthX + 10, heightY + 57 - bufferScale + 1, 0, 206 - bufferScale, 12, bufferScale);

        if(container.isSmelting()) {
            int progressScaled = container.getCookProgressScaled(24);

            this.blit(widthX + 93, heightY + 36, 176, 14, progressScaled + 1, 16);
        }

    }
}
