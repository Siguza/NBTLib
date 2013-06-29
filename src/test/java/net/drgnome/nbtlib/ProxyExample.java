// Bukkit Plugin "NBTLib" by Siguza
// The license under which this software is released can be accessed at:
// http://creativecommons.org/licenses/by/3.0/

package net.drgnome.nbtlib;

public abstract class ProxyExample
{
    public ProxyExample(double d)
    {
    }
    
    public abstract void test1(Object o);
    
    protected double test2(double d1, double d2, double d3)
    {
        return d1;
    }
    
    public Object test3()
    {
        return getClass();
    }
}