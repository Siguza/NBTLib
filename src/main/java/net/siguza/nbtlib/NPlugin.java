// Bukkit Plugin "NBTLib" by Siguza
// Released under the CC BY 3.0 (CreativeCommons Attribution 3.0 Unported) license.
// The full license and a human-readable summary can be found at the following location:
// http://creativecommons.org/licenses/by/3.0/

package net.siguza.nbtlib;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

// TODO: package.java
// TODO: Javadoc

public final class NPlugin extends JavaPlugin
{
    private static final Logger _log = Logger.getLogger("Minecraft");
    private static final String _version = "#VERSION#";
    
    public void onEnable()
    {
        
    }
    
    public void onDisable()
    {
        
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        sender.sendMessage(ChatColor.AQUA + "NBTLib version: " + _version);
        return true;
    }
    
    public static void info(String msg)
    {
        _log.info("[NBTLib] " + msg);
    }
    
    public static void warn(String msg)
    {
        _log.warning("[NBTLib] " + msg);
    }
    
    /*public static void severe(String msg)
    {
        note();
        _log.severe("[NBTLib] " + msg);
    }*/
    
    public static void error(Throwable t)
    {
        note();
        t.printStackTrace();
    }
    
    private static void note()
    {
        _log.severe("[NBTLib] Please copy and send the following message/error log to the developer.");
    }
}