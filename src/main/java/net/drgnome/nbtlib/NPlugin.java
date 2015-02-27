// Bukkit Plugin "NBTLib" by Siguza
// Released under the CC BY 3.0 (CreativeCommons Attribution 3.0 Unported) license.
// The full license and a human-readable summary can be found at the following location:
// http://creativecommons.org/licenses/by/3.0/

package net.drgnome.nbtlib;

import java.util.logging.*;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

public class NPlugin /*extends JavaPlugin*/
{
    public static final String _version = "#VERSION#";
    public static final Logger _log = Logger.getLogger("Minecraft");
    
    /*public void onEnable()
    {
        _log.info("[NBTLib] Enabling");
        if(!NBTLib.enabled())
        {
            getPluginLoader().disablePlugin(this);
        }
    }
    
    public void onDisable()
    {
        _log.info("[NBTLib] Disabling");
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        sender.sendMessage(ChatColor.AQUA + "NBTLib version: " + _version);
        return true;
    }*/
}