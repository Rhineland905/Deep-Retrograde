package com.deepretrograde.objects.GUI.StoneCutter;

import com.deepretrograde.init.BlockInit;
import com.deepretrograde.init.SoundInit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class ContainerStoneCutter extends Container {
    private final InventoryBasic inventory = new InventoryBasic("StoneCutter", false, 2);
    private final World world;
    private final BlockPos pos;
    private List<StoneCutterRecipe> availableRecipes = Collections.emptyList();
    private int selectedRecipeIndex = -1;

    // Какой предмет лежал во входе в прошлый раз: выбор рецепта сбрасывается
    // только при смене предмета, а не при каждом изменении количества.
    private Item lastInputItem = null;
    private int lastInputMeta = -1;
    private long lastSoundTime;

    public ContainerStoneCutter(InventoryPlayer playerInv, World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;

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
                stack.onCrafting(thePlayer.world, thePlayer, stack.getCount());
                ItemStack input = inventory.getStackInSlot(0);
                if (!input.isEmpty()) {
                    input.shrink(1);
                    updateAvailableRecipes();
                }
                // Не спамим звуком при Shift-клике (он забирает предметы пачкой за один тик)
                long time = thePlayer.world.getTotalWorldTime();
                if (!thePlayer.world.isRemote && lastSoundTime != time) {
                    lastSoundTime = time;
                    thePlayer.world.playSound(null, pos,
                            SoundInit.STONECUTTER_USE, SoundCategory.BLOCKS, 0.5F, 1.0F);
                }
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

    // Вызывается при клике по рецепту: на клиенте напрямую из GUI, на сервере через sendEnchantPacket.
    // Раньше клиентский контейнер не знал о выборе — после любой синхронизации входного слота
    // клиент обнулял выход, и результат "мигал"/пропадал до следующего пакета от сервера.
    @Override
    public boolean enchantItem(EntityPlayer playerIn, int id) {
        if (id >= 0 && id < availableRecipes.size()) {
            this.selectedRecipeIndex = id;
            updateOutput();
            return true;
        }
        return false;
    }

    private void updateAvailableRecipes() {
        ItemStack input = inventory.getStackInSlot(0);
        Item item = input.isEmpty() ? null : input.getItem();
        int meta = input.isEmpty() ? -1 : input.getMetadata();

        if (item != lastInputItem || meta != lastInputMeta) {
            lastInputItem = item;
            lastInputMeta = meta;
            selectedRecipeIndex = -1;
            this.availableRecipes = StoneCutterRecipeManager.getRecipesForInput(input);
        }

        if (selectedRecipeIndex >= availableRecipes.size()) {
            selectedRecipeIndex = -1;
        }
        updateOutput();
    }

    private void updateOutput() {
        if (selectedRecipeIndex >= 0 && selectedRecipeIndex < availableRecipes.size()
                && !inventory.getStackInSlot(0).isEmpty()) {
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
                if (!this.mergeItemStack(itemstack1, 2, 38, false)) return ItemStack.EMPTY;
            } else { // Из инвентаря игрока
                // Есть рецепты — во вход, иначе между инвентарём и хотбаром
                if (!StoneCutterRecipeManager.getRecipesForInput(itemstack1).isEmpty()) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) return ItemStack.EMPTY;
                } else if (index < 29) {
                    if (!this.mergeItemStack(itemstack1, 29, 38, false)) return ItemStack.EMPTY;
                } else if (!this.mergeItemStack(itemstack1, 2, 29, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();

            if (itemstack1.getCount() == itemstack.getCount()) return ItemStack.EMPTY;
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }

    // Возвращаем вход в инвентарь (или на пол, если места нет) при закрытии интерфейса
    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        // Выход "виртуальный" — его нельзя возвращать, иначе получится дюп
        this.inventory.removeStackFromSlot(1);
        if (!playerIn.world.isRemote) {
            this.clearContainer(playerIn, playerIn.world, this.inventory);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        // Раньше всегда true: GUI оставался открытым после того, как камнерез сломали
        // или игрок ушёл далеко
        return world.getBlockState(pos).getBlock() == BlockInit.stonecutter
                && playerIn.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }

    public List<StoneCutterRecipe> getAvailableRecipes() {
        return availableRecipes;
    }

    public int getSelectedRecipeIndex() {
        return selectedRecipeIndex;
    }
}
