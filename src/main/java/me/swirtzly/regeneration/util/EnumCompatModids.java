package me.swirtzly.regeneration.util;

/**
 * This is a helper enum for compat
 */
public enum EnumCompatModids {
    TARDIS("tardis"), LCCORE("lucraftcore");

    private final String modid;

    EnumCompatModids(String modid) {
        this.modid = modid;
    }

    public String getModid() {
        return modid;
    }

    public boolean isLoaded() {
        return Mods.isModLoaded(getModid());
    }
}
