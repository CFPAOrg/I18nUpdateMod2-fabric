package org.cfpa.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.resource.language.LanguageDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "<init>",at=@At("RETURN"))
    public void init(RunArgs args, CallbackInfo ci){
        MinecraftClient.getInstance().options.language = "zh_cn";
        MinecraftClient.getInstance().getLanguageManager().setLanguage(new LanguageDefinition("zh_cn", "CN", "简体中文", false));
        if (!MinecraftClient.getInstance().options.resourcePacks.contains("Minecraft-Mod-Language-Modpack-1-16.zip")){
            MinecraftClient.getInstance().options.resourcePacks.add("Minecraft-Mod-Language-Modpack-1-16.zip");
            MinecraftClient.getInstance().reloadResources();
        }else {
            List<String> packs = new ArrayList<>(10);
            packs.add("Minecraft-Mod-Language-Modpack-1-16.zip");
            packs.addAll(MinecraftClient.getInstance().options.resourcePacks);
            MinecraftClient.getInstance().options.resourcePacks = packs;
        }
    }
}
