package com.deepretrograde.objects.blocks;

import com.deepretrograde.DeepRetrograde;
import com.deepretrograde.init.BlockInit;
import com.deepretrograde.init.ItemInit;
import com.deepretrograde.util.IHasModel;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class BlockCustomStairs extends BlockStairs implements IHasModel {

    public BlockCustomStairs(String name, IBlockState modelState) {
        // modelState передает ступенькам свойства базового блока (например, прочность)
        super(modelState);
        setTranslationKey(name);
        setRegistryName(name);

        // ВАЖНО: Эта строчка убирает черные тени под ступеньками!
        this.useNeighborBrightness = true;

        BlockInit.BLOCKS.add(this);
        ItemInit.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

    @Override
    public void registerModels() {
        DeepRetrograde.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }
}