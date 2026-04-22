package com.example;

import com.example.init.Reference;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import com.example.proxy.CommonProxy;



@Mod(
	modid = Reference.MODID,
	name = Reference.NAME,
	version = Reference.VERSION
)
public class SampleMod112 {
	public static final String MODID = "samplemod112";
	public static final String NAME = "Sample Mod 1.12";
	public static final String VERSION = "1.0";

	@Mod.Instance()
	public static SampleMod112 instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
	public static CommonProxy proxy;


	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {}
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {}
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {}

}
