package org.cfpa;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.LanguageDefinition;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class I18nUpdateMod implements ClientModInitializer {
	public static final String MOD_ID = "i18nupdatemod";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public final static Path CACHE_DIR = Paths.get(System.getProperty("user.home"), "." + MOD_ID, "1.16.5");
	public final static Path RESOURCE_FOLDER = Paths.get(MinecraftClient.getInstance().runDirectory.getPath(), "resourcepacks");
	public final static Path LOCAL_LANGUAGE_PACK = RESOURCE_FOLDER.resolve("Minecraft-Mod-Language-Modpack-1-16.zip");
	public final static Path LANGUAGE_PACK = CACHE_DIR.resolve("Minecraft-Mod-Language-Modpack-1-16.zip");
	public final static Path LANGUAGE_MD5 = CACHE_DIR.resolve("1.16.md5");
	public final static String LINK = "http://downloader1.meitangdehulu.com:22943/Minecraft-Mod-Language-Modpack-1-16.zip";
	public final static String MD5 = "http://downloader1.meitangdehulu.com:22943/1.16.md5";
	public static String MD5String = "";

	@Override
	public void onInitializeClient() {
		MinecraftClient mc = MinecraftClient.getInstance();
		mc.getLanguageManager().setLanguage(new LanguageDefinition("zh_cn","中文（简体）","China",false));

		// 检查主资源包目录是否存在
		if (!Files.isDirectory(CACHE_DIR)) {
			try {
				Files.createDirectories(CACHE_DIR);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		//检查游戏下资源包目录
		if (!Files.isDirectory(RESOURCE_FOLDER)) {
			try {
				Files.createDirectories(RESOURCE_FOLDER);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		//尝试加载MD5文件
		try {
			FileUtils.copyURLToFile(new URL(MD5), LANGUAGE_MD5.toFile());
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error("Download MD5 failed.");
			return;
		}
		try {
			StringBuilder stringBuffer = new StringBuilder();
			List<String> lines = Files.readAllLines(LANGUAGE_MD5);
			for (String line : lines) {
				stringBuffer.append(line);
				MD5String = stringBuffer.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}


		if (Files.exists(LANGUAGE_PACK)) {
			String md5;
			try {
				InputStream is = Files.newInputStream(LANGUAGE_PACK);
				md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is).toUpperCase();
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.error("Error when compute md5.");
				return;
			}
			try {
				if (!md5.equals(MD5String)) {
					FileUtils.copyURLToFile(new URL(LINK), LANGUAGE_PACK.toFile());
					Files.delete(LOCAL_LANGUAGE_PACK);
					Files.copy(LANGUAGE_PACK, LOCAL_LANGUAGE_PACK);
				}
			} catch (MalformedURLException e) {
				LOGGER.error("Download Langpack failed.");
				e.printStackTrace();
				return;
			} catch (IOException e) {
				LOGGER.error("Error when copy file.");
				e.printStackTrace();
				return;
			}
			setResourcesRepository(mc);
		} else {
			try {
				FileUtils.copyURLToFile(new URL(LINK), LANGUAGE_PACK.toFile());
				Files.copy(LANGUAGE_PACK, LOCAL_LANGUAGE_PACK);
			} catch (IOException e) {
				LOGGER.error("Download Langpack failed.");
				e.printStackTrace();
				return;
			}
			try {
				setResourcesRepository(mc);
				//Minecraft.getInstance().getResourcePackRepository().addPackFinder(new LanguagePackFinder());
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}

		if(!Files.exists(LOCAL_LANGUAGE_PACK)){
			try {
				Files.copy(LANGUAGE_PACK, LOCAL_LANGUAGE_PACK);
				//InputStream is1 = Files.newInputStream(LOCAL_LANGUAGE_PACK);
				//InputStream is2 = Files.newInputStream(LANGUAGE_PACK);
				//String md51 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is1).toUpperCase();
				//String md52 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is2).toUpperCase();
				//if (!md51.equals(md52)){
				//Files.delete(LOCAL_LANGUAGE_PACK);
				//Files.copy(LANGUAGE_PACK, LOCAL_LANGUAGE_PACK);
				//}
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.error("Error when copy file.");
				return;
			}
			try {
				setResourcesRepository(mc);
				//Minecraft.getInstance().getResourcePackRepository().addPackFinder(new LanguagePackFinder());
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}

		setResourcesRepository(mc);
	}



	public static void setResourcesRepository(MinecraftClient mc){
		GameOptions options = mc.options;
		if (!options.resourcePacks.contains("Minecraft-Mod-Language-Modpack-1-16.zip")){
			mc.options.resourcePacks.add("Minecraft-Mod-Language-Modpack-1-16.zip");
		}else {
			List<String> packs = new ArrayList<>(100);
			packs.add("Minecraft-Mod-Language-Modpack-1-16.zip");
			packs.addAll(options.resourcePacks);
			options.resourcePacks = packs;
		}
		reloadResources(mc);
	}

	public static void reloadResources(MinecraftClient mc){
		mc.reloadResources();
	}
}
