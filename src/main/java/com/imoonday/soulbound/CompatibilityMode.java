package com.imoonday.soulbound;

import com.google.common.base.CaseFormat;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringRepresentable;

public enum CompatibilityMode implements StringRepresentable {

    BLACKLIST_AND_DEFAULT,
    BLACKLIST_ONLY,
    WHITELIST_AND_DEFAULT,
    WHITELIST_ONLY,
    DEFAULT;

    public static final String PREFIX = "config.soulbound.compatibilityMode.";
    public final String translationKey;

    CompatibilityMode() {
        this.translationKey = PREFIX + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name());
    }

    @Override
    public String toString() {
        return new TranslatableComponent(translationKey).getString();
    }

    @Override
    public String getSerializedName() {
        return this.toString();
    }
}
