package com.deepretrograde;

import com.deepretrograde.init.RecipesInit;
import com.deepretrograde.init.Reference;
import com.deepretrograde.world.ModWorldGen;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import com.deepretrograde.proxy.CommonProxy;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;


@Mod(
		modid = Reference.MODID,
		name = Reference.NAME,
		version = Reference.VERSION
)
public class DeepRetrograde {

	@Mod.Instance()
	public static DeepRetrograde instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
	public static CommonProxy proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		instance = this;
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new com.deepretrograde.util.handlers.GuiHandler());

	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.registerWorldGenerator(new ModWorldGen(), 0);
		RecipesInit.init();
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {}



}
