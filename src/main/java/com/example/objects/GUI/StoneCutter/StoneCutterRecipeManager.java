package com.example.objects.GUI.StoneCutter;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class StoneCutterRecipeManager {
    private static final List<StoneCutterRecipe> RECIPES = new ArrayList<>();

    static {
        registerDefaultRecipes();
    }

    private static void registerDefaultRecipes() {
        // Камень -> различные варианты
        addRecipe(new ItemStack(Blocks.STONE), new ItemStack(Blocks.STONE_SLAB, 2), "Каменные плиты");
        addRecipe(new ItemStack(Blocks.STONE), new ItemStack(Blocks.STONE_STAIRS, 1), "Каменные ступени");
        addRecipe(new ItemStack(Blocks.STONE), new ItemStack(Blocks.STONEBRICK, 1), "Каменные кирпичи");

        // Каменные кирпичи -> варианты
        addRecipe(new ItemStack(Blocks.STONEBRICK), new ItemStack(Blocks.STONE_BRICK_STAIRS, 1), "Ступени из кирпича");
        addRecipe(new ItemStack(Blocks.STONEBRICK), new ItemStack(Blocks.STONE_SLAB, 2, 5), "Плиты из кирпича");

        // Булыжник -> варианты
        addRecipe(new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.STONE_SLAB, 2, 3), "Булыжные плиты");
        addRecipe(new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.STONE_STAIRS, 1), "Булыжные ступени");

        // Дерево -> варианты
        addRecipe(new ItemStack(Blocks.PLANKS, 1, 0), new ItemStack(Blocks.WOODEN_SLAB, 2, 0), "Дубовые плиты");
        addRecipe(new ItemStack(Blocks.PLANKS, 1, 0), new ItemStack(Blocks.OAK_STAIRS, 1), "Дубовые ступени");

        // Кварц -> варианты
        addRecipe(new ItemStack(Blocks.QUARTZ_BLOCK), new ItemStack(Blocks.QUARTZ_STAIRS, 1), "Кварцевые ступени");
        addRecipe(new ItemStack(Blocks.QUARTZ_BLOCK), new ItemStack(Blocks.STONE_SLAB, 2, 7), "Кварцевые плиты");
    }

    public static void addRecipe(ItemStack input, ItemStack output, String name) {
        RECIPES.add(new StoneCutterRecipe(input, output, name));
    }

    public static List<StoneCutterRecipe> getRecipesForInput(ItemStack input) {
        List<StoneCutterRecipe> matchingRecipes = new ArrayList<>();
        for (StoneCutterRecipe recipe : RECIPES) {
            if (recipe.matches(input)) {
                matchingRecipes.add(recipe);
            }
        }
        return matchingRecipes;
    }

    public static List<StoneCutterRecipe> getAllRecipes() {
        return new ArrayList<>(RECIPES);
    }
}