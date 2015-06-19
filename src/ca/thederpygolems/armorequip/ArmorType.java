package ca.thederpygolems.armorequip;

import org.bukkit.inventory.ItemStack;

/**
 * @Author Borlea
 * @Author TheLightMC
 */
public enum ArmorType {
    HELMET(3),CHESTPLATE(2), LEGGINGS(1), BOOTS(0);
    private final int slot;

    ArmorType(int slot) {
        this.slot = slot;
    }

    /**
     * @param itemStack piece of armor you want to match
     * @return ArmorType of the itemStack
     */
        public static ArmorType matchType(final ItemStack itemStack){
            if(itemStack == null || !itemStack.getType().name().contains("_")){ return null; }
            String name = itemStack.getType().name().split("_")[1].toLowerCase();
            switch (name){
                case "chestplate":
                    return ArmorType.CHESTPLATE;
                case "helmet":
                    return ArmorType.HELMET;
                case "leggings":
                    return ArmorType.LEGGINGS;
                case "boots":
                    return ArmorType.BOOTS;
                default:
                    return null;
            }
        }

    public int getSlot() {
        return slot;
    }
}
