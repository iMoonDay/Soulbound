package com.imoonday.soulbound;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.event.DropRulesEvent;
import top.theillusivec4.curios.api.type.capability.ICurio;

@Mod(Soulbound.MODID)
public class Soulbound {

    public static final String MODID = "soulbound";
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);
    public static final RegistryObject<Enchantment> SOUL_BOUND_ENCHANTMENT = ENCHANTMENTS.register(MODID, SoulBoundEnchantment::new);
    private static boolean curios = ModList.get().isLoaded("curios");

    public Soulbound() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ENCHANTMENTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        if (curios) {
            MinecraftForge.EVENT_BUS.addListener(event -> {
                if (event instanceof DropRulesEvent rulesEvent) {
                    LivingEntity entity = rulesEvent.getEntity();
                    if (!(entity instanceof ServerPlayer player)) {
                        return;
                    }
                    rulesEvent.addOverride(stack -> {
                        if (EnchantmentHelper.getTagEnchantmentLevel(Soulbound.SOUL_BOUND_ENCHANTMENT.get(), stack) > 0) {
                            if (Config.maxDamagePercent != 0 && !player.isCreative() && stack.isDamageableItem()) {
                                stack.hurt(player.getRandom().nextInt(stack.getMaxDamage() * Config.maxDamagePercent / 100), player.getRandom(), player);
                                if (stack.getDamageValue() >= stack.getMaxDamage()) {
                                    if (Config.allowBreakItem) {
                                        return false;
                                    } else {
                                        stack.setDamageValue(stack.getMaxDamage() - 1);
                                    }
                                }
                            }
                            return true;
                        }
                        return false;
                    }, ICurio.DropRule.ALWAYS_KEEP);
                }
            });
        }
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
