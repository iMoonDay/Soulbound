package com.imoonday.soulbound.mixin;

import com.imoonday.soulbound.SoulBound;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getPossibleEntries", at = @At("RETURN"), cancellable = true)
    private static void removeInvalidEntries(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        List<EnchantmentLevelEntry> list = cir.getReturnValue();
        list.removeIf(entry -> entry.enchantment == SoulBound.SOUL_BOUND && !entry.enchantment.isAcceptableItem(stack));
        cir.setReturnValue(list);
    }
}
