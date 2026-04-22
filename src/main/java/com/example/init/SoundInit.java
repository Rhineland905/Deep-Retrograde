package com.example.init;

import net.minecraft.block.SoundType; // Нам понадобится этот импорт!
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class SoundInit {


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
}