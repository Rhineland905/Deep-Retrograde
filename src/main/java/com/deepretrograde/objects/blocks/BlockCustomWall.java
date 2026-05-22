package com.deepretrograde.objects.blocks;

import com.deepretrograde.DeepRetrograde;
import com.deepretrograde.init.BlockInit;
import com.deepretrograde.init.ItemInit;
import com.deepretrograde.util.IHasModel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWall;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class BlockCustomWall extends BlockWall implements IHasModel {

    public BlockCustomWall(String name, Block modelBlock) {

        super(modelBlock);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);

        BlockInit.BLOCKS.add(this);
        ItemInit.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }


    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this));
    }

    @Override
    public void registerModels() {
        DeepRetrograde.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }
}