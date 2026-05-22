package com.deepretrograde.objects.blocks;

import com.deepretrograde.DeepRetrograde;
import com.deepretrograde.init.BlockInit;
import com.deepretrograde.init.ItemInit;
import com.deepretrograde.util.IHasModel;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class BlockBase extends Block implements IHasModel {
    public BlockBase(String name, Material material) {
        super(material);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);

        BlockInit.BLOCKS.add(this);
        ItemInit.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }
    @Override
    public void registerModels() {
        DeepRetrograde.proxy.registerItemRenderer(Item.getItemFromBlock(this),0,"inventory");
    }
}
