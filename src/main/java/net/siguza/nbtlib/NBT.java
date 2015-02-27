// Bukkit Plugin "NBTLib" by Siguza
// Released under the CC BY 3.0 (CreativeCommons Attribution 3.0 Unported) license.
// The full license and a human-readable summary can be found at the following location:
// http://creativecommons.org/licenses/by/3.0/

package net.siguza.nbtlib;

import java.lang.reflect.*;
import java.util.*;

/**
 * Utility for dealing with NBT data.
 * <b>Note:</b> All methods of this class operate on <b>copies</b> of the values passed to them, so especially Lists and Maps are <b>not</b> passed by reference.
 *
 * @since 1.0.0
 */
public final class NBT
{
    private static final Object[] _cache = new Object[1];
    // 0 = Import/Export
    // 1 = Conversions
    private static final boolean[] _available = new boolean[2];
    //  0 = Byte
    //  1 = Short
    //  2 = Int
    //  3 = Long
    //  4 = Float
    //  5 = Double
    //  6 = ByteArray
    //  7 = String
    //  8 = List
    //  9 = Compound
    // 10 = IntArray
    private static final Class<?>[] _classes = new Class[11];
    private static final Constructor[] _constructors = new Constructor[11];
    private static final Field[] _fields = new Field[11];
    // 0 = import
    // 1 = export
    private static final Method[] _methods = new Method[2];

    static
    {
        try
        {
            VolatileClass streamClass = VolatileClass.getMinecraftClass("NBTCompressedStreamTools");
            if(VolatileClass.minecraftVersion < 1070300)
            {
                _methods[0] = streamClass.getDeclaredMethod(null, null, byte[].class);
            }
            else
            {
                _methods[0] = streamClass.getDeclaredMethod(null, null, byte[].class, null);
                Class clazz = _methods[0].getParameterTypes()[1];
                _cache[0] = VolatileClass.getRaw(clazz).getDeclaredFieldNoName(clazz).get(null);
            }
            _methods[1] = streamClass.getDeclaredMethod(null, byte[].class, null);
            _available[0] = true;
        }
        catch(Exception e)
        {
            NPlugin.error(e);
        }
        VolatileClass[] vcl = new VolatileClass[_classes.length];
        boolean success = false;
        try
        {
            vcl[9] = VolatileClass.getMinecraftClass("NBTTagCompound");
            vcl[0] = VolatileClass.getMinecraftClass("NBTTagByte");
            vcl[1] = VolatileClass.getMinecraftClass("NBTTagShort");
            vcl[2] = VolatileClass.getMinecraftClass("NBTTagInt");
            vcl[3] = VolatileClass.getMinecraftClass("NBTTagLong");
            vcl[4] = VolatileClass.getMinecraftClass("NBTTagFloat");
            vcl[5] = VolatileClass.getMinecraftClass("NBTTagDouble");
            vcl[6] = VolatileClass.getMinecraftClass("NBTTagByteArray");
            vcl[7] = VolatileClass.getMinecraftClass("NBTTagString");
            vcl[8] = VolatileClass.getMinecraftClass("NBTTagList");
            vcl[10] = VolatileClass.getMinecraftClass("NBTTagIntArray");
            success = true;
        }
        catch(Exception e1)
        {
            if(_methods[0] == null)
            {
                NPlugin.error(e1);
            }
            else
            {
                try
                {
                    if(vcl[9] == null)
                    {
                        vcl[9] = VolatileClass.getRaw(_methods[0].getReturnType());
                    }
                    Class base = vcl[9].getJavaClass().getSuperclass();
                    Method create = VolatileClass.getRaw(base).getDeclaredMethod(null, base, byte.class);
                    create.setAccessible(true);
                    for(int i = 0; i <= 10; i++)
                    {
                        if(i == 9)
                        {
                            continue;
                        }
                        vcl[i] = VolatileClass.getRaw(create.invoke(null, (byte)(i + 1)).getClass());
                    }
                    success = true;
                }
                catch(Exception e2)
                {
                    NPlugin.error(e1);
                    NPlugin.error(e2);
                }
            }
        }
        if(success)
        {
            try
            {
                _fields[0] = vcl[0].getDeclaredFieldNoName(byte.class);
                _fields[1] = vcl[1].getDeclaredFieldNoName(short.class);
                _fields[2] = vcl[2].getDeclaredFieldNoName(int.class);
                _fields[3] = vcl[3].getDeclaredFieldNoName(long.class);
                _fields[4] = vcl[4].getDeclaredFieldNoName(float.class);
                _fields[5] = vcl[5].getDeclaredFieldNoName(double.class);
                _fields[6] = vcl[6].getDeclaredFieldNoName(byte[].class);
                _fields[7] = vcl[7].getDeclaredFieldNoName(String.class);
                _fields[8] = vcl[8].getDeclaredFieldNoName(List.class);
                _fields[9] = vcl[9].getDeclaredFieldNoName(Map.class);
                _fields[10] = vcl[10].getDeclaredFieldNoName(int[].class);
                for(int i = 0; i < _constructors.length; i++)
                {
                    _constructors[i] = vcl[i].getDeclaredConstructor();
                }
                for(int i = 0; i < _classes.length; i++)
                {
                    _classes[i] = vcl[i].getJavaClass();
                }
                _available[1] = true;
            }
            catch(Exception e)
            {
                NPlugin.error(e);
            }
        }
    }

    private NBT()
    {
    }

    /**
     * Reads an array of bytes into an {@code NBTTagCompound}.
     *
     * @param b an array of bytes (NBT data)
     *
     * @return an {@code NBTTagCompound}
     *
     * @throws FunctionUnavailableException if NBTLib is unable to find the correct required minecraft method(s)
     * @throws IllegalAccessException       if access to the underlying minecraft method(s) is denied
     * @throws InvocationTargetException    if the underlying minecraft method(s) throw any exception
     * @throws NullPointerException         if {@code b} is {@code null}
     * @throws ExceptionInInitializerError  if there is a minecraft class initialization error
     */
    public static Object importData(byte[] b) throws FunctionUnavailableException, IllegalAccessException, InvocationTargetException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[0])
        {
            throw new FunctionUnavailableException("NBT streaming is not available. There should be error logs for this from the server start.");
        }
        try
        {
            if(VolatileClass.minecraftVersion < 1070300)
            {
                return _methods[0].invoke(null, b);
            }
            else
            {
                return _methods[0].invoke(null, b, _cache[0]);
            }
        }
        catch(IllegalArgumentException e)
        {
            NPlugin.error(e);
            throw broken();
        }
    }

    /**
     * Writes an {@code NBTTagCompound} to an array of bytes.
     *
     * @param nbt the {@code NBTTagCompound} to be written
     *
     * @return an array of bytes (NBT data)
     *
     * @throws FunctionUnavailableException if NBTLib is unable to find the correct required minecraft method(s)
     * @throws IllegalAccessException       if access to the underlying minecraft method(s) is denied
     * @throws IllegalArgumentException     if {@code nbt} is not an instance of {@code NBTTagCompound}
     * @throws InvocationTargetException    if the underlying minecraft method(s) throw any exception
     * @throws NullPointerException         if {@code nbt} is {@code null}
     * @throws ExceptionInInitializerError  if there is a minecraft class initialization error
     */
    public static byte[] exportData(Object nbt) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[0])
        {
            throw new FunctionUnavailableException("NBT streaming is not available. There should be error logs for this from the server start.");
        }
        return (byte[])_methods[1].invoke(null, nbt);
    }

    /**
     * Wraps an object in an NBT tag.
     *
     * @param obj an NBT-compatible object
     *
     * @return an NBT tag containing {@code obj}
     *
     * @throws ClassCastException           if {@code obj} is a {@link Map} of which any key is not an instance of {@link String}
     * @throws FunctionUnavailableException if NBTLib is unable to find the correct required minecraft method(s)
     * @throws IllegalAccessException       if access to the underlying minecraft method(s) is denied
     * @throws IllegalArgumentException     if {@code obj} is of a type that cannot be wrapped in any NBT type
     * @throws InvocationTargetException    if the underlying minecraft method(s) throw any exception
     * @throws NullPointerException         if {@code obj} is {@code null}
     * @throws ExceptionInInitializerError  if there is a minecraft class initialization error
     */
    @SuppressWarnings("unchecked")
    public static Object wrap(Object obj) throws ClassCastException, FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        };
        if(obj instanceof Byte)
        {
            return wrapByte((Byte)obj);
        }
        else if(obj instanceof Short)
        {
            return wrapShort((Short)obj);
        }
        else if(obj instanceof Integer)
        {
            return wrapInt((Integer)obj);
        }
        else if(obj instanceof Long)
        {
            return wrapLong((Long)obj);
        }
        else if(obj instanceof Float)
        {
            return wrapFloat((Float)obj);
        }
        else if(obj instanceof Double)
        {
            return wrapDouble((Double)obj);
        }
        else if(obj instanceof byte[])
        {
            return wrapByteArray((byte[])obj);
        }
        else if(obj instanceof String)
        {
            return wrapString((String)obj);
        }
        else if(obj instanceof List)
        {
            return wrapList((List<Object>)obj);
        }
        else if(obj instanceof Map)
        {
            return wrapCompound((Map<String, Object>)obj);
        }
        else if(obj instanceof int[])
        {
            return wrapIntArray((int[])obj);
        }
        throw new IllegalArgumentException("Parameter is not representable by any NBT type.");
    }

    /**
     * Unwraps an object from an NBT tag.
     *
     * @param nbt an NBT tag
     *
     * @return the contained object
     *
     * @throws FunctionUnavailableException if NBTLib is unable to find the correct required minecraft method(s)
     * @throws IllegalAccessException       if access to the underlying minecraft method(s) is denied
     * @throws IllegalArgumentException     if {@code nbt} is not an instance of any NBT type
     * @throws NullPointerException         if {@code nbt} is {@code null}
     * @throws ExceptionInInitializerError  if there is a minecraft class initialization error
     */
    public static Object unwrap(Object nbt) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        if(_classes[0].isInstance(nbt))
        {
            return unwrapByte(nbt);
        }
        else if(_classes[1].isInstance(nbt))
        {
            return unwrapShort(nbt);
        }
        else if(_classes[2].isInstance(nbt))
        {
            return unwrapInt(nbt);
        }
        else if(_classes[3].isInstance(nbt))
        {
            return unwrapLong(nbt);
        }
        else if(_classes[4].isInstance(nbt))
        {
            return unwrapFloat(nbt);
        }
        else if(_classes[5].isInstance(nbt))
        {
            return unwrapDouble(nbt);
        }
        else if(_classes[6].isInstance(nbt))
        {
            return unwrapByteArray(nbt);
        }
        else if(_classes[7].isInstance(nbt))
        {
            return unwrapString(nbt);
        }
        else if(_classes[8].isInstance(nbt))
        {
            return unwrapList(nbt);
        }
        else if(_classes[9].isInstance(nbt))
        {
            return unwrapCompound(nbt);
        }
        else if(_classes[10].isInstance(nbt))
        {
            return unwrapIntArray(nbt);
        }
        throw new IllegalArgumentException("Parameter is not an instance of any NBT type.");
    }

    /**
     * Unwraps a byte from an NBT tag.
     *
     * @param nbt an NBT tag
     *
     * @return
     *
     * @throws FunctionUnavailableException if NBTLib is unable to find the correct required minecraft method(s)
     * @throws IllegalAccessException       if access to the underlying minecraft method(s) is denied
     * @throws IllegalArgumentException     if {@code obj} is of a type that cannot be wrapped in any NBT type
     * @throws InvocationTargetException    if the underlying minecraft method(s) throw any exception
     * @throws NullPointerException         if {@code obj} is {@code null}
     * @throws ExceptionInInitializerError  if there is a minecraft class initialization error
     */
    public static Object wrapByte(byte b) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        try
        {
            Object nbt = _constructors[0].newInstance();
            _fields[0].set(nbt, b);
            return nbt;
        }
        catch(InstantiationException e)
        {
            NPlugin.error(e);
            throw broken();
        }
    }

    /**
     *
     *
     * @param
     *
     * @return
     *
     * @throws FunctionUnavailableException if NBTLib is unable to find the correct required minecraft method(s)
     * @throws IllegalAccessException       if access to the underlying minecraft method(s) is denied
     * @throws IllegalArgumentException     if {@code obj} is of a type that cannot be wrapped in any NBT type
     * @throws NullPointerException         if {@code obj} is {@code null}
     * @throws ExceptionInInitializerError  if there is a minecraft class initialization error
     */
    public static byte unwrapByte(Object nbt) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        return (Byte)_fields[0].get(nbt);
    }

    public static Object wrapShort(short s) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        try
        {
            Object nbt = _constructors[1].newInstance();
            _fields[1].set(nbt, s);
            return nbt;
        }
        catch(InstantiationException e)
        {
            NPlugin.error(e);
            throw broken();
        }
    }

    public static short unwrapShort(Object nbt) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        return (Short)_fields[1].get(nbt);
    }

    public static Object wrapInt(int i) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        try
        {
            Object nbt = _constructors[2].newInstance();
            _fields[2].set(nbt, i);
            return nbt;
        }
        catch(InstantiationException e)
        {
            NPlugin.error(e);
            throw broken();
        }
    }

    public static int unwrapInt(Object nbt) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        return (Integer)_fields[2].get(nbt);
    }

    public static Object wrapLong(long l) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        try
        {
            Object nbt = _constructors[3].newInstance();
            _fields[3].set(nbt, l);
            return nbt;
        }
        catch(InstantiationException e)
        {
            NPlugin.error(e);
            throw broken();
        }
    }

    public static long unwrapLong(Object nbt) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        return (Long)_fields[3].get(nbt);
    }

    public static Object wrapFloat(float f) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        try
        {
            Object nbt = _constructors[4].newInstance();
            _fields[4].set(nbt, f);
            return nbt;
        }
        catch(InstantiationException e)
        {
            NPlugin.error(e);
            throw broken();
        }
    }

    public static float unwrapFloat(Object nbt) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        return (Float)_fields[4].get(nbt);
    }

    public static Object wrapDouble(double d) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        try
        {
            Object nbt = _constructors[5].newInstance();
            _fields[5].set(nbt, d);
            return nbt;
        }
        catch(InstantiationException e)
        {
            NPlugin.error(e);
            throw broken();
        }
    }

    public static double unwrapDouble(Object nbt) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        return (Double)_fields[5].get(nbt);
    }

    public static Object wrapByteArray(byte[] b) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        try
        {
            Object nbt = _constructors[6].newInstance();
            _fields[6].set(nbt, copy(b));
            return nbt;
        }
        catch(InstantiationException e)
        {
            NPlugin.error(e);
            throw broken();
        }
    }

    public static byte[] unwrapByteArray(Object nbt) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        return copy((byte[])_fields[6].get(nbt));
    }

    public static Object wrapString(String str) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        try
        {
            Object nbt = _constructors[7].newInstance();
            _fields[7].set(nbt, str);
            return nbt;
        }
        catch(InstantiationException e)
        {
            NPlugin.error(e);
            throw broken();
        }
    }

    public static String unwrapString(Object nbt) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        return (String)_fields[7].get(nbt);
    }

    @SuppressWarnings("unchecked")
    public static Object wrapList(List<Object> list) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        try
        {
            Object nbt = _constructors[8].newInstance();
            List<Object> nbtlist = (List<Object>)_fields[8].get(nbt);
            if(nbtlist == null)
            {
                nbtlist = new ArrayList<Object>();
                _fields[8].set(nbt, nbtlist);
            }
            for(Object o : list)
            {
                nbtlist.add(wrap(o));
            }
            return nbt;
        }
        catch(InstantiationException e)
        {
            NPlugin.error(e);
            throw broken();
        }
    }

    public static List<Object> unwrapList(Object nbt) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        List<Object> ret = new ArrayList<Object>();
        for(Object o : (List)_fields[8].get(nbt))
        {
            ret.add(unwrap(o));
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    public static Object wrapCompound(Map<String, Object> map) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        try
        {
            Object nbt = _constructors[9].newInstance();
            Map<String, Object> nbtmap = (Map<String, Object>)_fields[9].get(nbt);
            if(nbtmap == null)
            {
                nbtmap = new HashMap<String, Object>();
                _fields[9].set(nbt, nbtmap);
            }
            for(Map.Entry<String, Object> e : map.entrySet())
            {
                nbtmap.put(e.getKey(), wrap(e.getValue()));
            }
            return nbt;
        }
        catch(InstantiationException e)
        {
            NPlugin.error(e);
            throw broken();
        }
    }

    public static Map<String, Object> unwrapCompound(Object nbt) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        Map<String, Object> ret = new HashMap<String, Object>();
        for(Map.Entry e : ((Map<?, ?>)_fields[9].get(nbt)).entrySet())
        {
            ret.put((String)e.getKey(), unwrap(e.getValue()));
        }
        return ret;
    }

    public static Object wrapIntArray(int[] i) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        try
        {
            Object nbt = _constructors[10].newInstance();
            _fields[10].set(nbt, copy(i));
            return nbt;
        }
        catch(InstantiationException e)
        {
            NPlugin.error(e);
            throw broken();
        }
    }

    public static int[] unwrapIntArray(Object nbt) throws FunctionUnavailableException, IllegalAccessException, IllegalArgumentException, NullPointerException, ExceptionInInitializerError
    {
        // Inlining ftw!
        if(!_available[1])
        {
            throw new FunctionUnavailableException("NBT conversion is not available. There should be error logs for this from the server start.");
        }
        return copy((int[])_fields[10].get(nbt));
    }

    private static FunctionUnavailableException broken() throws FunctionUnavailableException
    {
        return new FunctionUnavailableException("This should really never happen! Please inform the developer!");
    }

    private static byte[] copy(byte[] old)
    {
        byte[] b = new byte[old.length];
        for(int i = 0; i < old.length; i++)
        {
            b[i] = old[i];
        }
        return b;
    }

    private static int[] copy(int[] old)
    {
        int[] b = new int[old.length];
        for(int i = 0; i < old.length; i++)
        {
            b[i] = old[i];
        }
        return b;
    }
}
