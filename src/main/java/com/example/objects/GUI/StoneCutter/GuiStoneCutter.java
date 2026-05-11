package com.example.objects.GUI.StoneCutter;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import java.io.IOException;
import java.util.List;

public class GuiStoneCutter extends GuiContainer {

    private static final ResourceLocation TEXTURE   = new ResourceLocation("samplemod112:textures/gui/stonecutter/stonecutter.png");
    private static final ResourceLocation SCROLLER  = new ResourceLocation("samplemod112:textures/gui/stonecutter/sprites/scroll.png");
    private static final ResourceLocation SELECTED  = new ResourceLocation("samplemod112:textures/gui/stonecutter/sprites/selected.png");
    private static final ResourceLocation UNSELECTED = new ResourceLocation("samplemod112:textures/gui/stonecutter/sprites/unselected.png");

    // Recipe grid layout — matches both rendering and click-detection
    private static final int GRID_X       = 52;
    private static final int GRID_Y       = 14;
    private static final int SLOT_SIZE    = 18;
    private static final int COLUMNS      = 6;
    private static final int VISIBLE_ROWS = 6;

    // Scrollbar — drawn and detected at the same position (aligned to the track on the texture)
    private static final int SCROLL_X            = 170;
    private static final int SCROLL_Y            = GRID_Y;
    private static final int SCROLL_TRACK_HEIGHT = VISIBLE_ROWS * SLOT_SIZE;         // 72
    private static final int SCROLL_THUMB_HEIGHT = 15;

    private final ContainerStoneCutter container;
    private int  scrollRow          = 0;
    private int  selectedRecipeIndex = -1;
    private boolean isScrolling     = false;

    public GuiStoneCutter(InventoryPlayer playerInv) {
        super(new ContainerStoneCutter(playerInv));
        this.container = (ContainerStoneCutter) this.inventorySlots;
        this.xSize = 227;
        this.ySize = 191;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        int x = (this.width  - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        int absScrollX = x + SCROLL_X;
        int absScrollY = y + SCROLL_Y;

        boolean isMouseDown = Mouse.isButtonDown(0);
        if (!isScrolling && isMouseDown
                && mouseX >= absScrollX && mouseX < absScrollX + 12
                && mouseY >= absScrollY && mouseY < absScrollY + SCROLL_TRACK_HEIGHT) {
            isScrolling = true;
        }
        if (!isMouseDown) isScrolling = false;

        if (isScrolling) {
            float f = ((float)(mouseY - absScrollY) - SCROLL_THUMB_HEIGHT / 2.0F)
                    / (float)(SCROLL_TRACK_HEIGHT - SCROLL_THUMB_HEIGHT);
            f = MathHelper.clamp(f, 0.0F, 1.0F);
            scrollRow = Math.round(f * getMaxScrollRows());
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (this.width  - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        this.mc.getTextureManager().bindTexture(TEXTURE);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, xSize, ySize, xSize, ySize);

        // Scrollbar thumb
        int maxScroll = getMaxScrollRows();
        int scrollPos = (maxScroll > 0)
                ? (SCROLL_TRACK_HEIGHT - SCROLL_THUMB_HEIGHT) * scrollRow / maxScroll
                : 0;
        this.mc.getTextureManager().bindTexture(SCROLLER);
        drawModalRectWithCustomSizedTexture(x + SCROLL_X, y + SCROLL_Y + scrollPos,
                0, 0, 12, SCROLL_THUMB_HEIGHT, 12, SCROLL_THUMB_HEIGHT);

        renderRecipeIcons(x, y);
    }

    private void renderRecipeIcons(int x, int y) {
        List<StoneCutterRecipe> recipes = container.getAvailableRecipes();
        if (recipes == null || recipes.isEmpty()) return;

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();

        for (int i = 0; i < VISIBLE_ROWS * COLUMNS; i++) {
            int recipeIndex = i + scrollRow * COLUMNS;
            if (recipeIndex >= recipes.size()) break;

            int slotX = x + GRID_X + (i % COLUMNS) * SLOT_SIZE;
            int slotY = y + GRID_Y + (i / COLUMNS) * SLOT_SIZE;

            GlStateManager.disableLighting();
            boolean sel = (recipeIndex == selectedRecipeIndex);
            this.mc.getTextureManager().bindTexture(sel ? SELECTED : UNSELECTED);
            drawModalRectWithCustomSizedTexture(slotX, slotY, 0, 0, SLOT_SIZE, SLOT_SIZE, SLOT_SIZE, SLOT_SIZE);
            GlStateManager.enableLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

            this.itemRender.renderItemAndEffectIntoGUI(
                    recipes.get(recipeIndex).getOutput(), slotX + 1, slotY + 1);
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton != 0) return;

        List<StoneCutterRecipe> recipes = container.getAvailableRecipes();
        if (recipes == null || recipes.isEmpty()) return;

        int x = (this.width  - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;

        for (int i = 0; i < VISIBLE_ROWS * COLUMNS; i++) {
            int slotX = x + GRID_X + (i % COLUMNS) * SLOT_SIZE;
            int slotY = y + GRID_Y + (i / COLUMNS) * SLOT_SIZE;

            if (mouseX >= slotX && mouseX < slotX + SLOT_SIZE
                    && mouseY >= slotY && mouseY < slotY + SLOT_SIZE) {
                int recipeIndex = i + scrollRow * COLUMNS;
                if (recipeIndex < recipes.size()) {
                    selectedRecipeIndex = recipeIndex;
                    this.mc.playerController.sendEnchantPacket(this.container.windowId, recipeIndex);
                }
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            if (wheel > 0) scrollRow--; else scrollRow++;
            scrollRow = MathHelper.clamp(scrollRow, 0, getMaxScrollRows());
        }
    }

    private int getMaxScrollRows() {
        List<StoneCutterRecipe> recipes = container.getAvailableRecipes();
        if (recipes == null || recipes.isEmpty()) return 0;
        int totalRows = (int) Math.ceil((double) recipes.size() / COLUMNS);
        return Math.max(0, totalRows - VISIBLE_ROWS);
    }
}
