package com.deepretrograde.objects.items;

import com.deepretrograde.DeepRetrograde;
import com.deepretrograde.init.ItemInit;
import com.deepretrograde.util.IHasModel;


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
        DeepRetrograde.proxy.registerItemRenderer(this,0,"inventory");
    }
}
