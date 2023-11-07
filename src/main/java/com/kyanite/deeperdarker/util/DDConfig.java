package com.kyanite.deeperdarker.util;

import dev.isxander.yacl.config.ConfigEntry;
import dev.isxander.yacl.config.GsonConfigInstance;

import java.nio.file.Path;

public class DDConfig {
    public static GsonConfigInstance<DDConfig> HANDLER = new GsonConfigInstance<>(DDConfig.class, Path.of("config/deeperdarker.json"));

    @ConfigEntry
    public float spawnSomethingFromAncientVaseChance = 0.16f;

    @ConfigEntry
    public double sculkLeechesFromAncientVaseChance = 0.7;

    @ConfigEntry
    public boolean renderWardenHelmetHorns = true;

    @ConfigEntry
    public boolean geysersApplySlowFalling = false;

    @ConfigEntry
    public int portalMinWidth = 8;

    @ConfigEntry
    public int portalMinHeight = 4;

    @ConfigEntry
    public int portalMaxWidth = 48;

    @ConfigEntry
    public int portalMaxHeight = 24;

    @ConfigEntry
    public boolean wardenHeartPulses = true;

    @ConfigEntry
    public boolean changePhantomTextures = true;
}
