// Bukkit Plugin "NBTLib" by Siguza
// Do whatever you want with this code.

package net.drgnome.nbtlib;

import java.io.*;
import javax.xml.bind.DatatypeConverter;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.inventory.ItemStack;

public enum /*class*/ NBT
{
    END(0),
    BYTE(1),
    SHORT(2),
    INT(3),
    LONG(4),
    FLOAT(5),
    DOUBLE(6),
    BYTE_ARRAY(7),
    STRING(8),
    LIST(9),
    COMPOUND(10),
    INT_ARRAY(11);
    
    private final int _id;
    
    private NBT(int id)
    {
        _id = id;
    }
    
    public int getId()
    {
        return _id;
    }
    
    /**
     * Test
     *
     * @param string A base64 encoded string.
     */
    public static Object loadNBT64(String string)
    throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return loadNBT(DatatypeConverter.parseBase64Binary(string));
    }
    
    public static Object loadNBT(byte[] array)
    throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return NBTLib.invokeMinecraftDynamic("NBTCompressedStreamTools", null, NBTLib.getMinecraftPackage() + "NBTTagCompound", new Object[]{byte[].class}, new Object[]{array});
    }
    
    public static Object loadNBT(InputStream stream)
    throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return NBTLib.invokeMinecraftDynamic("NBTCompressedStreamTools", null, NBTLib.getMinecraftPackage() + "NBTTagCompound", new Object[]{InputStream.class}, new Object[]{stream});
    }
    
    public static Object loadNBT(DataInput input)
    throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return NBTLib.invokeMinecraftDynamic("NBTCompressedStreamTools", null, NBTLib.getMinecraftPackage() + "NBTTagCompound", new Object[]{DataInput.class}, new Object[]{input});
    }
    
    public static ItemStack loadItemStack64(String string)
    throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return mcToBukkit(loadNBT64(string));
    }
    
    public static ItemStack loadItemStack(byte[] array)
    throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return mcToBukkit(loadNBT(array));
    }
    
    public static ItemStack loadItemStack(InputStream stream)
    throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return mcToBukkit(loadNBT(stream));
    }
    
    public static ItemStack loadItemStack(DataInput input)
    throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return mcToBukkit(loadNBT(input));
    }
    
    public static ItemStack mcToBukkit(Object o)
    throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        return (ItemStack)NBTLib.invokeCraftbukkit("inventory.CraftItemStack", null, "asBukkitCopy", new Object[]{NBTLib.getMinecraftPackage() + "ItemStack"}, new Object[]{o});
    }
    
    public static NBT getEnum(Object o)
    throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        int i = ((Byte)NBTLib.invokeMinecraft("NBTBase", o, "getTypeId", new Object[0], new Object[0])).intValue();
        for(NBT nbt : values())
        {
            if(nbt.getId() == i)
            {
                return nbt;
            }
        }
        return null;
    }
}