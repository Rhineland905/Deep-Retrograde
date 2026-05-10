
package com.example.objects.blocks;

import com.example.init.SoundInit;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import java.util.Random;

public class BlockLapisLazuliOre extends BlockBase {

    public BlockLapisLazuliOre(String name, Material material) {
        super(name, material);
        setSoundType(SoundInit.DEEPSLATE_SOUNDS);
        setHardness(4.5F);
        setResistance(6.0F);
        // Лазурит требует каменную кирку или выше (уровень 1)
        this.setHarvestLevel("pickaxe", 1);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        // В 1.12.2 лазурит — это краситель (Dye)
        return Items.DYE;
    }

    @Override
    public int damageDropped(IBlockState state) {
        // Метаданные 4 соответствуют синему красителю (лазуриту)
        return EnumDyeColor.BLUE.getDyeDamage();
    }

    @Override
    public int quantityDropped(Random random) {
        // Ванильный лазурит дропает от 4 до 9 единиц
        return 4 + random.nextInt(6);
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
        // Лазурит дает от 2 до 5 опыта
        return MathHelper.getInt(rand, 2, 5);
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