
package com.example.objects.blocks;

import com.example.init.SoundInit;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import java.util.Random;

public class BlockIronOre extends BlockBase {

    public BlockIronOre(String name, Material material) {
        super(name, material);
        setSoundType(SoundInit.DEEPSLATE_SOUNDS);
        setHardness(4.5F);
        setResistance(3.0F);
        this.setHarvestLevel("pickaxe", 1);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }


}