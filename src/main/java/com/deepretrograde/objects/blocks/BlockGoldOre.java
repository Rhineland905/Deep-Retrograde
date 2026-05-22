
package com.deepretrograde.objects.blocks;

import com.deepretrograde.init.SoundInit;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;


public class BlockGoldOre extends BlockBase {

    public BlockGoldOre(String name, Material material) {
        super(name, material);
        setSoundType(SoundInit.DEEPSLATE_SOUNDS);
        setHardness(4.5F);
        setResistance(3.0F);
        this.setHarvestLevel("pickaxe", 2);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

}