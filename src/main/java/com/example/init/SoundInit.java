package com.example.init;

import net.minecraft.block.SoundType; // Нам понадобится этот импорт!
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class SoundInit {

    //CALCITE
    public static final SoundEvent CALCITE_STEP = new SoundEvent(new ResourceLocation("samplemod112", "calcite_step")).setRegistryName("calcite_step");
    public static final SoundEvent CALCITE_BREAK = new SoundEvent(new ResourceLocation("samplemod112", "calcite_break")).setRegistryName("calcite_break");
    public static final SoundEvent CALCITE_PLACE = new SoundEvent(new ResourceLocation("samplemod112", "calcite_place")).setRegistryName("calcite_place");

    public static final SoundType CALCITE_SOUNDS = new SoundType(
            1.0F,
            1.0F,
            CALCITE_BREAK,
            CALCITE_STEP,
            CALCITE_PLACE,
            CALCITE_STEP,
            CALCITE_STEP
    );
    //SCULK
    public static final SoundEvent SCULK_PLACE = new SoundEvent(new ResourceLocation("samplemod112", "sculk_place")).setRegistryName("sculk_place");
    public static final SoundEvent SCULK_BREAK = new SoundEvent(new ResourceLocation("samplemod112", "sculk_break")).setRegistryName("sculk_break");
    public static final SoundEvent SCULK_STEP = new SoundEvent(new ResourceLocation("samplemod112", "sculk_step")).setRegistryName("sculk_step");

    public static final SoundType SCULK_SOUNDS = new SoundType(
            1.0F,
            1.0F,
            SCULK_BREAK,
            SCULK_STEP,
            SCULK_PLACE,
            SCULK_STEP,
            SCULK_STEP
    );
    //TUFF
    public static final SoundEvent TUFF_PLACE = new SoundEvent(new ResourceLocation("samplemod112", "tuff_place")).setRegistryName("tuff_place");
    public static final SoundEvent TUFF_BREAK = new SoundEvent(new ResourceLocation("samplemod112", "tuff_break")).setRegistryName("tuff_break");
    public static final SoundEvent TUFF_STEP = new SoundEvent(new ResourceLocation("samplemod112", "tuff_step")).setRegistryName("tuff_step");


    public static final SoundType TUFF_SOUNDS = new SoundType(
            1.0F,
            1.0F,
            TUFF_BREAK,
            TUFF_STEP,
            TUFF_PLACE,
            TUFF_STEP,
            TUFF_STEP
    );
    //TUFF_BRICKS
    public static final SoundEvent TUFF_BRICKS_BREAK = new SoundEvent(new ResourceLocation("samplemod112", "tuff_bricks_break")).setRegistryName("tuff_bricks_break");
    public static final SoundEvent TUFF_BRICKS_STEP = new SoundEvent(new ResourceLocation("samplemod112", "tuff_bricks_step")).setRegistryName("tuff_bricks_step");

    public static final SoundType TUFF_BRICKS_SOUNDS = new SoundType(
            1.0F,
            1.0F,
            TUFF_BRICKS_BREAK,
            TUFF_BRICKS_STEP,
            TUFF_PLACE,
            TUFF_STEP,
            TUFF_STEP
    );

    //DEEPSLATE
    public static final SoundEvent DEEPSLATE_BREAK = new SoundEvent(new ResourceLocation("samplemod112", "deepslate_break")).setRegistryName("deepslate_break");
    public static final SoundEvent DEEPSLATE_STEP = new SoundEvent(new ResourceLocation("samplemod112", "deepslate_step")).setRegistryName("deepslate_step");
    public static final SoundEvent DEEPSLATE_PLACE = new SoundEvent(new ResourceLocation("samplemod112", "deepslate_place")).setRegistryName("deepslate_place");


    public static final SoundType DEEPSLATE_SOUNDS = new SoundType(
            1.0F,
            1.0F,
            DEEPSLATE_BREAK,
            DEEPSLATE_STEP,
            DEEPSLATE_PLACE,
            DEEPSLATE_STEP,
            DEEPSLATE_STEP
    );

    //STONECUTTER
    public static final SoundEvent STONECUTTER_USE = new SoundEvent(new ResourceLocation("samplemod112", "stonecutter_use")).setRegistryName("stonecutter_use");

    //DRIPSTONE
    public static final SoundEvent DRIPSTONE_BREAK = new SoundEvent(new ResourceLocation("samplemod112", "dripstone_break")).setRegistryName("dripstone_break");
    public static final SoundEvent DRIPSTONE_STEP = new SoundEvent(new ResourceLocation("samplemod112", "dripstone_step")).setRegistryName("dripstone_step");

    public static final SoundType DRIPSTONE_SOUNDS = new SoundType(
            1.0F,
            1.0F,
            DRIPSTONE_BREAK,
            DRIPSTONE_STEP,
            DRIPSTONE_BREAK,
            DRIPSTONE_STEP,
            DRIPSTONE_STEP
    );
}