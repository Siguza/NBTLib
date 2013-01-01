// Bukkit Plugin "NBTLib" by Siguza
// Do whatever you want with this code.

package net.drgnome.nbtlib;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class NBTLib extends JavaPlugin
{
    public static final String _version = "#VERSION#";
    public static final Logger _log = Logger.getLogger("Minecraft");
    private static String _minecraft;
    private static String _craftbukkit;
    private boolean _disable = false;
    
    /**
     * @exclude
     */
    public NBTLib()
    {
        super();
        ArrayList<Package> list = new ArrayList<Package>();
        for(Package p : Package.getPackages())
        {
            if(p.getName().startsWith("net.minecraft.server"))
            {
                list.add(p);
            }
        }
        if(list.size() == 1)
        {
            _minecraft = list.get(0).getName();
            _craftbukkit = "org.bukkit.craftbukkit" + _minecraft.substring(20);
            if(Package.getPackage(_craftbukkit) == null)
            {
                _log.severe("[NBTLib] Can't find Craftbukkit package! (" + _minecraft + "/" + _craftbukkit + ")");
                _disable = true;
            }
            else
            {
                _minecraft += ".";
                _craftbukkit += ".";
            }
        }
        else
        {
            _log.severe("[NBTLib] Can't find Minecraft package! " + list.size() + " possible packages found:");
            for(Package p : list.toArray(new Package[0]))
            {
                _log.severe("[NBTLib] " + p.getName());
                _disable = true;
            }
        }
    }
    
    public void onEnable()
    {
        if(_disable)
        {
            getPluginLoader().disablePlugin(this);
        }
    }
    
    public static String getMinecraftPackage()
    {
        return _minecraft;
    }
    
    public static String getCraftbukkitPackage()
    {
        return _craftbukkit;
    }
    
    public static Object invokeMinecraftDynamic(String className, Object object, Object returnType, Object[] paramTypes, Object[] params)
    throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return invokeDynamic(_minecraft + className, object, returnType, paramTypes, params);
    }
    
    public static Object invokeMinecraft(String className, Object object, String name, Object[] paramTypes, Object[] params)
    throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return invoke(_minecraft + className, object, name, paramTypes, params);
    }
    
    public static Object invokeCraftbukkitDynamic(String className, Object object, Object returnType, Object[] paramTypes, Object[] params)
    throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return invokeDynamic(_craftbukkit + className, object, returnType, paramTypes, params);
    }
    
    public static Object invokeCraftbukkit(String className, Object object, String name, Object[] paramTypes, Object[] params)
    throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return invoke(_craftbukkit + className, object, name, paramTypes, params);
    }
    
    public static Object invokeDynamic(String className, Object object, Object returnType, Object[] paramTypes, Object[] params)
    throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return getMethod(Class.forName(className), parseClass(returnType), parseClass(paramTypes)).invoke(object, params);
    }
    
    public static Object invoke(String className, Object object, String name, Object[] paramTypes, Object[] params)
    throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return getMethod(Class.forName(className), name, parseClass(paramTypes)).invoke(object, params);
    }
    
    public static Method getMethod(Class clazz, Class returnType, Class... params)
    {
        for(Method m : clazz.getMethods())
        {
            if(m.getReturnType() == returnType)
            {
                Class[] args = m.getParameterTypes();
                if(args.length != params.length)
                {
                    continue;
                }
                for(int i = 0; i < args.length; i++)
                {
                    if(args[i] != params[i])
                    {
                        continue;
                    }
                }
                m.setAccessible(true);
                return m;
            }
        }
        return null;
    }
    
    public static Method getMethod(Class clazz, String name, Class... params)
    throws NoSuchMethodException
    {
        Method m = clazz.getMethod(name, params);
        m.setAccessible(true);
        return m;
    }
    
    private static Class[] parseClass(Object[] array)
    throws ClassNotFoundException
    {
        ArrayList<Class> list = new ArrayList<Class>();
        for(Object o : array)
        {
            list.add(parseClass(o));
        }
        return list.toArray(new Class[0]);
    }
    
    private static Class parseClass(Object o)
    throws ClassNotFoundException
    {
        if(o instanceof Class)
        {
            return (Class)o;
        }
        else
        {
            return Class.forName((String)o);
        }
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        sender.sendMessage(ChatColor.AQUA + "NBTLib version: " + _version);
        return true;
    }
}