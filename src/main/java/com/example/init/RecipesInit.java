package com.example.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.init.Items;
import net.minecraft.init.Blocks;


public class RecipesInit {
    public static void init() {
        
        // Iron
        FurnaceRecipes.instance().addSmelting(Item.getItemFromBlock(BlockInit.deepslate_iron_ore), new ItemStack(Items.IRON_INGOT), 0.7F);

        // Gold
        FurnaceRecipes.instance().addSmelting(Item.getItemFromBlock(BlockInit.deepslate_gold_ore), new ItemStack(Items.GOLD_INGOT), 1.0F);

        // Coal
        FurnaceRecipes.instance().addSmelting(Item.getItemFromBlock(BlockInit.deepslate_coal_ore), new ItemStack(Items.COAL), 0.1F);

        // Diamond
        FurnaceRecipes.instance().addSmelting(Item.getItemFromBlock(BlockInit.deepslate_diamond_ore), new ItemStack(Items.DIAMOND), 1.0F);

        // Emerald
        FurnaceRecipes.instance().addSmelting(Item.getItemFromBlock(BlockInit.deepslate_emerald_ore), new ItemStack(Items.EMERALD), 1.0F);

        // Redstone
        FurnaceRecipes.instance().addSmelting(Item.getItemFromBlock(BlockInit.deepslate_redstone_ore), new ItemStack(Items.REDSTONE), 1.0F);

        // Redstone
        FurnaceRecipes.instance().addSmelting(Item.getItemFromBlock(BlockInit.deepslate_lapis_lazuli_ore), new ItemStack(Items.DYE, 1, 4), 1.0F);
        //deepslate
        FurnaceRecipes.instance().addSmelting(Item.getItemFromBlock(BlockInit.cobbled_deepslate), new ItemStack(BlockInit.deepslate) , 1.0F);

    }
}
