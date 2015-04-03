package ca.thederpygolems.armorequip;

import java.util.HashMap;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by: Borlea
 * https://github.com/borlea/
 * http://thederpygolems.ca/
 */
public class Main extends JavaPlugin implements Listener{

	HashMap<String, HashMap<ArmorEquipEvent.ArmorType, Long>> lastEquip;

	public void onEnable(){
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		lastEquip = new HashMap<String, HashMap<ArmorEquipEvent.ArmorType, Long>>();
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
		ArmorEquipEvent.ArmorType newArmorType = ArmorEquipEvent.ArmorType.matchType(shift ? e.getCurrentItem() : e.getCursor());
		if(newArmorType != null){
			if(e.getRawSlot() == 5 && !newArmorType.equals(ArmorEquipEvent.ArmorType.HELMET)){ return; }
			if(e.getRawSlot() == 6 && !newArmorType.equals(ArmorEquipEvent.ArmorType.CHESTPLATE)){ return; }
			if(e.getRawSlot() == 7 && !newArmorType.equals(ArmorEquipEvent.ArmorType.LEGGINGS)){ return; }
			if(e.getRawSlot() == 8 && !newArmorType.equals(ArmorEquipEvent.ArmorType.BOOTS)){ return; }
		}
		if(shift){
			newArmorType = ArmorEquipEvent.ArmorType.matchType(e.getCurrentItem());
			if(newArmorType != null){
				boolean equipping = true;
				if(e.getRawSlot() == 5 || e.getRawSlot() == 6 || e.getRawSlot() == 7 || e.getRawSlot() == 8){
					equipping = false;
				}
				if(newArmorType.equals(ArmorEquipEvent.ArmorType.HELMET) && (equipping ? e.getWhoClicked().getInventory().getHelmet() == null : e.getWhoClicked().getInventory().getHelmet() != null) || newArmorType.equals(ArmorEquipEvent.ArmorType.CHESTPLATE) && (equipping ? e.getWhoClicked().getInventory().getChestplate() == null : e.getWhoClicked().getInventory().getChestplate() != null) || newArmorType.equals(ArmorEquipEvent.ArmorType.LEGGINGS) && (equipping ? e.getWhoClicked().getInventory().getLeggings() == null : e.getWhoClicked().getInventory().getLeggings() != null) || newArmorType.equals(ArmorEquipEvent.ArmorType.BOOTS) && (equipping ? e.getWhoClicked().getInventory().getBoots() == null : e.getWhoClicked().getInventory().getBoots() != null)){
					final ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), null, equipping ? null : e.getCurrentItem(), equipping ? e.getCurrentItem() : null);
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
				final ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), ArmorEquipEvent.ArmorType.matchType(shift ? e.getCurrentItem() : e.getCursor()), shift ? null : e.getCurrentItem(), shift ? e.getCurrentItem() : e.getCursor());
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
			if(e.getClickedBlock() != null){
				Material mat = e.getClickedBlock().getType();
				for(String s : getConfig().getStringList("blocked")){
					if(mat.name().equalsIgnoreCase(s)){ return; }
				}
			}
			ArmorEquipEvent.ArmorType newArmorType = ArmorEquipEvent.ArmorType.matchType(e.getItem());
			if(newArmorType != null){
				if(newArmorType.equals(ArmorEquipEvent.ArmorType.HELMET) && e.getPlayer().getInventory().getHelmet() == null || newArmorType.equals(ArmorEquipEvent.ArmorType.CHESTPLATE) && e.getPlayer().getInventory().getChestplate() == null || newArmorType.equals(ArmorEquipEvent.ArmorType.LEGGINGS) && e.getPlayer().getInventory().getLeggings() == null || newArmorType.equals(ArmorEquipEvent.ArmorType.BOOTS) && e.getPlayer().getInventory().getBoots() == null){
					final ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(e.getPlayer(), ArmorEquipEvent.ArmorType.matchType(e.getItem()), null, e.getItem());
					Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
					if(armorEquipEvent.isCancelled()){
						e.setCancelled(true);
					}
				}
			}
		}
	}

	public boolean canEquip(String id, ArmorEquipEvent.ArmorType type){
		if(type == null){ return true; }
		if(lastEquip.containsKey(id)){
			if(lastEquip.get(id).containsKey(type)){
				if(System.currentTimeMillis() - lastEquip.get(id).get(type) < 500){ return false; }
			}
		}
		return true;
	}

	public void setLastEquip(String id, ArmorEquipEvent.ArmorType armorType){
		if(armorType != null){
			if(!lastEquip.containsKey(id)){
				lastEquip.put(id, new HashMap<ArmorEquipEvent.ArmorType, Long>());
			}
			HashMap<ArmorEquipEvent.ArmorType, Long> data = lastEquip.get(id);
			data.put(armorType, System.currentTimeMillis());
			lastEquip.put(id, data);
		}
	}
}