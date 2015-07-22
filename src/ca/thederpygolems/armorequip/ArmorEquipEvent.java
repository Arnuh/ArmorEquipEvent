package ca.thederpygolems.armorequip;

import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by: Borlea
 * https://github.com/borlea/
 * http://thederpygolems.ca/
 */
public final class ArmorEquipEvent extends PlayerEvent implements Cancellable{

	private static final HandlerList handlers = new HandlerList();
	private boolean cancel = false;
	private final EquipMethod equipType;
	private final ArmorType type;
	private final ItemStack oldArmorPiece;
	private ItemStack newArmorPiece;

	/**
	 * Constructor for the ArmorEquipEvent.
	 *
	 * @param player The player who put on / removed the armor.
	 * @param type The ArmorType of the armor added
	 * @param oldArmorPiece The ItemStack of the armor removed.
	 * @param newArmorPiece The ItemStack of the armor added.
	 */
	public ArmorEquipEvent(final Player player, final EquipMethod equipType, final ArmorType type, final ItemStack oldArmorPiece, final ItemStack newArmorPiece){
		super(player);
		this.equipType = equipType;
		this.type = type;
		this.oldArmorPiece = oldArmorPiece;
		this.newArmorPiece = newArmorPiece;
	}

	/**
	 * Gets a list of handlers handling this event.
	 *
	 * @return A list of handlers handling this event.
	 */
	public final static HandlerList getHandlerList(){
		return handlers;
	}

	/**
	 * Gets a list of handlers handling this event.
	 *
	 * @return A list of handlers handling this event.
	 */
	@Override
	public final HandlerList getHandlers(){
		return handlers;
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

	public final ArmorEquipEvent.ArmorType getType(){
		return type;
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

	public enum ArmorType{
		HELMET, CHESTPLATE, LEGGINGS, BOOTS;

		/**
		 * Attempts to match the ArmorType for the specified ItemStack.
		 *
		 * @param itemStack The ItemStack to parse the type of.
		 * @return The parsed ArmorType. (null if none were found.)
		 */
		public final static ArmorType matchType(final ItemStack itemStack){
			if(itemStack == null){ return null; }
			switch (itemStack.getType()){
				case DIAMOND_HELMET:
				case GOLD_HELMET:
				case IRON_HELMET:
				case CHAINMAIL_HELMET:
				case LEATHER_HELMET:
					return ArmorEquipEvent.ArmorType.HELMET;
				case DIAMOND_CHESTPLATE:
				case GOLD_CHESTPLATE:
				case IRON_CHESTPLATE:
				case CHAINMAIL_CHESTPLATE:
				case LEATHER_CHESTPLATE:
					return ArmorEquipEvent.ArmorType.CHESTPLATE;
				case DIAMOND_LEGGINGS:
				case GOLD_LEGGINGS:
				case IRON_LEGGINGS:
				case CHAINMAIL_LEGGINGS:
				case LEATHER_LEGGINGS:
					return ArmorEquipEvent.ArmorType.LEGGINGS;
				case DIAMOND_BOOTS:
				case GOLD_BOOTS:
				case IRON_BOOTS:
				case CHAINMAIL_BOOTS:
				case LEATHER_BOOTS:
					return ArmorEquipEvent.ArmorType.BOOTS;
				default:
					return null;
			}
		}
	}

	public enum EquipMethod{
		SHIFT_CLICK, DRAG, HOTBAR, DISPENSER, BROKE, DEATH;
	}
}