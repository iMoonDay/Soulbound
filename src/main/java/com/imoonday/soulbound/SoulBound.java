package com.imoonday.soulbound;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SoulBound implements ModInitializer {

    public static final Enchantment SOUL_BOUND = Registry.register(Registry.ENCHANTMENT, new Identifier("soulbound", "soulbound"), new SoulBoundEnchantment());

    @Override
    public void onInitialize() {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        ServerPlayerEvents.COPY_FROM.register(SoulBoundEnchantment::copySoulBoundItems);
        SoulBoundEnchantment.registerTrinketDropCallback();
    }
}
