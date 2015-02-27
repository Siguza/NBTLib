package net.siguza.nbtlib.test;

import java.util.*;
import org.junit.Test;
import net.siguza.nbtlib.*;

public class Tests
{
    // Since we don't have craftbukkit anymore, we can't test
    //@Test
    public void testNBT() throws Throwable
    {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("byte", (byte)4);
        map.put("short", (short)8);
        map.put("int", (int)15);
        map.put("long", (long)16);
        map.put("float", (float)23);
        map.put("double", (double)42);
        map.put("byteArray", "TARDIS".getBytes());
        map.put("string", "Exterminate!");
        List<String> list = new ArrayList<String>();
        list.add("Wibbly wobbly timey wimey");
        map.put("list", list);
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("are", "cool");
        map.put("bowties", map2);
        Map meow = NBT.unwrapCompound(NBT.importData(NBT.exportData(NBT.wrapCompound(map))));
    }
}
