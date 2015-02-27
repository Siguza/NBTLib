// Bukkit Plugin "NBTLib" by Siguza
// Released under the CC BY 3.0 (CreativeCommons Attribution 3.0 Unported) license.
// The full license and a human-readable summary can be found at the following location:
// http://creativecommons.org/licenses/by/3.0/

package net.siguza.nbtlib;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.jar.*;
import java.security.CodeSource;
import org.bukkit.Bukkit;

// TODO: Have a look at md5's specialsource

/**
 * Represents a class which may vary in all aspects in different releases or implementations, such as the Minecraft classes in Bukkit, Spigot or MCPC.
 *
 * @since 1.0.0
 */
public final class VolatileClass
{
    /**
     * The version of the bukkit-modded Minecraft version as an integer.
     *
     * This value is computed as {@code majorVersion * 1000000 + minorVersion * 10000 + revisionNumber * 100 + buildNumber} and should therefore increase with new Minecraft/Bukkit releases.
     * 
     * Example: For an implemented Minecraft version of "v1_7_R3", this value would be 1070300.
     */
    public static final int minecraftVersion;
    private static final String PATTERN_MC = "^net\\.minecraft\\.server\\.v[0-9][^\\.]+$";
    private static final String PATTERN_CB = "^org\\.bukkit\\.craftbukkit\\.v[0-9][^\\.]+$";
    private static final HashMap<Class, VolatileClass> _clas_nameCache = new HashMap<Class, VolatileClass>();
    private static final HashMap<String, VolatileClass> _nameCache = new HashMap<String, VolatileClass>();
    private static final ClassFinder _mcClassFinder;
    private static final ClassFinder _cbClassFinder;
    private static final MCPCRemapper _mcpc;
    private static final String _suffix;
    private final Class _javaClass;
    private final String _className;
    
    static
    {
        int version = 0;
        ClassFinder mcFinder = null;
        ClassFinder cbFinder = null;
        MCPCRemapper remapper = null;
        String suffix = null;
        try
        {
            ArrayList<String> mcList = new ArrayList<String>();
            ArrayList<String> cbList = new ArrayList<String>();
            for(Package p : Package.getPackages())
            {
                String name = p.getName().toLowerCase();
                if(name.matches(PATTERN_MC))
                {
                    mcList.add(p.getName());
                }
                else if(name.matches(PATTERN_CB))
                {
                    cbList.add(p.getName());
                }
            }
            String[] array = cbList.toArray(new String[0]);
            if(array.length > 1)
            {
                NPlugin.error(new FunctionUnavailableException(array.length + " Craftbukkit packages found (too many)!"));
            }
            else
            {
                if(array.length == 0)
                {
                    array = filterJar(PATTERN_CB);
                }
                if(array.length == 0)
                {
                    NPlugin.error(new FunctionUnavailableException("No Craftbukkit package found!"));
                }
                else
                {
                    cbFinder = new ClassFinder(array[0]);
                    suffix = array[0].substring(23);
                    if(suffix.matches("^v[0-9]+_[0-9]+_R[0-9]+$"))
                    {
                        String[] parts = suffix.substring(1).split("_");
                        try
                        {
                            version = (Integer.parseInt(parts[0]) * 1000000) + (Integer.parseInt(parts[1]) * 10000) + (Integer.parseInt(parts[2].substring(1)) * 100);
                        }
                        catch(NumberFormatException e)
                        {
                        }
                    }
                    array = mcList.toArray(new String[0]);
                    if(array.length > 1)
                    {
                        NPlugin.error(new FunctionUnavailableException(array.length + " Minecraft packages found (too many)!"));
                    }
                    else
                    {
                        mcFinder = new ClassFinder((array.length == 0) ? "net.minecraft.server." + suffix : array[0]);
                        try
                        {
                            Class.forName("za.co.mcportcentral.MCPCConfig");
                            remapper = new MCPCRemapper();
                        }
                        catch(ClassNotFoundException e)
                        {
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            NPlugin.error(e);
        }
        minecraftVersion = version;
        _mcClassFinder = mcFinder;
        _cbClassFinder = cbFinder;
        _mcpc = remapper;
        _suffix = suffix;
    }
    
    // ===== ===== ===== public static Methods ===== ===== =====
    
    public static VolatileClass getMinecraftClass(String name) throws ClassNotFoundException, FunctionUnavailableException
    {
        if(_mcClassFinder == null)
        {
            throw new FunctionUnavailableException("The Minecraft package could not be found or assumed.");
        }
        String className = _mcClassFinder.getFullName(name);
        if(_nameCache.containsKey(className))
        {
            return _nameCache.get(className);
        }
        Class clazz = null;
        if(_mcpc != null)
        {
            clazz = _mcpc.remapClass(className);
        }
        if(clazz == null)
        {
            clazz = _mcClassFinder.findClass(className);
        }
        if(clazz == null)
        {
            throw new ClassNotFoundException("Cannot find Minecraft class \"" + name + "\"!");
        }
        return new VolatileClass(clazz, className);
    }
    
    public static VolatileClass getCraftbukkitClass(String name) throws ClassNotFoundException, FunctionUnavailableException
    {
        if(_cbClassFinder == null)
        {
            throw new FunctionUnavailableException("The Craftbukkit package could not be found or assumed.");
        }
        String className = _cbClassFinder.getFullName(name);
        if(_nameCache.containsKey(className))
        {
            return _nameCache.get(className);
        }
        Class clazz = null;
        if(_mcpc != null)
        {
            clazz = _mcpc.remapClass(className);
        }
        if(clazz == null)
        {
            clazz = _cbClassFinder.findClass(className);
        }
        if(clazz == null)
        {
            throw new ClassNotFoundException("Cannot find Craftbukkit class \"" + name + "\"!");
        }
        return new VolatileClass(clazz, className);
    }
    
    public static VolatileClass getRaw(Class javaClass)
    {
        if(_clas_nameCache.containsKey(javaClass))
        {
            return _clas_nameCache.get(javaClass);
        }
        return new VolatileClass(javaClass);
    }
    
    // ===== ===== ===== private static Methods ===== ===== =====
    
    private static String[] filterJar(String pattern)
    {
        CodeSource cs = Bukkit.class.getProtectionDomain().getCodeSource();
        if(cs == null)
        {
            return new String[0];
        }
        ArrayList<String> list = new ArrayList<String>();
        try
        {
            JarInputStream jis = new JarInputStream(new FileInputStream(new File(cs.getLocation().toURI())));
            JarEntry je;
            while((je = jis.getNextJarEntry()) != null)
            {
                if(je.isDirectory())
                {
                    String name = je.getName().replace("/", ".");
                    name = name.substring(0, name.length() - 1); // Remove trailing dot
                    if(name.toLowerCase().matches(pattern))
                    {
                        list.add(name);
                    }
                }
            }
            jis.close();
        }
        catch(Exception e)
        {
            NPlugin.error(e);
        }
        return list.toArray(new String[0]);
    }
    
    private static String parametersToString(Class[] parameterTypes)
    {
        String msg = "(";
        boolean first = true;
        for(Class c : parameterTypes)
        {
            if(first)
            {
                first = false;
            }
            else
            {
                msg += ", ";
            }
            msg += (c == null) ? "???" : c.getName();
        }
        return msg + ")";
    }
    
    // ===== ===== ===== private Constructors ===== ===== =====
    
    private VolatileClass(Class javaClass)
    {
        _javaClass = javaClass;
        _className = (_mcpc != null) ? _mcpc.getClassName(javaClass) : javaClass.getName();
        _clas_nameCache.put(javaClass, this);
    }
    
    private VolatileClass(Class javaClass, String className)
    {
        _javaClass = javaClass;
        _className = className;
        _clas_nameCache.put(javaClass, this);
        _nameCache.put(className, this);
    }
    
    // ===== ===== ===== public Methods ===== ===== =====
    
    public Class getJavaClass()
    {
        return _javaClass;
    }
    
    public Constructor getConstructor(Class... parameterTypes) throws NoSuchMethodException, TooManyMatchesException
    {
        Constructor c = filterConstructors(_javaClass.getConstructors(), parameterTypes);
        if(c == null)
        {
            throw new NoSuchMethodException(getConstructorMessage(parameterTypes));
        }
        c.setAccessible(true);
        return c;
    }
    
    public Constructor getConstructorUnsafe(Class... parameterTypes) throws NoSuchMethodException
    {
        Constructor c = filterConstructorsUnsafe(_javaClass.getConstructors(), parameterTypes);
        if(c == null)
        {
            throw new NoSuchMethodException(getConstructorMessage(parameterTypes));
        }
        c.setAccessible(true);
        return c;
    }
    
    public Constructor getDeclaredConstructor(Class... parameterTypes) throws NoSuchMethodException, TooManyMatchesException
    {
        Constructor c = filterConstructors(_javaClass.getDeclaredConstructors(), parameterTypes);
        if(c == null)
        {
            throw new NoSuchMethodException(getConstructorMessage(parameterTypes));
        }
        c.setAccessible(true);
        return c;
    }
    
    public Constructor getDeclaredConstructorUnsafe(Class... parameterTypes) throws NoSuchMethodException
    {
        Constructor c = filterConstructorsUnsafe(_javaClass.getDeclaredConstructors(), parameterTypes);
        if(c == null)
        {
            throw new NoSuchMethodException(getConstructorMessage(parameterTypes));
        }
        c.setAccessible(true);
        return c;
    }
    
    public Method getMethod(String name, Class returnType, Class... parameterTypes) throws NoSuchMethodException, TooManyMatchesException
    {
        Method m = null;
        if((name != null) && (_mcpc != null))
        {
            m = _mcpc.remapMethod(_javaClass, name, returnType, parameterTypes, true);
        }
        if(m == null)
        {
            m = filterMethods(_javaClass.getMethods(), name, returnType, parameterTypes);
        }
        if(m == null)
        {
            throw new NoSuchMethodException(getMethodMessage(name, returnType, parameterTypes));
        }
        m.setAccessible(true);
        return m;
    }
    
    public Method getMethodUnsafe(String name, Class returnType, Class... parameterTypes) throws NoSuchMethodException
    {
        Method m = null;
        if((name != null) && (_mcpc != null))
        {
            m = _mcpc.remapMethodUnsafe(_javaClass, name, returnType, parameterTypes, true);
        }
        if(m == null)
        {
            m = filterMethodsUnsafe(_javaClass.getMethods(), name, returnType, parameterTypes);
        }
        if(m == null)
        {
            throw new NoSuchMethodException(getMethodMessage(name, returnType, parameterTypes));
        }
        m.setAccessible(true);
        return m;
    }
    
    public Method getDeclaredMethod(String name, Class returnType, Class... parameterTypes) throws NoSuchMethodException, TooManyMatchesException
    {
        Method m = null;
        if((name != null) && (_mcpc != null))
        {
            m = _mcpc.remapMethod(_javaClass, name, returnType, parameterTypes, false);
        }
        if(m == null)
        {
            m = filterMethods(_javaClass.getDeclaredMethods(), name, returnType, parameterTypes);
        }
        if(m == null)
        {
            throw new NoSuchMethodException(getMethodMessage(name, returnType, parameterTypes));
        }
        m.setAccessible(true);
        return m;
    }
    
    public Method getDeclaredMethodUnsafe(String name, Class returnType, Class... parameterTypes) throws NoSuchMethodException
    {
        Method m = null;
        if((name != null) && (_mcpc != null))
        {
            m = _mcpc.remapMethodUnsafe(_javaClass, name, returnType, parameterTypes, false);
        }
        if(m == null)
        {
            m = filterMethodsUnsafe(_javaClass.getDeclaredMethods(), name, returnType, parameterTypes);
        }
        if(m == null)
        {
            throw new NoSuchMethodException(getMethodMessage(name, returnType, parameterTypes));
        }
        m.setAccessible(true);
        return m;
    }
    
    public Field getField(String name) throws NoSuchFieldException
    {
        Field f = null;
        if(_mcpc != null)
        {
            f = _mcpc.remapField(_javaClass, name, true);
        }
        if(f == null)
        {
            f = _javaClass.getField(name);
        }
        if(f == null)
        {
            throw new NoSuchFieldException("Cannot find field " + name + " on class " + _className + ".");
        }
        f.setAccessible(true);
        return f;
    }
    
    public Field getFieldNoName(Class type) throws NoSuchFieldException, TooManyMatchesException
    {
        Field f = filterFields(_javaClass.getFields(), type);
        if(f == null)
        {
            throw new NoSuchFieldException("No matching field found on class " + _className + ".");
        }
        f.setAccessible(true);
        return f;
    }
    
    public Field getFieldNoNameUnsafe(Class type) throws NoSuchFieldException
    {
        Field f = filterFieldsUnsafe(_javaClass.getFields(), type);
        if(f == null)
        {
            throw new NoSuchFieldException("No matching field found on class " + _className + ".");
        }
        f.setAccessible(true);
        return f;
    }
    
    public Field getDeclaredField(String name) throws NoSuchFieldException
    {
        Field f = null;
        if(_mcpc != null)
        {
            f = _mcpc.remapField(_javaClass, name, false);
        }
        if(f == null)
        {
            f = _javaClass.getDeclaredField(name);
        }
        if(f == null)
        {
            throw new NoSuchFieldException("Cannot find field " + name + " on class " + _className + ".");
        }
        f.setAccessible(true);
        return f;
    }
    
    public Field getDeclaredFieldNoName(Class type) throws NoSuchFieldException, TooManyMatchesException
    {
        Field f = filterFields(_javaClass.getDeclaredFields(), type);
        if(f == null)
        {
            throw new NoSuchFieldException("No matching field found on class " + _className + ".");
        }
        f.setAccessible(true);
        return f;
    }
    
    public Field getDeclaredFieldNoNameUnsafe(Class type) throws NoSuchFieldException
    {
        Field f = filterFieldsUnsafe(_javaClass.getDeclaredFields(), type);
        if(f == null)
        {
            throw new NoSuchFieldException("No matching field found on class " + _className + ".");
        }
        f.setAccessible(true);
        return f;
    }
    
    public boolean equals(Object obj)
    {
        return (obj instanceof VolatileClass) && ((VolatileClass)obj)._javaClass.equals(_javaClass);
    }
    
    /*private String getConstructorMessage(String name, Class returnType, Class[] parameterTypes)
    {
        String cname = returnType.getName();
        return getDescription("constructor " + (name.equals(cname) ? name : (cname + "/" + name)), parameterTypes);
    }
    
    private String getMethodMessage(String name, Class returnType, Class[] parameterTypes)
    {
        return getMessage("method " + (returnType == null ? "???" : returnType.getName()) + " " + (name == null ? "???" : name), parameterTypes);
    }
    
    private String getMessage(String prefix, Class[] parameterTypes)
    {
        String msg = "Cannot find " + prefix + "(";
        boolean first = true;
        for(Class c : parameterTypes)
        {
            if(first)
            {
                first = false;
            }
            else
            {
                msg += ", ";
            }
            msg += (c == null) ? "???" : c.getName();
        }
        msg += ") on class " + _className + ".";
        return msg;
    }*/
    
    // ===== ===== ===== private Methods ===== ===== =====
    
    private String getConstructorMessage(Class[] parameterTypes)
    {
        return "Cannot find constructor " + getConstructorDescription(parameterTypes) + " on class " + _className + ".";
    }
    
    private String getMethodMessage(String name, Class returnType, Class[] parameterTypes)
    {
        return "Cannot find method " + getMethodDescription(name, returnType, parameterTypes) + " on class " + _className + ".";
    }
    
    private String getConstructorDescription(Constructor c)
    {
        return getConstructorDescription(c.getParameterTypes());
    }
    
    private String getConstructorDescription(Class[] parameterTypes)
    {
        return ((_className.equals(_javaClass.getName())) ? _className : (_javaClass.getName() + "|" + _className)) + parametersToString(parameterTypes);
    }
    
    private String getMethodDescription(Method m)
    {
        return getMethodDescription(m.getName(), m.getReturnType(), m.getParameterTypes());
    }
    
    private String getMethodDescription(String name, Class returnType, Class[] parameterTypes)
    {
        return ((returnType == null) ? "???" : returnType.getName()) + " " + ((name == null) ? "???" : name) + parametersToString(parameterTypes);
    }
    
    private Constructor filterConstructors(Constructor[] constructors, Class[] parameterTypes) throws TooManyMatchesException
    {
        ArrayList<Constructor> list = new ArrayList<Constructor>();
    outside1:
        for(Constructor c : constructors)
        {
            if(parameterTypes != null)
            {
                Class[] p = c.getParameterTypes();
                if(p.length != parameterTypes.length)
                {
                    continue;
                }
                for(int i = 0; i < p.length; i++)
                {
                    if((parameterTypes[i] != null) && !p[i].equals(parameterTypes[i]))
                    {
                        continue outside1;
                    }
                }
            }
            list.add(c);
        }
        Constructor[] array = list.toArray(new Constructor[0]);
        if(array.length == 0)
        {
            return null;
        }
        if(array.length > 1)
        {
            String msg = "Constructors " + array[0].getName();
            for(int i = 1; i < array.length - 1; i++)
            {
                msg += ", " + getConstructorDescription(array[i]);
            }
            msg += "and " + array[array.length - 1].getName() + " match on class " + _className + ".";
            throw new TooManyMatchesException(msg);
        }
        return array[0];
    }
    
    private Constructor filterConstructorsUnsafe(Constructor[] constructors, Class[] parameterTypes)
    {
    outside2:
        for(Constructor c : constructors)
        {
            if(parameterTypes != null)
            {
                Class[] p = c.getParameterTypes();
                if(p.length != parameterTypes.length)
                {
                    continue;
                }
                for(int i = 0; i < p.length; i++)
                {
                    if((parameterTypes[i] != null) && !p[i].equals(parameterTypes[i]))
                    {
                        continue outside2;
                    }
                }
            }
            return c;
        }
        return null;
    }
    
    private Method filterMethods(Method[] methods, String name, Class returnType, Class[] parameterTypes) throws TooManyMatchesException
    {
        ArrayList<Method> list = new ArrayList<Method>();
    outside3:
        for(Method m : methods)
        {
            if(((name != null) && !m.getName().equals(name)) || ((returnType != null) && !m.getReturnType().equals(returnType)))
            {
                continue;
            }
            if(parameterTypes != null)
            {
                Class[] p = m.getParameterTypes();
                if(p.length != parameterTypes.length)
                {
                    continue;
                }
                for(int i = 0; i < p.length; i++)
                {
                    if((parameterTypes[i] != null) && !p[i].equals(parameterTypes[i]))
                    {
                        continue outside3;
                    }
                }
            }
            list.add(m);
        }
        Method[] array = list.toArray(new Method[0]);
        if(array.length == 0)
        {
            return null;
        }
        if(array.length > 1)
        {
            String msg = "Methods " + array[0].getName();
            for(int i = 1; i < array.length - 1; i++)
            {
                msg += ", " + getMethodDescription(array[i]);
            }
            msg += "and " + array[array.length - 1].getName() + " match on class " + _className + ".";
            throw new TooManyMatchesException(msg);
        }
        return array[0];
    }
    
    private Method filterMethodsUnsafe(Method[] methods, String name, Class returnType, Class[] parameterTypes)
    {
    outside4:
        for(Method m : methods)
        {
            if(((name != null) && !m.getName().equals(name)) || ((returnType != null) && !m.getReturnType().equals(returnType)))
            {
                continue;
            }
            if(parameterTypes != null)
            {
                Class[] p = m.getParameterTypes();
                if(p.length != parameterTypes.length)
                {
                    continue;
                }
                for(int i = 0; i < p.length; i++)
                {
                    if((parameterTypes[i] != null) && !p[i].equals(parameterTypes[i]))
                    {
                        continue outside4;
                    }
                }
            }
            return m;
        }
        return null;
    }
    
    private Field filterFields(Field[] fields, Class type) throws /*NoSuchFieldException,*/ TooManyMatchesException
    {
        ArrayList<Field> list = new ArrayList<Field>();
        for(Field f : fields)
        {
            if(!f.getType().equals(type))
            {
                continue;
            }
            list.add(f);
        }
        Field[] array = list.toArray(new Field[0]);
        if(array.length == 0)
        {
            // throw new NoSuchFieldException("No matching field found on class " + _className + ".");
            return null;
        }
        if(array.length > 1)
        {
            String msg = "Fields " + array[0].getName();
            for(int i = 1; i < array.length - 1; i++)
            {
                msg += ", " + array[i].getName();
            }
            msg += "and " + array[array.length - 1].getName() + " match on class " + _className + ".";
            throw new TooManyMatchesException(msg);
        }
        // array[0].setAccessible(true);
        return array[0];
    }
    
    private Field filterFieldsUnsafe(Field[] fields, Class type) /*throws NoSuchFieldException*/
    {
        for(Field f : fields)
        {
            if(!f.getType().equals(type))
            {
                continue;
            }
            // f.setAccessible(true);
            return f;
        }
        return null;
        // throw new NoSuchFieldException("No matching field found on class " + _className + ".");
    }
    
    // ===== ===== ===== private static Inner Classes ===== ===== =====
    
    private static class ClassFinder
    {
        private final String _prefix;
        
        private ClassFinder(String name)
        {
            _prefix = name + ".";
        }
        
        public String getFullName(String name)
        {
            return _prefix + name;
        }
        
        public Class findClass(String name)
        {
            try
            {
                return Class.forName(name);
            }
            catch(ClassNotFoundException e)
            {
                return null;
            }
        }
    }
    
    private static class MCPCRemapper
    {
        private MCPCRemapper()
        {
        }
        
        public String getClassName(Class clazz)
        {
            return null; // TODO
        }
        
        public Class remapClass(String name)
        {
            return null; // TODO
        }
        
        public Method remapMethod(Class clazz, String name, Class returnType, Class[] parameterTypes, boolean deep) throws TooManyMatchesException
        {
            return null; // TODO
        }
        
        public Method remapMethodUnsafe(Class clazz, String name, Class returnType, Class[] parameterTypes, boolean deep)
        {
            return null; // TODO
        }
        
        public Field remapField(Class clazz, String name, boolean deep)
        {
            return null; // TODO
        }
    }
}