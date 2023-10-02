package com.imoonday.soulbound;

import com.google.common.base.CaseFormat;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public enum CompatibilityMode implements StringIdentifiable {

    BLACKLIST_AND_DEFAULT,
    BLACKLIST_ONLY,
    WHITELIST_AND_DEFAULT,
    WHITELIST_ONLY,
    DEFAULT;

    public static final String PREFIX = "text.autoconfig.soulbound.option.compatibilityMode.";
    public final String translationKey;

    CompatibilityMode() {
        this.translationKey = PREFIX + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name());
    }

    @Override
    public String toString() {
        return Text.translatable(translationKey).getString();
    }

    @Override
    public String asString() {
        return this.toString();
    }
}
