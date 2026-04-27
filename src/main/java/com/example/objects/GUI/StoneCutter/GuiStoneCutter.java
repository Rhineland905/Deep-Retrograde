package com.example.objects.GUI.StoneCutter;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import java.io.IOException;
import java.util.List;

public class GuiStoneCutter extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("samplemod112:textures/gui/stonecutter/stonecutter.png");
    private static final ResourceLocation SCROLLER = new ResourceLocation("samplemod112:textures/gui/stonecutter/sprites/scroll.png");
    private static final ResourceLocation SELECTED = new ResourceLocation("samplemod112:textures/gui/stonecutter/sprites/selected.png");
    private static final ResourceLocation UNSELECTED = new ResourceLocation("samplemod112:textures/gui/stonecutter/sprites/unselected.png");
    private final ContainerStoneCutter container;

    private int scrollRow = 0;
    private boolean isScrolling = false;

    private static final int COLUMNS = 3;
    private static final int VISIBLE_ROWS = 4;

    public GuiStoneCutter(InventoryPlayer playerInv) {
        super(new ContainerStoneCutter(playerInv));
        this.container = (ContainerStoneCutter) this.inventorySlots;
        this.xSize = 227;
        this.ySize = 191;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        // Логика перетаскивания ползунка
        boolean isMouseDown = Mouse.isButtonDown(0);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        int scrollX = x + 119;
        int scrollY = y + 15;

        if (!this.isScrolling && isMouseDown && mouseX >= scrollX && mouseX < scrollX + 12 && mouseY >= scrollY && mouseY < scrollY + 54) {
            this.isScrolling = true;
        }
        if (!isMouseDown) this.isScrolling = false;

        if (this.isScrolling) {
            float f = ((float)(mouseY - scrollY) - 7.5F) / (54.0F - 15.0F);
            f = MathHelper.clamp(f, 0.0F, 1.0F);
            int maxScroll = getMaxScrollRows();
            this.scrollRow = (int)((double)(f * (float)maxScroll) + 0.5D);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;


        drawModalRectWithCustomSizedTexture(x, y, 0, 0, xSize, ySize, 227, 191);


        int maxScroll = getMaxScrollRows();
        int scrollPos = (maxScroll > 0) ? (int)(41.0F * (float)scrollRow / (float)maxScroll) : 0;
        this.mc.getTextureManager().bindTexture(SCROLLER);
        drawModalRectWithCustomSizedTexture(x + 170, y + 15 + scrollPos, 0, 0, 12, 15, 12, 15);
        this.mc.getTextureManager().bindTexture(TEXTURE);

        renderRecipeIcons(x, y);
    }

    private void renderRecipeIcons(int x, int y) {
        List<StoneCutterRecipe> recipes = container.getAvailableRecipes();
        if (recipes.isEmpty()) return;


        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();

        int step = 19;

        for (int i = 0; i < (VISIBLE_ROWS * COLUMNS); i++) {
            int recipeIndex = i + (scrollRow * COLUMNS);
            if (recipeIndex < recipes.size()) {
                int slotX = x + 53 + (i % 3) * step;
                int slotY = y + 16 + (i / 3) * step;


                GlStateManager.disableLighting(); // Выключаем свет для плоской текстуры рамки
                this.mc.getTextureManager().bindTexture(UNSELECTED);
                drawModalRectWithCustomSizedTexture(slotX, slotY, 0, 0, 18, 18, 18, 18);
                GlStateManager.enableLighting(); // Включаем обратно для предмета


                this.itemRender.renderItemAndEffectIntoGUI(recipes.get(recipeIndex).getOutput(), slotX + 1, slotY + 1);
            }
        }

        // 2. ВЫКЛЮЧАЕМ СВЕТ ПОСЛЕ ЦИКЛА
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            int x = (this.width - this.xSize) / 2;
            int y = (this.height - this.ySize) / 2;
            List<StoneCutterRecipe> recipes = container.getAvailableRecipes();

            for (int i = 0; i < (VISIBLE_ROWS * COLUMNS); i++) {
                int slotX = x + 52 + (i % COLUMNS) * 18;
                int slotY = y + 18 + (i / COLUMNS) * 18;
                if (mouseX >= slotX && mouseX < slotX + 18 && mouseY >= slotY && mouseY < slotY + 18) {
                    int recipeIndex = i + (scrollRow * COLUMNS);
                    if (recipeIndex < recipes.size()) {
                        this.mc.playerController.sendEnchantPacket(this.container.windowId, recipeIndex);
                    }
                }
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            int maxScroll = getMaxScrollRows();
            if (wheel > 0) scrollRow--; else scrollRow++;
            this.scrollRow = MathHelper.clamp(scrollRow, 0, maxScroll);
        }
    }

    private int getMaxScrollRows() {
        List<StoneCutterRecipe> recipes = container.getAvailableRecipes();
        int totalRows = (int) Math.ceil((double) recipes.size() / COLUMNS);
        return Math.max(0, totalRows - VISIBLE_ROWS);
    }
}