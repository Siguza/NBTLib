// Bukkit Plugin "NBTLib" by Siguza
// The license under which this software is released can be accessed at:
// http://creativecommons.org/licenses/by/3.0/

package net.drgnome.nbtlib;

import java.lang.reflect.*;
import org.junit.Before;
import org.junit.Test;

public class ProxyTest
{
    @Test
    public void tryCompile() throws Throwable
    {
        try
        {
            ProxyExample ex = ClassProxy.newInstance(ProxyExample.class, new Handler(), new Class[]{double.class}, 5D);
            System.out.println(ex.test3());
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            throw t;
        }
    }
    
    private static class Handler implements InvocationHandler
    {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            System.out.println(proxy.getClass().getName() + " - " + method.getName());
            return ClassProxy.callSuper(proxy, method, args);
        }
    }
}