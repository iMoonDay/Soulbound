package com.imoonday.soulbound;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.NotNull;

public class SoulBoundEnchantment extends Enchantment {
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
        return stack.isDamageableItem() || !stack.isStackable() || super.canEnchant(stack);
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
                int level = EnchantmentHelper.getTagEnchantmentLevel(Soulbound.SOUL_BOUND_ENCHANTMENT.get(), oldStack);
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
        }
    }
}
