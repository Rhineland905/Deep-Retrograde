package com.example.objects.blocks;

import com.example.SampleMod112;
import com.example.init.BlockInit;
import com.example.init.ItemInit;
import com.example.util.IHasModel;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public abstract class BlockCustomSlab extends BlockSlab implements IHasModel {
    // Вспомогательное свойство, которое требует Майнкрафт для плит
    public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

    public BlockCustomSlab(String name, Material material) {
        super(material);
        setTranslationKey(name);
        setRegistryName(name);
        this.useNeighborBrightness = true; // Убираем черные тени

        IBlockState state = this.blockState.getBaseState().withProperty(VARIANT, Variant.DEFAULT);
        if (!this.isDouble()) {
            state = state.withProperty(HALF, EnumBlockHalf.BOTTOM);
        }
        this.setDefaultState(state);
    }

    @Override
    public String getTranslationKey(int meta) { return super.getTranslationKey(); }

    @Override
    public IProperty<?> getVariantProperty() { return VARIANT; }

    // --- ВОТ ТА САМАЯ ИСПРАВЛЕННАЯ СТРОЧКА ---
    @Override
    public Comparable<?> getTypeForItem(ItemStack stack) { return Variant.DEFAULT; }

    @Override
    protected BlockStateContainer createBlockState() {
        return this.isDouble() ? new BlockStateContainer(this, VARIANT) : new BlockStateContainer(this, HALF, VARIANT);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState().withProperty(VARIANT, Variant.DEFAULT);
        if (!this.isDouble()) {
            state = state.withProperty(HALF, (meta & 8) == 0 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
        }
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = 0;
        if (!this.isDouble() && state.getValue(HALF) == EnumBlockHalf.TOP) {
            meta |= 8;
        }
        return meta;
    }

    public enum Variant implements IStringSerializable {
        DEFAULT;
        @Override public String getName() { return "default"; }
    }

    // --- МАГИЯ: ДВА КЛАССА ВНУТРИ ОДНОГО ДЛЯ ДВОЙНОЙ И ОДИНАРНОЙ ПЛИТЫ ---

    public static class Double extends BlockCustomSlab {
        public Double(String name, Material material) {
            super(name, material);
            BlockInit.BLOCKS.add(this);
        }
        @Override public boolean isDouble() { return true; }
        @Override public void registerModels() {}
    }

    public static class Half extends BlockCustomSlab {
        public Half(String name, Material material, BlockSlab doubleSlab) {
            super(name, material);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            BlockInit.BLOCKS.add(this);
            // Регистрируем предмет-плиту, которая знает, как превращаться в двойную!
            ItemInit.ITEMS.add(new ItemSlab(this, this, doubleSlab).setRegistryName(name));
        }
        @Override public boolean isDouble() { return false; }
        @Override public void registerModels() {
            SampleMod112.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
        }
    }
}