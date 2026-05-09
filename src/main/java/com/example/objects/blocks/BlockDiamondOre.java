package com.example.objects.blocks;

import com.example.init.SoundInit;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import java.util.Random;

public class BlockDiamondOre extends BlockBase {

    public BlockDiamondOre(String name, Material material) {
        super(name, material);
        setSoundType(SoundInit.DEEPSLATE_SOUNDS);
        setHardness(4.5F);
        setResistance(6.0F);
        this.setHarvestLevel("pickaxe", 2);

        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.DIAMOND;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        if (fortune > 0) {
            int i = random.nextInt(fortune + 2) - 1;
            if (i < 0) i = 0;
            return this.quantityDropped(random) * (i + 1);
        } else {
            return this.quantityDropped(random);
        }
    }

    @Override
    public int getExpDrop(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune) {
        Random rand = world instanceof World ? ((World)world).rand : new Random();
        if (this.getItemDropped(state, rand, fortune) != Item.getItemFromBlock(this)) {
            return MathHelper.getInt(rand, 3, 7);
        }
        return 0;
    }

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }

    @Override
    public ItemStack getSilkTouchDrop(IBlockState state) {
        return new ItemStack(this, 1);
    }
}