package ca.thederpygolems.armorequip;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @Author Borlea
 * @Author ReactiveMC
 * https://github.com/borlea/
 * http://thederpygolems.ca/
 */
public final class ArmorEquipEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancel = false;
    private final EquipMethod equipType;
    private final ArmorType type;
    private final ItemStack oldArmorPiece;
    private ItemStack newArmorPiece;
    private final ItemStack[] oldArmor;
    private ItemStack[] newArmor;

    /**
     * Constructor for the ArmorEquipEvent.
     *  @param player The player who put on / removed the armor.
     * @param type The ArmorType of the armor added
     * @param oldArmorPiece The ItemStack of the armor removed.
     * @param newArmorPiece The ItemStack of the armor added.
     * @param oldArmor The old armor of a player
     */
    public ArmorEquipEvent(final Player player, final EquipMethod equipType, final ArmorType type, final ItemStack oldArmorPiece, final ItemStack newArmorPiece, ItemStack[] oldArmor){
        super(player);
        this.equipType = equipType;
        this.type = type;
        // I set it to null so people don't have to do material air checks.
        this.oldArmorPiece = oldArmorPiece;
        this.newArmorPiece = newArmorPiece;
        this.oldArmor = oldArmor;
        this.newArmor = new ItemStack[4];
        this.newArmor[type.getSlot()] = newArmorPiece;
        for (int i = 0; i<4; i++) {
            if (this.newArmor[i] == null) {
                this.newArmor[i] = new ItemStack(Material.AIR);
            }
            if (this.oldArmor[i] == null) {
                this.oldArmor[i] = new ItemStack(Material.AIR);
            }
        }
    }

    /**
     * Sets if this event should be cancelled.
     *
     * @param cancel If this event should be cancelled.
     */
    public final void setCancelled(final boolean cancel){
        this.cancel = cancel;
    }

    /**
     * Gets if this event is cancelled.
     *
     * @return If this event is cancelled
     */
    public final boolean isCancelled(){
        return cancel;
    }

    public final ArmorType getType(){
        return type;
    }
    
    /**
    * Gets a list of handlers handling this event.
    * 
    * @return A list of handlers handling this event.
    */
    public static HandlerList getHandlerList(){
    	return handlers;
    }

    /**
     * Returns the last equipped armor piece, could be a piece of armor, an AIR material, or null.
     */
    public final ItemStack getOldArmorPiece(){
        return oldArmorPiece;
    }

    /**
     * Returns the newly equipped armor, could be a piece of armor, an AIR material, or null.
     */
    public final ItemStack getNewArmorPiece(){
        return newArmorPiece;
    }

    public final void setNewArmorPiece(final ItemStack newArmorPiece){
        this.newArmorPiece = newArmorPiece;
    }

    /**
     * Gets the method used to either equip or uneqiip an armor piece.
     */
    public EquipMethod getMethod(){
        return equipType;
    }

    @Deprecated
    public ItemStack[] getNewArmor() {
        return newArmor;
    }

    public ItemStack[] getOldArmor() {
        return oldArmor;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }


    public enum EquipMethod{
        SHIFT_CLICK, DRAG, HOTBAR, DISPENSER, BROKE;
    }
}
