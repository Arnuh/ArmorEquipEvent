package ca.thederpygolems.armorequip;

import java.util.*;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import ca.thederpygolems.armorequip.ArmorEquipEvent.*;

/**
 * Created by: Borlea
 * https://github.com/borlea/
 * http://thederpygolems.ca/
 */
public class Main extends JavaPlugin implements Listener{

	// Update config to add signs
	HashMap<String, HashMap<ArmorType, Long>> lastEquip;
	List<String> blockedMaterials;

	@Override
	public void onEnable(){
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		lastEquip = new HashMap<String, HashMap<ArmorType, Long>>();
		blockedMaterials = getConfig().getStringList("blocked");
		// This code is not required if you plan to implement this into a plugin, just to update any old configs.
		if(getConfig().getInt("config") == 1){
			getConfig().set("config", 2);
			blockedMaterials.add(Material.SIGN_POST.name());
			blockedMaterials.add(Material.WALL_SIGN.name());
			blockedMaterials.add(Material.SIGN.name());
			getConfig().set("blocked", blockedMaterials);
			saveConfig();
		}
	}

	@EventHandler
	public final void onInventoryClick(final InventoryClickEvent e){
		boolean shift = false;
		if(e.isCancelled()){ return; }
		if(e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT){
			shift = true;
		}
		if(e.getSlotType() != SlotType.ARMOR && e.getSlotType() != SlotType.QUICKBAR && !e.getInventory().getName().equalsIgnoreCase("container.crafting")){ return; }
		if(!(e.getWhoClicked() instanceof Player)){ return; }
		if(e.getCurrentItem() == null){ return; }
		ArmorType newArmorType = ArmorType.matchType(shift ? e.getCurrentItem() : e.getCursor());
		if(newArmorType != null){
			// Used for drag and drop checking to make sure you aren't trying to place a helmet in the boots place.
			if(e.getRawSlot() == 5 && !newArmorType.equals(ArmorType.HELMET) ||
					e.getRawSlot() == 6 && !newArmorType.equals(ArmorType.CHESTPLATE) ||
					e.getRawSlot() == 7 && !newArmorType.equals(ArmorType.LEGGINGS) ||
					e.getRawSlot() == 8 && !newArmorType.equals(ArmorType.BOOTS)){ return; }
		}
		if(shift){
			newArmorType = ArmorType.matchType(e.getCurrentItem());
			if(newArmorType != null){
				boolean equipping = true;
				if(e.getRawSlot() == 5 || e.getRawSlot() == 6 || e.getRawSlot() == 7 || e.getRawSlot() == 8){
					equipping = false;
				}
				if(newArmorType.equals(ArmorType.HELMET) && (equipping ? e.getWhoClicked().getInventory().getHelmet() == null : e.getWhoClicked().getInventory().getHelmet() != null) || newArmorType.equals(ArmorType.CHESTPLATE) && (equipping ? e.getWhoClicked().getInventory().getChestplate() == null : e.getWhoClicked().getInventory().getChestplate() != null) || newArmorType.equals(ArmorType.LEGGINGS) && (equipping ? e.getWhoClicked().getInventory().getLeggings() == null : e.getWhoClicked().getInventory().getLeggings() != null) || newArmorType.equals(ArmorType.BOOTS) && (equipping ? e.getWhoClicked().getInventory().getBoots() == null : e.getWhoClicked().getInventory().getBoots() != null)){
					ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), EquipMethod.SHIFT_CLICK, newArmorType, equipping ? null : e.getCurrentItem(), equipping ? e.getCurrentItem() : null);
					if(canEquip(e.getWhoClicked().getUniqueId().toString(), newArmorType)){
						Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
						setLastEquip(e.getWhoClicked().getUniqueId().toString(), newArmorType);
						if(armorEquipEvent.isCancelled()){
							e.setCancelled(true);
						}
					}
				}
			}
		}else{
			if(e.getRawSlot() == 5 || e.getRawSlot() == 6 || e.getRawSlot() == 7 || e.getRawSlot() == 8){
				// e.getCurrentItem() == Unequip
				// e.getCursor() == Equip
				newArmorType = ArmorType.matchType(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR ? e.getCurrentItem() : e.getCursor());
				ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), EquipMethod.DRAG, newArmorType, e.getCurrentItem(), e.getCursor());
				if(canEquip(e.getWhoClicked().getUniqueId().toString(), newArmorType)){
					Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
					setLastEquip(e.getWhoClicked().getUniqueId().toString(), newArmorType);
					if(armorEquipEvent.isCancelled()){
						e.setCancelled(true);
					}
				}
			}
		}
	}

	// No need for a check for canEquip as you can't spam equip with right click
	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent e){
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getClickedBlock() != null){// Check if action is right_click_block, who knows why I didn't just do another check like above.
				// Some blocks have actions when you right click them which stops the client from equipping the armor in hand.
				Material mat = e.getClickedBlock().getType();
				for(String s : blockedMaterials){
					if(mat.name().equalsIgnoreCase(s)){ return; }
				}
			}
			ArmorType newArmorType = ArmorType.matchType(e.getItem());
			if(newArmorType != null){
				if(newArmorType.equals(ArmorType.HELMET) && e.getPlayer().getInventory().getHelmet() == null ||
						newArmorType.equals(ArmorType.CHESTPLATE) && e.getPlayer().getInventory().getChestplate() == null ||
						newArmorType.equals(ArmorType.LEGGINGS) && e.getPlayer().getInventory().getLeggings() == null ||
						newArmorType.equals(ArmorType.BOOTS) && e.getPlayer().getInventory().getBoots() == null){
					ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(e.getPlayer(), EquipMethod.HOTBAR, ArmorType.matchType(e.getItem()), null, e.getItem());
					Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
					if(armorEquipEvent.isCancelled()){
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void dispenserFireEvent(BlockDispenseEvent e){
		ArmorType type = ArmorType.matchType(e.getItem());
		if(ArmorType.matchType(e.getItem()) != null){
			Location loc = e.getBlock().getLocation();
			for(Player p : loc.getWorld().getPlayers()){
				if(loc.getBlockY() - p.getLocation().getBlockY() >= -1 && loc.getBlockY() - p.getLocation().getBlockY() <= 1){
					if(p.getInventory().getHelmet() == null && type.equals(ArmorType.HELMET) || p.getInventory().getChestplate() == null && type.equals(ArmorType.CHESTPLATE) || p.getInventory().getLeggings() == null && type.equals(ArmorType.LEGGINGS) || p.getInventory().getBoots() == null && type.equals(ArmorType.BOOTS)){
						org.bukkit.block.Dispenser dispenser = (org.bukkit.block.Dispenser) e.getBlock().getState();
						org.bukkit.material.Dispenser dis = (org.bukkit.material.Dispenser) dispenser.getData();
						BlockFace directionFacing = dis.getFacing();
						// Someone told me not to do big if checks because it's hard to read, look at me doing it -_-
						if(directionFacing == BlockFace.EAST && p.getLocation().getBlockX() != loc.getBlockX() && p.getLocation().getX() <= loc.getX() + 2.3 && p.getLocation().getX() >= loc.getX() || directionFacing == BlockFace.WEST && p.getLocation().getX() >= loc.getX() - 1.3 && p.getLocation().getX() <= loc.getX() || directionFacing == BlockFace.SOUTH && p.getLocation().getBlockZ() != loc.getBlockZ() && p.getLocation().getZ() <= loc.getZ() + 2.3 && p.getLocation().getZ() >= loc.getZ() || directionFacing == BlockFace.NORTH && p.getLocation().getZ() >= loc.getZ() - 1.3 && p.getLocation().getZ() <= loc.getZ()){
							ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, EquipMethod.DISPENSER, ArmorType.matchType(e.getItem()), null, e.getItem());
							Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
							if(armorEquipEvent.isCancelled()){
								e.setCancelled(true);
							}
							return;
						}
					}
				}
			}
		}
	}

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
			if(i != null && !i.getType().equals(Material.AIR)) {
				Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(p, EquipMethod.DEATH, ArmorType.matchType(i), i, null));
			}
		}
	}

	public boolean canEquip(String id, ArmorType type){
		if(type == null){ return true; }
		if(lastEquip.containsKey(id)){
			if(lastEquip.get(id).containsKey(type)){
				if(System.currentTimeMillis() - lastEquip.get(id).get(type) < 500){
					return false;
				}
			}
		}
		return true;
	}

	public void setLastEquip(String id, ArmorType armorType){
		if(armorType != null){
			if(!lastEquip.containsKey(id)){
				lastEquip.put(id, new HashMap<ArmorType, Long>());
			}
			HashMap<ArmorType, Long> data = lastEquip.get(id);
			data.put(armorType, System.currentTimeMillis());
			lastEquip.put(id, data);
		}
	}
}