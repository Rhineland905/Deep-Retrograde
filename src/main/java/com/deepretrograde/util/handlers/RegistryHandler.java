package com.deepretrograde.util.handlers;

import com.deepretrograde.DeepRetrograde;
import com.deepretrograde.init.BlockInit;
import com.deepretrograde.init.ItemInit;
import com.deepretrograde.init.SoundInit;
import com.deepretrograde.util.IHasModel;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod.EventBusSubscriber
public class RegistryHandler {

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(ItemInit.ITEMS.toArray(new Item[0]));
    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(BlockInit.BLOCKS.toArray(new Block[0]));
    }

    @SubscribeEvent
    public static void onSoundRegister(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(
                SoundInit.CALCITE_BREAK, SoundInit.CALCITE_STEP, SoundInit.CALCITE_PLACE,
                SoundInit.SCULK_PLACE, SoundInit.SCULK_BREAK, SoundInit.SCULK_STEP,
                SoundInit.TUFF_PLACE, SoundInit.TUFF_BREAK, SoundInit.TUFF_STEP,
                SoundInit.TUFF_BRICKS_PLACE, SoundInit.TUFF_BRICKS_STEP,
                SoundInit.DEEPSLATE_BREAK, SoundInit.DEEPSLATE_STEP, SoundInit.DEEPSLATE_PLACE,
                SoundInit.DRIPSTONE_BREAK, SoundInit.DRIPSTONE_STEP,
                SoundInit.STONECUTTER_USE
        );
    }

    // Исправлено событие на ModelRegistryEvent
    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        for (Item item : ItemInit.ITEMS) {
            if(item instanceof IHasModel){
                ((IHasModel)item).registerModels();
            }
        }
        for(Block block : BlockInit.BLOCKS){
            if(block instanceof IHasModel){
                ((IHasModel)block).registerModels();
            }
        }
    }
}