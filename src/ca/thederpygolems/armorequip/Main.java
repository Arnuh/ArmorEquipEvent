package ca.thederpygolems.armorequip;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by: Borlea
 * https://github.com/borlea/
 * http://thederpygolems.ca/
 */
public class Main extends JavaPlugin {
    @Override
    public void onEnable(){
        getConfig().addDefault("handle-dispensers",true);
        getConfig().options().copyDefaults(true);
        saveConfig();
        boolean dispensers = getConfig().getBoolean("handle-dispensers");
        getServer().getPluginManager().registerEvents(new ArmorListener(dispensers), this);
    }
}
