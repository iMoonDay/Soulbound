package com.imoonday.soulbound.mixin;

import com.imoonday.soulbound.Soulbound;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Inventory.class)
public class InventoryMixin {

    @Shadow
    @Final
    public Player player;

    @Redirect(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    public boolean isEmpty(ItemStack instance) {
        return this.player.isAlive() ? instance.isEmpty() : EnchantmentHelper.getItemEnchantmentLevel(Soulbound.SOUL_BOUND_ENCHANTMENT.get(), instance) > 0 || instance.isEmpty();
    }
}
