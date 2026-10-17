package com.deepretrograde.objects.blocks;

import com.deepretrograde.DeepRetrograde;
import com.deepretrograde.util.handlers.GuiHandler;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockStoneCutter extends BlockBase {

    // Модель: основание 16×9×16 + пила сверху. Хитбокс по основанию, как в ванилле 1.14+
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 9.0D / 16.0D, 1.0D);

    public BlockStoneCutter(String name, Material material) {
        super(name, material);
        setHardness(3.5F);
        setResistance(3.5F);
        setSoundType(SoundType.STONE);
        setHarvestLevel("pickaxe", 0);

        this.lightOpacity = 0;
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
                                    EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                    float hitX, float hitY, float hitZ) {

        if (!worldIn.isRemote) {
            playerIn.openGui(DeepRetrograde.instance, GuiHandler.STONECUTTER, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABB;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }

    @Override
    public boolean isFullCube(IBlockState state) { return false; }

    // В 1.12 метод называется getRenderLayer (getBlockLayer был в 1.11 и раньше).
    // Без @Override старое имя молча не переопределяло ничего → блок рисовался в SOLID,
    // и прозрачные пиксели пилы становились чёрными (баг на скриншоте).
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
