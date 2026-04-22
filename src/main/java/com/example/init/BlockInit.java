package com.example.init;

import com.example.objects.blocks.BlockBase;
import com.example.objects.blocks.BlockCalcite;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

public class BlockInit {
    public static final List<Block> BLOCKS = new ArrayList<Block>();

    public static final Block calcite = new BlockCalcite("calcite", Material.ROCK);

}
