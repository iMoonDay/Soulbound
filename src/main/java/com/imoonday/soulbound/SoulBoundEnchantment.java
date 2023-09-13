package com.imoonday.soulbound;

import com.tiviacz.travelersbackpack.component.ComponentUtils;
import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.VanishingCurseEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;

public class SoulBoundEnchantment extends Enchantment {

    private static boolean curios = FabricLoader.getInstance().isModLoaded("trinkets");
    private static boolean travelersBackpack = FabricLoader.getInstance().isModLoaded("travelersbackpack");

    public SoulBoundEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.BREAKABLE, EquipmentSlot.values());
    }

    @Override
    public int getMinPower(int level) {
        return getConfig().minPower;
    }

    @Override
    public int getMaxPower(int level) {
        int powerRange = getConfig().powerRange;
        if (powerRange < 0) {
            powerRange = 50;
        }
        return this.getMinPower(level) + powerRange;
    }

    public boolean isTreasure() {
        return getConfig().isTreasure;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.isDamageable() || !stack.isStackable() || super.isAcceptableItem(stack);
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        return (!(other instanceof VanishingCurseEnchantment) || !getConfig().conflictWithVanishingCurse) && super.canAccept(other);
    }

    public static void copySoulBoundItems(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        if (!alive && !(oldPlayer.getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || oldPlayer.isSpectator())) {
            for (int i = 0; i < oldPlayer.getInventory().size(); i++) {
                ItemStack oldStack = oldPlayer.getInventory().getStack(i);
                ItemStack newStack = newPlayer.getInventory().getStack(i);
                int level = EnchantmentHelper.getLevel(SoulBound.SOUL_BOUND, oldStack);
                if (level > 0 && !ItemStack.areEqual(oldStack, newStack)) {
                    if (getConfig().maxDamagePercent != 0 && !oldPlayer.isCreative() && oldStack.isDamageable()) {
                        oldStack.damage(oldPlayer.getRandom().nextInt(oldStack.getMaxDamage() * getConfig().maxDamagePercent / 100), oldPlayer.getRandom(), oldPlayer);
                        if (oldStack.getDamage() >= oldStack.getMaxDamage()) {
                            if (getConfig().allowBreakItem) {
                                continue;
                            } else {
                                oldStack.setDamage(oldStack.getMaxDamage() - 1);
                            }
                        }
                    }
                    if (newStack.isEmpty()) {
                        newPlayer.getInventory().setStack(i, oldStack);
                    } else {
                        newPlayer.getInventory().offerOrDrop(oldStack);
                    }
                }
            }

            if (travelersBackpack) {
                if (ComponentUtils.isWearingBackpack(oldPlayer)) {
                    ItemStack backpack = ComponentUtils.getWearingBackpack(oldPlayer);
                    int level = EnchantmentHelper.getLevel(SoulBound.SOUL_BOUND, backpack);
                    if (level > 0) {
                        if (ComponentUtils.isWearingBackpack(newPlayer)) {
                            newPlayer.getInventory().offerOrDrop(backpack);
                        } else {
                            ComponentUtils.getComponent(newPlayer).setWearable(backpack);
                            ComponentUtils.getComponent(newPlayer).setContents(backpack);
                            ComponentUtils.sync(newPlayer);
                            ComponentUtils.syncToTracking(newPlayer);
                        }
                    }
                }
            }
        }
    }

    public static void registerTrinketDropCallback() {
        if (curios) {
            TrinketDropCallback.EVENT.register((rule, stack, ref, entity) -> {
                if (!(entity instanceof ServerPlayerEntity player)) {
                    return rule;
                }
                if (EnchantmentHelper.getLevel(SoulBound.SOUL_BOUND, stack) > 0) {
                    if (getConfig().maxDamagePercent != 0 && !player.isCreative() && stack.isDamageable()) {
                        stack.damage(player.getRandom().nextInt(stack.getMaxDamage() * getConfig().maxDamagePercent / 100), player.getRandom(), player);
                        if (stack.getDamage() >= stack.getMaxDamage()) {
                            if (getConfig().allowBreakItem) {
                                return TrinketEnums.DropRule.DESTROY;
                            } else {
                                stack.setDamage(stack.getMaxDamage() - 1);
                            }
                        }
                    }
                    return TrinketEnums.DropRule.KEEP;
                }
                return rule;
            });
        }
    }

    private static ModConfig getConfig() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
}
