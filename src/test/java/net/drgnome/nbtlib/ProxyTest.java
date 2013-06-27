// Bukkit Plugin "NBTLib" by Siguza
// The license under which this software is released can be accessed at:
// http://creativecommons.org/licenses/by/3.0/

package net.drgnome.nbtlib;

import java.lang.reflect.*;
import org.junit.Before;
import org.junit.Test;

public class ProxyTest
{
    @Before
    public void loadPackages() throws Throwable
    {
        String v = "1_5_R3";
        Class.forName("net.minecraft.server.v" + v + ".Entity");
        Class.forName("org.bukkit.craftbukkit.v" + v + ".CraftWorld");
    }
    
    @Test
    public void tryCompile() throws Throwable
    {
        try
        {
            Class<?> mcClass = Class.forName(NBTLib.getMinecraftPackage() + "Material");
            Object o = ClassProxy.newInstance(mcClass, new InvocationHandler()
            {
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
                {
                    System.out.println(proxy.getClass().getName() + " - " + method.getName());
                    return ClassProxy.callSuper(proxy, method, args);
                }
            }, new Class[]
            {
                Class.forName(NBTLib.getMinecraftPackage() + "MaterialMapColor")
            }, new Object[]
            {
                NBTLib.fetchMinecraftField("MaterialMapColor", null, "m")
            });
            System.out.println(o.toString());
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            throw t;
        }
    }
}