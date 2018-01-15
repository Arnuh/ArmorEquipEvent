package com.codingforcookies.armorequip;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @Author Borlea
 * @Github https://github.com/borlea/
 * @Website http://codingforcookies.com/
 * @since Jul 30, 2015
 */
public class Main extends JavaPlugin implements Listener{

	@Override
	public void onEnable(){
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(new ArmorListener(getConfig().getStringList("blocked")), this);
		// example();
	}

	/**
	 * Used to test locally and debug information about what is happening with the ArmorEquipEvent.
	 */
	public void example(){
		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void equip(ArmorEquipEvent event){
		System.out.println("ArmorEquipEvent - " + event.getMethod());
		System.out.println("Type: " + event.getType());
		System.out.println("New: " + (event.getNewArmorPiece() != null ? event.getNewArmorPiece().getType() : "null"));
		System.out.println("Old: " + (event.getOldArmorPiece() != null ? event.getOldArmorPiece().getType() : "null"));
		boolean test = true;
		if(test){
			// Does a test where if you start in survival without a helmet on you SHOULD end up in survival without the helmet on, or adventure mode if helmet is on.
			// Used to make sure spam clicking doesn't mess up, no clue if a better test exists.
			if(event.getOldArmorPiece() != null && event.getOldArmorPiece().getType().equals(Material.DIAMOND_HELMET)){
				event.getPlayer().setGameMode(event.getPlayer().getGameMode().equals(GameMode.ADVENTURE) ? GameMode.SURVIVAL : GameMode.ADVENTURE);
			}
			if(event.getNewArmorPiece() != null && event.getNewArmorPiece().getType().equals(Material.DIAMOND_HELMET)){
				event.getPlayer().setGameMode(event.getPlayer().getGameMode().equals(GameMode.ADVENTURE) ? GameMode.SURVIVAL : GameMode.ADVENTURE);
			}
		}
	}
}