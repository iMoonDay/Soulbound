package com.imoonday.soulbound;

import com.electronwill.nightconfig.core.EnumGetMethod;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

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

    private static final ForgeConfigSpec.EnumValue<CompatibilityMode> COMPATIBILITY_MODE = BUILDER
            .comment("Compatibility Mode")
            .comment("兼容模式")
            .defineEnum("compatibilityMode", CompatibilityMode.DEFAULT, EnumGetMethod.ORDINAL_OR_NAME_IGNORECASE);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> WHITELIST = BUILDER
            .comment("Example:(Only vanilla items support omitting prefixes)")
            .comment("例:(仅原版物品支持省略前缀)")
            .comment("[\"minecraft:diamond_sword{Damage:0}\", \"minecraft:diamond_sword\", \"diamond_sword\", \"minecraft:diamond_sword*\", \"diamond_sword*\"]")
            .comment("1.minecraft:diamond_sword{Damage:0}","2.minecraft:diamond_sword","3.diamond_sword","4.minecraft:diamond_sword*","5.diamond_sword*")
            .comment("Whitelist")
            .comment("白名单")
            .defineList("whitelist", new ArrayList<>(), o -> isValidString(o.toString()));

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST = BUILDER
            .comment("Blacklist")
            .comment("黑名单")
            .defineList("blacklist", new ArrayList<>(), o -> isValidString(o.toString()));

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean conflictWithVanishingCurse;
    public static boolean isTreasure;
    public static boolean allowBreakItem;
    public static int maxDamagePercent;
    public static int minPower;
    public static int powerRange;
    public static CompatibilityMode compatibilityMode;
    public static List<? extends String> whitelist;
    public static List<? extends String> blacklist;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        conflictWithVanishingCurse = CONFLICT_WITH_VANISHING_CURSE.get();
        isTreasure = IS_TREASURE.get();
        allowBreakItem = ALLOW_BREAK_ITEM.get();
        maxDamagePercent = MAX_DAMAGE_PERCENT.get();
        minPower = MIN_POWER.get();
        powerRange = POWER_RANGE.get();
        compatibilityMode = COMPATIBILITY_MODE.get();
        whitelist = WHITELIST.get();
        blacklist = BLACKLIST.get();
    }

    public static boolean isValidString(String s) {
        if (s.endsWith(SoulBoundEnchantment.IGNORED_NBT)) {
            String id = s.split("\\*", 2)[0];
            ResourceLocation identifier = ResourceLocation.tryParse(id);
            if (identifier == null) {
                identifier = ResourceLocation.tryParse(ResourceLocation.DEFAULT_NAMESPACE + ":" + id);
            }
            if (identifier == null) {
                return false;
            }
            Item item = ForgeRegistries.ITEMS.getValue(identifier);
            return item != null;
        }
        String[] split = s.split("\\{", 2);
        if (split.length > 0) {
            ResourceLocation identifier = ResourceLocation.tryParse(split[0]);
            if (identifier == null) {
                identifier = ResourceLocation.tryParse(ResourceLocation.DEFAULT_NAMESPACE + ":" + split[0]);
            }
            if (identifier == null) {
                return false;
            }
            CompoundTag nbt = null;
            if (split.length > 1) {
                try {
                    nbt = TagParser.parseTag("{" + split[1]);
                } catch (CommandSyntaxException ignored) {
                    return false;
                }
            }
            Item item = ForgeRegistries.ITEMS.getValue(identifier);
            if (item != null) {
                ItemStack itemStack = new ItemStack(item);
                if (nbt != null) {
                    itemStack.save(nbt);
                }
                return !itemStack.isEmpty();
            }
        }
        return false;
    }
}
