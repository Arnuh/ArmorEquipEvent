package com.codingforcookies.armorequip;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.codingforcookies.armorequip.ArmorEquipEvent.EquipMethod;

/**
 * @Author Borlea
 * @Github https://github.com/borlea/
 * @Website http://codingforcookies.com/
 * @since Jul 30, 2015 6:43:34 PM
 */
public class ArmorListener implements Listener{

	private final List<String> blockedMaterials;

	public ArmorListener(List<String> blockedMaterials){
		this.blockedMaterials = blockedMaterials;
	}

	@EventHandler
	public final void onInventoryClick(final InventoryClickEvent e){
		boolean shift = false, numberkey = false;
		if(e.isCancelled()) return;
		if(e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT)){
			shift = true;
		}
		if(e.getClick().equals(ClickType.NUMBER_KEY)){
			numberkey = true;
		}
		if(e.getSlotType() != SlotType.ARMOR && e.getSlotType() != SlotType.QUICKBAR && e.getSlotType() != SlotType.CONTAINER) return;
		if(e.getClickedInventory() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
		if (!e.getInventory().getType().equals(InventoryType.CRAFTING) && !e.getInventory().getType().equals(InventoryType.PLAYER)) return;
		if(!(e.getWhoClicked() instanceof Player)) return;
		if(e.getCurrentItem() == null) return;
		ArmorType newArmorType = ArmorType.matchType(shift ? e.getCurrentItem() : e.getCursor());
		if(!shift && newArmorType != null && e.getRawSlot() != newArmorType.getSlot()){
			// Used for drag and drop checking to make sure you aren't trying to place a helmet in the boots place.
			return;
		}
		if(shift){
			newArmorType = ArmorType.matchType(e.getCurrentItem());
			if(newArmorType != null){
				boolean equipping = true;
				if(e.getRawSlot() == newArmorType.getSlot()){
					equipping = false;
				}
				if(newArmorType.equals(ArmorType.HELMET) && (equipping ? e.getWhoClicked().getInventory().getHelmet() == null : e.getWhoClicked().getInventory().getHelmet() != null) || newArmorType.equals(ArmorType.CHESTPLATE) && (equipping ? e.getWhoClicked().getInventory().getChestplate() == null : e.getWhoClicked().getInventory().getChestplate() != null) || newArmorType.equals(ArmorType.LEGGINGS) && (equipping ? e.getWhoClicked().getInventory().getLeggings() == null : e.getWhoClicked().getInventory().getLeggings() != null) || newArmorType.equals(ArmorType.BOOTS) && (equipping ? e.getWhoClicked().getInventory().getBoots() == null : e.getWhoClicked().getInventory().getBoots() != null)){
					ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), EquipMethod.SHIFT_CLICK, newArmorType, equipping ? null : e.getCurrentItem(), equipping ? e.getCurrentItem() : null);
					Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
					if(armorEquipEvent.isCancelled()){
						e.setCancelled(true);
					}
				}
			}
		}else{
			ItemStack newArmorPiece = e.getCursor();
			ItemStack oldArmorPiece = e.getCurrentItem();
			if(numberkey){
				if(e.getClickedInventory().getType().equals(InventoryType.PLAYER)){// Prevents shit in the 2by2 crafting
					// e.getClickedInventory() == The players inventory
					// e.getHotBarButton() == key people are pressing to equip or unequip the item to or from.
					// e.getRawSlot() == The slot the item is going to.
					// e.getSlot() == Armor slot, can't use e.getRawSlot() as that gives a hotbar slot ;-;
					ItemStack hotbarItem = e.getClickedInventory().getItem(e.getHotbarButton());
					if(hotbarItem != null){// Equipping
						newArmorType = ArmorType.matchType(hotbarItem);
						newArmorPiece = hotbarItem;
						oldArmorPiece = e.getClickedInventory().getItem(e.getSlot());
					}else{// Unequipping
						newArmorType = ArmorType.matchType(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR ? e.getCurrentItem() : e.getCursor());
					}
				}
			}else{
				// e.getCurrentItem() == Unequip
				// e.getCursor() == Equip
				newArmorType = ArmorType.matchType(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR ? e.getCurrentItem() : e.getCursor());
			}
			if(newArmorType != null && e.getRawSlot() == newArmorType.getSlot()){
				EquipMethod method = EquipMethod.PICK_DROP;
				if(e.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberkey) method = EquipMethod.HOTBAR_SWAP;
				ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), method, newArmorType, oldArmorPiece, newArmorPiece);
				Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
				if(armorEquipEvent.isCancelled()){
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent e){
		if(e.getAction() == Action.PHYSICAL) return;
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
			final Player player = e.getPlayer();
			if(e.getClickedBlock() != null && e.getAction() == Action.RIGHT_CLICK_BLOCK){// Having both of these checks is useless, might as well do it though.
				// Some blocks have actions when you right click them which stops the client from equipping the armor in hand.
				Material mat = e.getClickedBlock().getType();
				for(String s : blockedMaterials){
					if(mat.name().equalsIgnoreCase(s)) return;
				}
			}
			ArmorType newArmorType = ArmorType.matchType(e.getItem());
			if(newArmorType != null){
				if(newArmorType.equals(ArmorType.HELMET) && e.getPlayer().getInventory().getHelmet() == null || newArmorType.equals(ArmorType.CHESTPLATE) && e.getPlayer().getInventory().getChestplate() == null || newArmorType.equals(ArmorType.LEGGINGS) && e.getPlayer().getInventory().getLeggings() == null || newArmorType.equals(ArmorType.BOOTS) && e.getPlayer().getInventory().getBoots() == null){
					ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(e.getPlayer(), EquipMethod.HOTBAR, ArmorType.matchType(e.getItem()), null, e.getItem());
					Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
					if(armorEquipEvent.isCancelled()){
						e.setCancelled(true);
						player.updateInventory();
					}
				}
			}
		}
	}

	@EventHandler
	public void inventoryDrag(InventoryDragEvent event){
		// getType() seems to always be even.
		// Old Cursor gives the item you are equipping
		// Raw slot is the ArmorType slot
		// Can't replace armor using this method making getCursor() useless.
		ArmorType type = ArmorType.matchType(event.getOldCursor());
		if(event.getRawSlots().isEmpty()) return;// Idk if this will ever happen
		if(type != null && type.getSlot() == event.getRawSlots().stream().findFirst().orElse(0)){
			ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) event.getWhoClicked(), EquipMethod.DRAG, type, null, event.getOldCursor());
			Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
			if(armorEquipEvent.isCancelled()){
				event.setResult(Result.DENY);
				event.setCancelled(true);
			}
		}
		// Debug shit
		/*System.out.println("Slots: " + event.getInventorySlots().toString());
		System.out.println("Raw Slots: " + event.getRawSlots().toString());
		if(event.getCursor() != null){
			System.out.println("Cursor: " + event.getCursor().getType().name());
		}
		if(event.getOldCursor() != null){
			System.out.println("OldCursor: " + event.getOldCursor().getType().name());
		}
		System.out.println("Type: " + event.getType().name());*/
	}

	/*@EventHandler
	public void dispenserFireEvent(BlockDispenseEvent e){
		ArmorType type = ArmorType.matchType(e.getItem());
		if(type != null){
			org.bukkit.block.Dispenser dispenser = (org.bukkit.block.Dispenser) e.getBlock().getState();
			org.bukkit.material.Dispenser dispenserData = (org.bukkit.material.Dispenser) dispenser.getData();
			Location loc = shift(e.getBlock().getLocation(), dispenserData.getFacing(), 1);
			List<EntityLiving> list = ((CraftWorld) loc.getWorld()).getHandle().a(EntityLiving.class, new AxisAlignedBB(new BlockPosition(loc.getX(), loc.getY(), loc.getZ())), Predicates.and(IEntitySelector.e, new IEntitySelector.EntitySelectorEquipable(CraftItemStack.asNMSCopy(e.getItem()))));
			if(list.isEmpty()) return;
			EntityLiving ent = list.get(0);
			if(ent instanceof EntityPlayer){
				Player p = (Player) ent.getBukkitEntity();
				ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, EquipMethod.DISPENSER, type, null, e.getItem());
				Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
				if(armorEquipEvent.isCancelled()){
					e.setCancelled(true);
				}
			}
		}
	}*/

	@EventHandler
	public void itemBreakEvent(PlayerItemBreakEvent e){
		ArmorType type = ArmorType.matchType(e.getBrokenItem());
		if(type != null){
			Player p = e.getPlayer();
			ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, EquipMethod.BROKE, type, e.getBrokenItem(), null);
			Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
			if(armorEquipEvent.isCancelled()){
				ItemStack i = e.getBrokenItem().clone();
				i.setAmount(1);
				i.setDurability((short) (i.getDurability() - 1));
				if(type.equals(ArmorType.HELMET)){
					p.getInventory().setHelmet(i);
				}else if(type.equals(ArmorType.CHESTPLATE)){
					p.getInventory().setChestplate(i);
				}else if(type.equals(ArmorType.LEGGINGS)){
					p.getInventory().setLeggings(i);
				}else if(type.equals(ArmorType.BOOTS)){
					p.getInventory().setBoots(i);
				}
			}
		}
	}

	@EventHandler
	public void playerDeathEvent(PlayerDeathEvent e){
		Player p = e.getEntity();
		for(ItemStack i : p.getInventory().getArmorContents()){
			if(i != null && !i.getType().equals(Material.AIR)){
				Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(p, EquipMethod.DEATH, ArmorType.matchType(i), i, null));
				// No way to cancel a death event.
			}
		}
	}

	private Location shift(Location start, BlockFace direction, int multiplier){
		if(multiplier == 0) return start;
		return new Location(start.getWorld(), start.getX() + direction.getModX() * multiplier, start.getY() + direction.getModY() * multiplier, start.getZ() + direction.getModZ() * multiplier);
	}
}
