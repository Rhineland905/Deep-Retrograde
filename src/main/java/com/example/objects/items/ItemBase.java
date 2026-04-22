package com.example.objects.items;

import com.example.SampleMod112;
import com.example.init.ItemInit;
import com.example.util.IHasModel;


import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBase extends Item implements IHasModel {
    public ItemBase(String name) {
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);

        ItemInit.ITEMS.add(this);
    }
    @Override
    public void registerModels() {
        SampleMod112.proxy.registerItemRenderer(this,0,"inventory");
    }
}
