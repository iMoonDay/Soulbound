package com.imoonday.soulbound.mixin;

import com.imoonday.soulbound.Soulbound;
import com.tiviacz.travelersbackpack.util.BackpackUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BackpackUtils.class, remap = false)
public class BackpackUtilsMixin {
    @Inject(method = "onPlayerDeath", at = @At("HEAD"), cancellable = true)
    private static void handleOnPlayerDeath(Level level, Player player, ItemStack stack, CallbackInfo ci) {
        if (EnchantmentHelper.getItemEnchantmentLevel(Soulbound.SOUL_BOUND_ENCHANTMENT.get(), stack) > 0) {
            ci.cancel();
        }
    }
}
