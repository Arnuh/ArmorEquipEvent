package ca.thederpygolems.armorequip;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @Author Borlea
 * @Github https://github.com/borlea/
 * @Website http://thederpygolems.ca/
 */
public class Main extends JavaPlugin{


	@Override
	public void onEnable(){
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(new ArmorListener(getConfig().getStringList("blocked")), this);
	}
}