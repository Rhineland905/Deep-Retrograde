package com.deepretrograde.objects.blocks;

import com.deepretrograde.init.SoundInit;
import com.deepretrograde.util.IHasModel;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockCobbledDeepslate extends BlockBase implements IHasModel {

    public BlockCobbledDeepslate(String name, Material material) {
        super(name, material);
        setSoundType(SoundInit.DEEPSLATE_SOUNDS);
        setHardness(3.0F);
        setResistance(6.0F);
        this.setHarvestLevel("pickaxe", 1);
        setLightLevel(0.0F);
        setLightOpacity(255);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }
}