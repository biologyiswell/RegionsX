package io.github.biologyiswell.regionsx.region;

/**
 * This enum represents the flags from a region.
 * Each flag represents a permission that the
 * player has in a region.
 */
public enum RXRegionFlag {

    BREAK,
    PLACE,
    USE;

    /**
     * Get region flag by name.
     */
    public static RXRegionFlag getRegionFlagByName(String name) {
        if (name.equalsIgnoreCase("break") || name.equalsIgnoreCase("quebrar")) {
            return BREAK;
        } else if (name.equalsIgnoreCase("place") || name.equalsIgnoreCase("colocar")) {
            return PLACE;
        } else if (name.equalsIgnoreCase("use") || name.equalsIgnoreCase("usar")) {
            return USE;
        }

        return null;
    }

    /**
     * Get translated name from region flag.
     */
    public static String getTranslatedName(RXRegionFlag regionFlag) {
        if (regionFlag == BREAK) {
            return "Quebrar";
        } else if (regionFlag == PLACE) {
            return "Colocar";
        } else if (regionFlag == USE) {
            return "Usar";
        }

        throw new RuntimeException("Translation from region flag \"" + regionFlag + "\" can\'t be supported.");
    }
}
