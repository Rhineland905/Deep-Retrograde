package com.example.objects.GUI.StoneCutter;

import com.example.init.SoundInit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import java.util.List;

public class ContainerStoneCutter extends Container {
    private final InventoryBasic inventory = new InventoryBasic("StoneCutter", false, 2);
    private List<StoneCutterRecipe> availableRecipes = new java.util.ArrayList<>();
    private int selectedRecipeIndex = -1;

    public ContainerStoneCutter(InventoryPlayer playerInv) {
        // Слот 0: Вход (на координатах 20, 33)
        this.addSlotToContainer(new Slot(inventory, 0, 20, 33) {
            @Override
            public void onSlotChanged() {
                super.onSlotChanged();
                updateAvailableRecipes();
            }
        });

        // Слот 1: Выход (на координатах 194, 33)
        this.addSlotToContainer(new Slot(inventory, 1, 194, 33) {
            @Override
            public boolean isItemValid(ItemStack stack) { return false; }

            @Override
            public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
                ItemStack input = inventory.getStackInSlot(0);
                if (!input.isEmpty()) {
                    input.shrink(1);
                    updateAvailableRecipes();
                }
                thePlayer.world.playSound(null,
                        thePlayer.posX, thePlayer.posY, thePlayer.posZ,
                        SoundInit.STONECUTTER_USE, SoundCategory.BLOCKS,
                        0.5F, 1.0F);
                return super.onTake(thePlayer, stack);
            }
        });

        // ИНВЕНТАРЬ И ХОТБАР
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(new Slot(playerInv, x + y * 9 + 9, 33 + x * 18, 109 + y * 18));
            }
        }
        for (int x = 0; x < 9; x++) {
            this.addSlotToContainer(new Slot(playerInv, x, 33 + x * 18, 167));
        }
    }

    // Этот метод вызывается, когда игрок кликает по рецепту в GUI (через sendEnchantPacket)
    @Override
    public boolean enchantItem(EntityPlayer playerIn, int id) {
        if (availableRecipes != null && id >= 0 && id < availableRecipes.size()) {
            this.selectedRecipeIndex = id;
            updateOutput();
            return true;
        }
        return false;
    }

    private void updateAvailableRecipes() {
        ItemStack input = inventory.getStackInSlot(0);
        // Получаем список рецептов из вашего менеджера рецептов
        this.availableRecipes = StoneCutterRecipeManager.getRecipesForInput(input);

        // Если список пуст или выбранный индекс стал невалидным, сбрасываем выход
        if (availableRecipes.isEmpty() || selectedRecipeIndex >= availableRecipes.size()) {
            selectedRecipeIndex = -1;
        }
        updateOutput();
    }

    private void updateOutput() {
        if (selectedRecipeIndex != -1 && !availableRecipes.isEmpty()) {
            ItemStack result = availableRecipes.get(selectedRecipeIndex).getOutput().copy();
            this.inventory.setInventorySlotContents(1, result);
        } else {
            this.inventory.setInventorySlotContents(1, ItemStack.EMPTY);
        }
    }

    // Обработка Shift + Клик (важно, чтобы предметы не исчезали)
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 1) { // Если забираем из Выхода
                if (!this.mergeItemStack(itemstack1, 2, 38, true)) return ItemStack.EMPTY;
                slot.onSlotChange(itemstack1, itemstack);
            } else if (index == 0) { // Если забираем из Входа
                if (!this.mergeItemStack(itemstack1, 2, 38, true)) return ItemStack.EMPTY;
            } else { // Если перекладываем из инвентаря игрока
                // Пытаемся положить в слот Входа (0)
                if (!this.mergeItemStack(itemstack1, 0, 1, false)) return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();

            if (itemstack1.getCount() == itemstack.getCount()) return ItemStack.EMPTY;
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }

    // Возвращаем предметы на пол при закрытии интерфейса
    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (!playerIn.world.isRemote) {
            ItemStack input = this.inventory.getStackInSlot(0);
            if (!input.isEmpty()) playerIn.dropItem(input, false);

        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    public List<StoneCutterRecipe> getAvailableRecipes() {
        return availableRecipes;
    }
}