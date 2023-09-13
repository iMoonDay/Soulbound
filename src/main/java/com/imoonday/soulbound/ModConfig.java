package com.imoonday.soulbound;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "soulbound")
public class ModConfig implements ConfigData {

    public boolean conflictWithVanishingCurse = true;

    public boolean isTreasure = true;

    public boolean allowBreakItem = false;

    @ConfigEntry.BoundedDiscrete(max = 100)
    public int maxDamagePercent = 20;

    @ConfigEntry.BoundedDiscrete(max = 100)
    public int minPower = 25;

    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    public int powerRange = 50;
}
