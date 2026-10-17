package com.deepretrograde.objects.GUI.StoneCutter;

import com.deepretrograde.init.BlockInit;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoneCutterRecipeManager {
    private static final List<StoneCutterRecipe> RECIPES = new ArrayList<>();
    // Должен быть объявлен ДО static-блока ниже, иначе addRecipe получит null
    private static final Map<Long, List<StoneCutterRecipe>> INDEX = new HashMap<>();

    static {
        registerDefaultRecipes();
    }

    private static void registerDefaultRecipes() {
        // Камень -> различные варианты
        addRecipe(new ItemStack(Blocks.STONE), new ItemStack(Blocks.STONEBRICK, 1), "Каменные кирпичи");
        addRecipe(new ItemStack(Blocks.STONE), new ItemStack(Blocks.COBBLESTONE, 1),"Булыга");

        //STONEBRICK
        addRecipe(new ItemStack(Blocks.STONEBRICK), new ItemStack(Blocks.STONE_BRICK_STAIRS, 1), "Каменные кирпичи ступеньки");
        addRecipe(new ItemStack(Blocks.STONEBRICK), new ItemStack(Blocks.STONE_SLAB, 2, 5), "Каменные кирпичи плиты");
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
        addRecipe(new ItemStack(Blocks.STONE,1,3), new ItemStack(BlockInit.calcite,1), "calcite");

        //ANDEZITE
        addRecipe(new ItemStack(Blocks.STONE,1,5), new ItemStack(Blocks.STONE, 1,6), "Andezite");

        //TUFF
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_stairs, 1), "tuff stairs");
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_slab, 2), "tuff slab");
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_wall, 1), "tuff wall");
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.chiseled_tuff, 1), "chiseled tuff");
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.chiseled_tuff_bricks, 1), "chiseled tuff bricks");

        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_polished, 1), "tuff polished");
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_polished_slab, 2), "tuff polished slab");
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_polished_stairs, 1), "tuff polished stairs");
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_polished_wall, 1), "tuff polished wall");

        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_bricks, 1), "tuff bricks");
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_bricks_slab, 2), "tuff bricks slab");
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_bricks_stairs, 1), "tuff bricks stairs");
        addRecipe(new ItemStack(BlockInit.tuff), new ItemStack(BlockInit.tuff_bricks_wall, 1), "tuff bricks wall");

        //TUFF BRICKS
        addRecipe(new ItemStack(BlockInit.tuff_bricks), new ItemStack(BlockInit.tuff_bricks_slab, 2), "tuff bricks slab");
        addRecipe(new ItemStack(BlockInit.tuff_bricks), new ItemStack(BlockInit.tuff_bricks_stairs, 1), "tuff bricks stairs");
        addRecipe(new ItemStack(BlockInit.tuff_bricks), new ItemStack(BlockInit.tuff_bricks_wall, 1), "tuff bricks wall");
        addRecipe(new ItemStack(BlockInit.tuff_bricks), new ItemStack(BlockInit.chiseled_tuff_bricks, 1), "chiseled tuff bricks");

        //TUFF POLISHED
        addRecipe(new ItemStack(BlockInit.tuff_polished), new ItemStack(BlockInit.tuff_polished_slab, 2), "tuff polished slab");
        addRecipe(new ItemStack(BlockInit.tuff_polished), new ItemStack(BlockInit.tuff_polished_stairs, 1), "tuff polished stairs");
        addRecipe(new ItemStack(BlockInit.tuff_polished), new ItemStack(BlockInit.tuff_polished_wall, 1), "tuff polished wall");
        addRecipe(new ItemStack(BlockInit.tuff_polished), new ItemStack(BlockInit.tuff_bricks, 1), "tuff bricks");

        //DEEPSLATE
        addRecipe(new ItemStack(BlockInit.deepslate), new ItemStack(BlockInit.deepslate_bricks, 1), "deepslate bricks");
        addRecipe(new ItemStack(BlockInit.deepslate), new ItemStack(BlockInit.deepslate_bricks_slab, 2), "deepslate slab");
        addRecipe(new ItemStack(BlockInit.deepslate), new ItemStack(BlockInit.deepslate_bricks_wall, 1), "deepslate wall");
        addRecipe(new ItemStack(BlockInit.deepslate), new ItemStack(BlockInit.deepslate_bricks_stairs, 1), "deepslate stairs");

        addRecipe(new ItemStack(BlockInit.deepslate), new ItemStack(BlockInit.polished_deepslate, 1), "polished deepslate");
        addRecipe(new ItemStack(BlockInit.deepslate), new ItemStack(BlockInit.polished_deepslate_wall, 1), "polished deepslate wall");
        addRecipe(new ItemStack(BlockInit.deepslate), new ItemStack(BlockInit.polished_deepslate_stairs, 1), "polished deepslate stairs");
        addRecipe(new ItemStack(BlockInit.deepslate), new ItemStack(BlockInit.polished_deepslate_slab, 2), "polished deepslate slab");

        addRecipe(new ItemStack(BlockInit.deepslate), new ItemStack(BlockInit.cracked_deepslate_bricks, 1), "cracked deepslate bricks");

        addRecipe(new ItemStack(BlockInit.deepslate), new ItemStack(BlockInit.chiseled_deepslate, 1), "chiseled deepslate");

        addRecipe(new ItemStack(BlockInit.deepslate), new ItemStack(BlockInit.deepslate_tiles, 1), "deepslate tiles");
        addRecipe(new ItemStack(BlockInit.deepslate), new ItemStack(BlockInit.cracked_deepslate_tiles, 1), "cracked deepslate tiles");


        //POLISHED DEEPSLATE
        addRecipe(new ItemStack(BlockInit.polished_deepslate), new ItemStack(BlockInit.polished_deepslate_wall, 1), "polished deepslate wall");
        addRecipe(new ItemStack(BlockInit.polished_deepslate), new ItemStack(BlockInit.polished_deepslate_stairs, 1), "polished deepslate stairs");
        addRecipe(new ItemStack(BlockInit.polished_deepslate), new ItemStack(BlockInit.polished_deepslate_slab, 2), "polished deepslate slab");

        //BRICKS DEEPSLATE
        addRecipe(new ItemStack(BlockInit.deepslate_bricks), new ItemStack(BlockInit.deepslate_bricks_slab, 2), "deepslate slab");
        addRecipe(new ItemStack(BlockInit.deepslate_bricks), new ItemStack(BlockInit.deepslate_bricks_wall, 1), "deepslate wall");
        addRecipe(new ItemStack(BlockInit.deepslate_bricks), new ItemStack(BlockInit.deepslate_bricks_stairs, 1), "deepslate stairs");
        addRecipe(new ItemStack(BlockInit.deepslate_bricks), new ItemStack(BlockInit.cracked_deepslate_bricks, 1), "cracked deepslate bricks");

        //DEEPSLATE_TILES
        addRecipe(new ItemStack(BlockInit.deepslate_tiles), new ItemStack(BlockInit.cracked_deepslate_tiles, 1), "cracked deepslate tiles");

        //COBBLED DEEPSLATE — те же варианты, что и у дипслейта
        for (StoneCutterRecipe r : new ArrayList<>(RECIPES)) {
            if (r.getInput().getItem() == Item.getItemFromBlock(BlockInit.deepslate)) {
                addRecipe(new ItemStack(BlockInit.cobbled_deepslate), r.getOutput(), r.getRecipeName());
            }
        }


    }

    // Индекс "предмет+мета -> рецепты". getRecipesForInput вызывается при каждом изменении
    // входного слота (и на каждом Shift-клике), поэтому линейный перебор всех рецептов
    // с созданием нового списка заменён на поиск в HashMap и общий неизменяемый список.
    private static long key(Item item, int meta) {
        return ((long) Item.getIdFromItem(item) << 16) | (meta & 0xFFFF);
    }

    public static synchronized void addRecipe(ItemStack input, ItemStack output, String name) {
        StoneCutterRecipe recipe = new StoneCutterRecipe(input, output, name);
        RECIPES.add(recipe);
        INDEX.computeIfAbsent(key(input.getItem(), input.getMetadata()), k -> new ArrayList<>()).add(recipe);
    }

    public static synchronized List<StoneCutterRecipe> getRecipesForInput(ItemStack input) {
        if (input.isEmpty()) return Collections.emptyList();
        List<StoneCutterRecipe> list = INDEX.get(key(input.getItem(), input.getMetadata()));
        return list == null ? Collections.<StoneCutterRecipe>emptyList() : Collections.unmodifiableList(list);
    }

    public static List<StoneCutterRecipe> getAllRecipes() {
        return new ArrayList<>(RECIPES);
    }
}
