package com.imoonday.soulbound.mixin;

import com.imoonday.soulbound.SoulBound;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @Shadow
    @Final
    public PlayerEntity player;

    @Redirect(method = "dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    public boolean isEmpty(ItemStack instance) {
        return this.player.isAlive() ? instance.isEmpty() : EnchantmentHelper.getLevel(SoulBound.SOUL_BOUND, instance) > 0 || instance.isEmpty();
    }
}
