package com.deepretrograde;

import com.deepretrograde.init.RecipesInit;
import com.deepretrograde.init.Reference;
import com.deepretrograde.world.CubicWorldGen;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import com.deepretrograde.proxy.CommonProxy;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;


@Mod(
		modid = Reference.MODID,
		name = Reference.NAME,
		version = Reference.VERSION,
		// Загружаемся ДО CubicChunks, чтобы наш preInit пропатчил конфиг
		// прежде чем CubicChunks его прочитает — без перезапуска
		dependencies = "before:cubicchunks"
)
public class DeepRetrograde {

	private static final Logger LOGGER = LogManager.getLogger("DeepRetrograde");

	@Mod.Instance()
	public static DeepRetrograde instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
	public static CommonProxy proxy;

	private File cubicConfigDir = null;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		instance = this;
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new com.deepretrograde.util.handlers.GuiHandler());
		if (Loader.isModLoaded("cubicchunks")) {
			cubicConfigDir = event.getModConfigurationDirectory();
			// Патчим ДО того, как CubicChunks читает свой конфиг (благодаря before:cubicchunks)
			patchCubicChunksConfig(cubicConfigDir);
		}
	}

	/**
	 * Автоматически выставляет нужные значения в cubicchunks.cfg.
	 * Срабатывает при каждом запуске — идемпотентно (повторный запуск ничего не ломает).
	 *
	 * Патчи:
	 *   forceLoadCubicChunks=NEW_WORLD  — новые миры автоматически кубические
	 *   useVanillaChunkWorldGenerators=false — не ломать ICubicPopulator
	 *   defaultMinHeight=-64            — дно мира как в 1.21.1 (граница на Y=-64)
	 *   defaultMaxHeight=320            — высота 320 (=16×20, как в 1.18+; ближайшее к 300 кратное 16)
	 */
	private void patchCubicChunksConfig(File configDir) {
		File cfg = new File(configDir, "cubicchunks.cfg");
		if (!cfg.exists()) {
			LOGGER.info("[DeepRetrograde] cubicchunks.cfg не найден, пропускаем патч.");
			return;
		}
		try {
			String content = new String(Files.readAllBytes(cfg.toPath()), StandardCharsets.UTF_8);
			String patched = content
					// Миры должны создаваться кубическими без выбора типа вручную
					.replace("S:forceLoadCubicChunks=NONE",          "S:forceLoadCubicChunks=NEW_WORLD")
					// Ванильные генераторы ломают ICubicPopulator — держим выключенными
					.replace("B:useVanillaChunkWorldGenerators=true", "B:useVanillaChunkWorldGenerators=false")
					// Дно мира = Y=-64, как в 1.21.1
					.replaceAll("I:defaultMinHeight=-[0-9]+",         "I:defaultMinHeight=-64")
					// Верхняя граница: 320 = 16×20 (как в 1.18+, ближайшее кратное 16 к 300)
					.replaceAll("I:defaultMaxHeight=[0-9]+",          "I:defaultMaxHeight=320");

			if (!patched.equals(content)) {
				Files.write(cfg.toPath(), patched.getBytes(StandardCharsets.UTF_8));
				LOGGER.info("[DeepRetrograde] cubicchunks.cfg обновлён (min=-64, max=320).");
			} else {
				LOGGER.info("[DeepRetrograde] cubicchunks.cfg уже актуален.");
			}
		} catch (IOException e) {
			LOGGER.warn("[DeepRetrograde] Не удалось обновить cubicchunks.cfg: " + e.getMessage());
		}
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		RecipesInit.init();

		// CubicChunks: настоящие отрицательные Y как в 1.21.1
		if (Loader.isModLoaded("cubicchunks")) {
			registerCubicWorldGen();
		}
	}

	@Optional.Method(modid = "cubicchunks")
	private void registerCubicWorldGen() {
		io.github.opencubicchunks.cubicchunks.api.worldgen.CubeGeneratorsRegistry
				.register(new CubicWorldGen(), 0);
		// Compatibility mode (cubicchunks:default — обёртка ванильного генератора)
		io.github.opencubicchunks.cubicchunks.api.worldgen.CubeGeneratorsRegistry
				.registerForCompatibilityGenerator(new CubicWorldGen());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		// postInit — все моды (включая CubicChunks) уже записали свои конфиги.
		// Теперь наши правки точно не будут перезаписаны до следующего запуска.
		if (cubicConfigDir != null) {
			patchCubicChunksConfig(cubicConfigDir);
		}
		// Принудительно обновляем значения в памяти CubicChunks через рефлексию —
		// это позволяет конфигу вступить в силу БЕЗ рестарта игры.
		if (Loader.isModLoaded("cubicchunks")) {
			forceSyncCubicChunksConfigInMemory();
		}
	}

	/**
	 * Напрямую выставляет нужные значения в статических полях CubicChunksConfig через рефлексию.
	 * Все поля публичные (public static), ForceCCMode — enum с известными константами.
	 *
	 * Это позволяет изменениям вступить в силу сразу в текущей сессии без перезапуска Minecraft.
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private void forceSyncCubicChunksConfigInMemory() {
		try {
			Class<?> cfgClass  = Class.forName("io.github.opencubicchunks.cubicchunks.core.CubicChunksConfig");
			Class<?> modeClass = Class.forName("io.github.opencubicchunks.cubicchunks.core.CubicChunksConfig$ForceCCMode");

			// forceLoadCubicChunks = NEW_WORLD
			Object newWorld = Enum.valueOf((Class<Enum>) modeClass, "NEW_WORLD");
			java.lang.reflect.Field fForce = cfgClass.getField("forceLoadCubicChunks");
			fForce.set(null, newWorld);

			// useVanillaChunkWorldGenerators = false
			java.lang.reflect.Field fVanilla = cfgClass.getField("useVanillaChunkWorldGenerators");
			fVanilla.set(null, false);

			// defaultMinHeight = -64
			java.lang.reflect.Field fMinH = cfgClass.getField("defaultMinHeight");
			fMinH.set(null, -64);

			// defaultMaxHeight = 320  (16×20, как в 1.18+; ближайшее кратное 16 к 300)
			java.lang.reflect.Field fMaxH = cfgClass.getField("defaultMaxHeight");
			fMaxH.set(null, 320);

			LOGGER.info("[DeepRetrograde] CubicChunks config синхронизирован в памяти (min=-64, max=320) — рестарт не нужен.");
		} catch (Exception e) {
			LOGGER.warn("[DeepRetrograde] Не удалось синхронизировать CubicChunks config в памяти: " + e.getMessage());
		}
	}



}
