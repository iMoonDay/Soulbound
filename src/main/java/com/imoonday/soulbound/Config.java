package com.imoonday.soulbound;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Soulbound.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue CONFLICT_WITH_VANISHING_CURSE = BUILDER
            .comment("Whether it conflicts with the Vanishing Curse")
            .comment("是否与消失诅咒冲突")
            .define("conflictWithVanishingCurse", true);

    private static final ForgeConfigSpec.BooleanValue IS_TREASURE = BUILDER
            .comment("Whether it is treasure enchantment (i.e. will not appear in the Enchantment Table)")
            .comment("是否是宝物附魔（即不会出现在附魔台中）")
            .define("isTreasure", true);

    private static final ForgeConfigSpec.BooleanValue ALLOW_BREAK_ITEM = BUILDER
            .comment("Whether to allow the item to disappear after being damaged")
            .comment("是否允许物品在损坏后消失")
            .define("allowBreakItem", false);

    private static final ForgeConfigSpec.IntValue MAX_DAMAGE_PERCENT = BUILDER
            .comment("Max percentage of damage")
            .comment("最大损坏百分比")
            .defineInRange("maxDamagePercent", 20, 0, 100);

    private static final ForgeConfigSpec.IntValue MIN_POWER = BUILDER
            .comment("Min power of enchantment")
            .comment("最小附魔权重")
            .defineInRange("minPower", 25, 0, 100);

    private static final ForgeConfigSpec.IntValue POWER_RANGE = BUILDER
            .comment("Power range of enchantment")
            .comment("附魔权重范围")
            .defineInRange("powerRange", 50, 1, 100);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean conflictWithVanishingCurse;
    public static boolean isTreasure;
    public static boolean allowBreakItem;
    public static int maxDamagePercent;
    public static int minPower;
    public static int powerRange;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        conflictWithVanishingCurse = CONFLICT_WITH_VANISHING_CURSE.get();
        isTreasure = IS_TREASURE.get();
        allowBreakItem = ALLOW_BREAK_ITEM.get();
        maxDamagePercent = MAX_DAMAGE_PERCENT.get();
        minPower = MIN_POWER.get();
        powerRange = POWER_RANGE.get();
    }
}
