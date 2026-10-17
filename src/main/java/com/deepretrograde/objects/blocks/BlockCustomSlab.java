package com.deepretrograde.objects.blocks;

import com.deepretrograde.DeepRetrograde;
import com.deepretrograde.init.BlockInit;
import com.deepretrograde.init.ItemInit;
import com.deepretrograde.util.IHasModel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSlab;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public abstract class BlockCustomSlab extends BlockSlab implements IHasModel {
    // Вспомогательное свойство, которое требует Майнкрафт для плит
    public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

    // Ссылка на одинарную плиту: двойная дропает и "пикает" именно её
    protected BlockSlab halfSlab;

    @SuppressWarnings("deprecation")
    public BlockCustomSlab(String name, Block base) {
        super(base.getDefaultState().getMaterial());
        setTranslationKey(name);
        setRegistryName(name);
        this.useNeighborBrightness = true; // Убираем черные тени

        // Раньше прочность не задавалась (0 = ломалась мгновенно рукой), звук был каменный.
        // Берём всё у базового блока, как это делают ванильные ступеньки.
        IBlockState baseState = base.getDefaultState();
        setHardness(baseState.getBlockHardness(null, null));
        setResistance(base.getExplosionResistance(null) * 5.0F / 3.0F);
        setSoundType(base.getSoundType());
        String tool = base.getHarvestTool(baseState);
        if (tool != null) setHarvestLevel(tool, base.getHarvestLevel(baseState));

        IBlockState state = this.blockState.getBaseState().withProperty(VARIANT, Variant.DEFAULT);
        if (!this.isDouble()) {
            state = state.withProperty(HALF, EnumBlockHalf.BOTTOM);
        }
        this.setDefaultState(state);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(halfSlab != null ? halfSlab : this);
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(halfSlab != null ? halfSlab : this);
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
        public Double(String name, Block base) {
            super(name, base);
            BlockInit.BLOCKS.add(this);
        }
        @Override public boolean isDouble() { return true; }
        @Override public void registerModels() {}
    }

    public static class Half extends BlockCustomSlab {
        public Half(String name, Block base, BlockSlab doubleSlab) {
            super(name, base);
            this.halfSlab = this;
            if (doubleSlab instanceof BlockCustomSlab) ((BlockCustomSlab) doubleSlab).halfSlab = this;
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            BlockInit.BLOCKS.add(this);
            // Регистрируем предмет-плиту, которая знает, как превращаться в двойную!
            ItemInit.ITEMS.add(new ItemSlab(this, this, doubleSlab).setRegistryName(name));
        }
        @Override public boolean isDouble() { return false; }
        @Override public void registerModels() {
            DeepRetrograde.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
        }
    }
}