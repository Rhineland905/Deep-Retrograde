package com.example;

import com.example.init.RecipesInit;
import com.example.init.Reference;
import com.example.world.ModWorldGen;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import com.example.proxy.CommonProxy;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;


@Mod(
		modid = Reference.MODID,
		name = Reference.NAME,
		version = Reference.VERSION
)
public class SampleMod112 {

	@Mod.Instance()
	public static SampleMod112 instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		instance = this;
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new com.example.util.handlers.GuiHandler());

	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new ModWorldGen(), 0);
		RecipesInit.init();
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {}



}
