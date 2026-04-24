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
}