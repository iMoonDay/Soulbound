package com.imoonday.soulbound;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.event.DropRulesEvent;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.List;
import java.util.Optional;

public class SoulBoundEnchantment extends Enchantment {

    public static final String IGNORED_NBT = "*";
    private static boolean curios = ModList.get().isLoaded("curios");
    private static boolean travelersBackpack = ModList.get().isLoaded("travelersbackpack");

    protected SoulBoundEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    public int getMinCost(int level) {
        return Config.minPower;
    }

    @Override
    public int getMaxCost(int level) {
        int powerRange = Config.powerRange;
        if (powerRange < 0) {
            powerRange = 50;
        }
        return this.getMinCost(level) + powerRange;
    }

    @Override
    public boolean isTreasureOnly() {
        return Config.isTreasure;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        switch (Config.compatibilityMode) {
            case WHITELIST_ONLY -> {
                return hasMatchItemStack(Config.whitelist, stack);
            }
            case BLACKLIST_ONLY -> {
                return hasMatchItemStack(Config.blacklist, stack);
            }
            case WHITELIST_AND_DEFAULT -> {
                if (!hasMatchItemStack(Config.whitelist, stack)) {
                    return false;
                }
            }
            case BLACKLIST_AND_DEFAULT -> {
                if (hasMatchItemStack(Config.blacklist, stack)) {
                    return false;
                }
            }
        }
        return stack.isDamageableItem() || !stack.isStackable() || super.canEnchant(stack);
    }

    public static boolean hasMatchItemStack(List<? extends String> list, ItemStack stack) {
        return list.stream().anyMatch(s -> match(stack, s));
    }

    public static boolean match(ItemStack stack, String s) {
        if (s.endsWith(IGNORED_NBT)) {
            String id = s.split("\\*", 2)[0];
            ResourceLocation identifier = ResourceLocation.tryParse(id);
            if (identifier == null) {
                identifier = ResourceLocation.tryParse(ResourceLocation.DEFAULT_NAMESPACE + ":" + id);
            }
            if (identifier == null) {
                return false;
            }
            Item item = ForgeRegistries.ITEMS.getValue(identifier);
            return stack.is(item);
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

                }
            }
            Item item = ForgeRegistries.ITEMS.getValue(identifier);
            if (item != null) {
                ItemStack itemStack = new ItemStack(item);
                if (nbt != null) {
                    itemStack.save(nbt);
                }
                return ItemStack.isSameItemSameTags(itemStack, stack);
            }
        }
        return false;
    }

    @Override
    protected boolean checkCompatibility(@NotNull Enchantment other) {
        return (other != Enchantments.VANISHING_CURSE || !Config.conflictWithVanishingCurse) && super.checkCompatibility(other);
    }

    public static void copySoulBoundItems(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        if (!alive && !(newPlayer.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || oldPlayer.isSpectator())) {
            for (int i = 0; i < oldPlayer.getInventory().getContainerSize(); i++) {
                ItemStack oldStack = oldPlayer.getInventory().getItem(i);
                ItemStack newStack = newPlayer.getInventory().getItem(i);
                int level = EnchantmentHelper.getItemEnchantmentLevel(Soulbound.SOUL_BOUND_ENCHANTMENT.get(), oldStack);
                if (level > 0 && !ItemStack.matches(oldStack, newStack)) {
                    if (Config.maxDamagePercent != 0 && !oldPlayer.isCreative() && oldStack.isDamageableItem()) {
                        oldStack.hurt(oldPlayer.getRandom().nextInt(oldStack.getMaxDamage() * Config.maxDamagePercent / 100), oldPlayer.getRandom(), oldPlayer);
                        if (oldStack.getDamageValue() >= oldStack.getMaxDamage()) {
                            if (Config.allowBreakItem) {
                                continue;
                            } else {
                                oldStack.setDamageValue(oldStack.getMaxDamage() - 1);
                            }
                        }
                    }
                    if (newStack.isEmpty()) {
                        newPlayer.getInventory().setItem(i, oldStack);
                    } else {
                        newPlayer.getInventory().placeItemBackInInventory(oldStack);
                    }
                }
            }

            if (travelersBackpack) {
                if (CapabilityUtils.isWearingBackpack(oldPlayer)) {
                    ItemStack backpack = CapabilityUtils.getWearingBackpack(oldPlayer);
                    int level = EnchantmentHelper.getItemEnchantmentLevel(Soulbound.SOUL_BOUND_ENCHANTMENT.get(), backpack);
                    if (level > 0) {
                        Optional<ITravelersBackpack> optional = CapabilityUtils.getCapability(newPlayer).resolve();
                        boolean synchronised = false;
                        if (optional.isPresent()) {
                            ITravelersBackpack iTravelersBackpack = optional.get();
                            ItemStack wearable = iTravelersBackpack.getWearable();
                            ItemStack content = iTravelersBackpack.getContainer().getItemStack();
                            boolean areNull = wearable == null && content == null;
                            boolean areEmpty = wearable != null && content != null && wearable.isEmpty() && content.isEmpty();
                            boolean areEqual = backpack.equals(wearable) && backpack.equals(content);
                            if (areNull || areEmpty || areEqual) {
                                iTravelersBackpack.setWearable(backpack);
                                iTravelersBackpack.setContents(backpack);
                                iTravelersBackpack.synchronise();
                                iTravelersBackpack.synchroniseToOthers(newPlayer);
                                synchronised = true;
                            }
                        }
                        if (!synchronised) {
                            newPlayer.getInventory().placeItemBackInInventory(backpack);
                        }
                    }
                }
            }
        }
    }

    public static void addCuriosDropListener() {
        if (curios) {
            MinecraftForge.EVENT_BUS.addListener(event -> {
                if (event instanceof DropRulesEvent rulesEvent) {
                    Entity entity = rulesEvent.getEntity();
                    if (!(entity instanceof ServerPlayer player)) {
                        return;
                    }
                    rulesEvent.addOverride(stack -> {
                        if (EnchantmentHelper.getItemEnchantmentLevel(Soulbound.SOUL_BOUND_ENCHANTMENT.get(), stack) > 0) {
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
    }
}
