package com.example.objects.GUI.StoneCutter;

import com.example.init.BlockInit;
import net.minecraft.init.Blocks;
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
        addRecipe(new ItemStack(Blocks.STONE), new ItemStack(Blocks.STONEBRICK, 1), "Каменные кирпичи");
        addRecipe(new ItemStack(Blocks.STONE), new ItemStack(Blocks.COBBLESTONE, 1),"Булыга");

        //STONEBRICK
        addRecipe(new ItemStack(Blocks.STONEBRICK), new ItemStack(Blocks.STONE_BRICK_STAIRS, 1), "Каменные кирпичи ступеньки");
        addRecipe(new ItemStack(Blocks.STONEBRICK), new ItemStack(Blocks.STONE_SLAB, 1), "Каменные кирпичи плиты");
        addRecipe(new ItemStack(Blocks.STONEBRICK), new ItemStack(Blocks.STONEBRICK, 1, 2), "Потрескавшиеся кирпичи");
        addRecipe(new ItemStack(Blocks.STONEBRICK), new ItemStack(Blocks.STONEBRICK, 1, 3), "Резной кирпичи");


        //QUARTZ
        addRecipe(new ItemStack(Blocks.QUARTZ_BLOCK), new ItemStack(Blocks.QUARTZ_STAIRS, 1), "Кварцовые ступыньки");
        addRecipe(new ItemStack(Blocks.QUARTZ_BLOCK), new ItemStack(Blocks.QUARTZ_BLOCK, 1,1), "Chiseled Quartz");
        addRecipe(new ItemStack(Blocks.QUARTZ_BLOCK), new ItemStack(Blocks.QUARTZ_BLOCK, 1,2), "Pillar Quartz");
        addRecipe(new ItemStack(Blocks.QUARTZ_BLOCK), new ItemStack(Blocks.STONE_SLAB, 2,7), "Quartz Slab");

        //GRANITE
        addRecipe(new ItemStack(Blocks.STONE,1,1), new ItemStack(Blocks.STONE, 1,2), "Granite");

        //DIORITE
        addRecipe(new ItemStack(Blocks.STONE,1,3), new ItemStack(Blocks.STONE, 1,4), "Diorite");

        //ANDEZITE
        addRecipe(new ItemStack(Blocks.STONE,1,5), new ItemStack(Blocks.STONE, 1,6), "Andezite");

        //TUFF
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_stairs, 1), "tuff stairs");
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_slab, 2), "tuff slab");
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_wall, 2), "tuff wall");
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.chiseled_tuff_bricks, 1), "chiseled tuff bricks");

        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_polished, 1), "tuff polished");
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_polished_slab, 2), "tuff polished slab");
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_polished_stairs, 1), "tuff polished stairs");
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_polished_wall, 1), "tuff polished stairs");

        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_bricks, 1), "tuff bricks");
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_bricks_slab, 2), "tuff bricks slab");
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_bricks_stairs, 1), "tuff bricks stairs");
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_bricks_wall, 1), "tuff bricks wall");

        //TUFF BRICKS
        addRecipe(new ItemStack(BlockInit.tuff_bricks), new ItemStack(BlockInit.tuff_bricks_slab, 2), "tuff bricks slab");
        addRecipe(new ItemStack(BlockInit.tuff_bricks), new ItemStack(BlockInit.tuff_bricks_stairs, 1), "tuff bricks stairs");
        addRecipe(new ItemStack(BlockInit.tuff_bricks), new ItemStack(BlockInit.tuff_bricks_wall, 1), "tuff bricks wall");

        //TUFF POLISHED
        addRecipe(new ItemStack(BlockInit.tuff_polished), new ItemStack(BlockInit.tuff_polished_slab, 2), "tuff polished slab");
        addRecipe(new ItemStack(BlockInit.tuff_polished), new ItemStack(BlockInit.tuff_polished_stairs, 1), "tuff polished stairs");
        addRecipe(new ItemStack(BlockInit.tuff_polished), new ItemStack(BlockInit.tuff_polished_wall, 1), "tuff polished stairs");
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