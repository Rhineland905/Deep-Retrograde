package com.deepretrograde.objects.GUI.StoneCutter;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class StoneCutterRecipe {
    private final ItemStack input;
    private final ItemStack output;
    private final String recipeName;

    public StoneCutterRecipe(ItemStack input, ItemStack output, String recipeName) {
        this.input = input;
        this.output = output;
        this.recipeName = recipeName;
    }

    public ItemStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public boolean matches(ItemStack inputStack) {
        if (inputStack.isEmpty() || input.isEmpty()) {
            return false;
        }
        return inputStack.getItem() == input.getItem() &&
                inputStack.getMetadata() == input.getMetadata();
    }
}