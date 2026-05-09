package com.example.init;

import com.example.objects.blocks.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

public class BlockInit {
    public static final List<Block> BLOCKS = new ArrayList<Block>();

    //calcite
    public static final Block calcite = new BlockCalcite("calcite", Material.ROCK);
    public static final Block calcite_stairs = new BlockCustomStairs("calcite_stairs", calcite.getDefaultState());
    public static final Block calcite_wall = new BlockCustomWall("calcite_wall", calcite);
    public static final BlockSlab calcite_slab_double = new BlockCustomSlab.Double("calcite_slab_double", Material.ROCK);
    public static final BlockSlab calcite_slab = new BlockCustomSlab.Half("calcite_slab", Material.ROCK, calcite_slab_double);

    //sculk
    public static final Block sculk = new BlockSculk("sculk",Material.CLAY);

    //tuf
    public static final Block tuff = new BlockTuff("tuff", Material.ROCK);
    public static final Block tuff_stairs = new BlockCustomStairs("tuff_stairs", tuff.getDefaultState());
    public static final Block tuff_wall = new BlockCustomWall("tuff_wall", tuff);
    public static final BlockSlab tuff_slab_double = new BlockCustomSlab.Double("tuff_slab_double", Material.ROCK);
    public static final BlockSlab tuff_slab = new BlockCustomSlab.Half("tuff_slab", Material.ROCK, tuff_slab_double);

    //tuff_bricks
    public static Block tuff_bricks = new BlockTuffBricks("tuff_bricks", Material.ROCK);
    public static final Block tuff_bricks_stairs = new BlockCustomStairs("tuff_bricks_stairs", tuff.getDefaultState());
    public static final Block tuff_bricks_wall = new BlockCustomWall("tuff_bricks_wall", tuff_bricks);
    public static final BlockSlab tuff_bricks_slab_double = new BlockCustomSlab.Double("tuff_bricks_slab_double", Material.ROCK);
    public static final BlockSlab tuff_bricks_slab = new BlockCustomSlab.Half("tuff_bricks_slab", Material.ROCK, tuff_bricks_slab_double);

    //polished_tuff
    public static Block tuff_polished = new BlockTuffBricks("tuff_polished", Material.ROCK);
    public static final Block tuff_polished_stairs = new BlockCustomStairs("tuff_polished_stairs", tuff_polished.getDefaultState());
    public static final Block tuff_polished_wall = new BlockCustomWall("tuff_polished_wall", tuff_polished);
    public static final BlockSlab tuff_polished_slab_double = new BlockCustomSlab.Double("tuff_polished_slab_double", Material.ROCK);
    public static final BlockSlab tuff_polished_slab = new BlockCustomSlab.Half("tuff_polished_slab", Material.ROCK, tuff_polished_slab_double);

    //chiseled_tuff
    public static Block chiseled_tuff = new BlockTuff("chiseled_tuff", Material.ROCK);

    //chiseled_tuff_bricks
    public static Block chiseled_tuff_bricks = new BlockTuff("chiseled_tuff_bricks", Material.ROCK);

    //StoneCutter
    public static Block stonecutter = new  BlockStoneCutter("stonecutter", Material.ROCK);

    //Deepslate
    public static  Block deepslate= new BlockDeepslate("deepslate", Material.ROCK);

    //Cobbled Deepslate
    public static  Block cobbled_deepslate= new BlockCobbledDeepslate("cobbled_deepslate", Material.ROCK);


    //Deepslate_bricks
    public static  Block deepslate_bricks= new BlockCobbledDeepslate("deepslate_bricks", Material.ROCK);
    public static  Block deepslate_bricks_stairs= new BlockCustomStairs("deepslate_bricks_stairs", deepslate_bricks.getDefaultState());
    public static final Block deepslate_bricks_wall = new BlockCustomWall("deepslate_bricks_wall", deepslate_bricks);
    public static final BlockSlab deepslate_bricks_slab_double = new BlockCustomSlab.Double("deepslate_bricks_slab_double", Material.ROCK);
    public static final BlockSlab deepslate_bricks_slab = new BlockCustomSlab.Half("deepslate_bricks_slab", Material.ROCK, deepslate_bricks_slab_double);

    //Polished Deepslate
    public static  Block polished_deepslate= new BlockCobbledDeepslate("polished_deepslate", Material.ROCK);
    public static  Block polished_deepslate_stairs= new BlockCustomStairs("polished_deepslate_stairs", polished_deepslate.getDefaultState());
    public static final Block polished_deepslate_wall = new BlockCustomWall("polished_deepslate_wall", deepslate_bricks);
    public static final BlockSlab polished_deepslate_slab_double = new BlockCustomSlab.Double("polished_deepslate_slab_double", Material.ROCK);
    public static final BlockSlab polished_deepslate_slab = new BlockCustomSlab.Half("polished_deepslate_slab", Material.ROCK, polished_deepslate_slab_double);

    //Chiseled Deepslate
    public static  Block chiseled_deepslate= new BlockCobbledDeepslate("chiseled_deepslate", Material.ROCK);

    //Cracked Deepslate Bricks
    public static  Block cracked_deepslate_bricks= new BlockCobbledDeepslate("cracked_deepslate_bricks", Material.ROCK);

    //Cracked Deepslate Tiles
    public static  Block cracked_deepslate_tiles= new BlockCobbledDeepslate("cracked_deepslate_tiles", Material.ROCK);

    //Deepslate Tiles
    public static  Block deepslate_tiles= new BlockCobbledDeepslate("deepslate_tiles", Material.ROCK);

    //Deepslate Coal Ore
    public static  Block deepslate_coal_ore= new BlockCoalOre("deepslate_coal_ore", Material.ROCK);

    //Deepslate Diamond Ore
    public static  Block deepslate_diamond_ore= new BlockDiamondOre("deepslate_diamond_ore", Material.ROCK);

    //Deepslate Emerald Ore
    public static  Block deepslate_emerald_ore= new BlockEmeraldOre("deepslate_emerald_ore", Material.ROCK);

    //Deepslate Gold Ore
    public static  Block deepslate_gold_ore= new BlockGoldOre("deepslate_gold_ore", Material.ROCK);

    //Deepslate Iron Ore
    public static  Block deepslate_iron_ore= new BlockIronOre("deepslate_iron_ore", Material.ROCK);

    //Dripstone Block
    public static  Block dripstone_block= new BlockDripstone("dripstone_block", Material.ROCK);

}