package com.zero.support.binder.compiler;
 public class Constant{
        public static final String PACKAGE_NAME = "com.zero.support";
public static final String ARRAY_CREATOR = "package com.zero.support.binder;\n" +
        "\n" +
        "import android.os.Parcel;\n" +
        "\n" +
        "import java.lang.reflect.Array;\n" +
        "import java.lang.reflect.Type;\n" +
        "\n" +
        "@SuppressWarnings(\"ALL\")\n" +
        "class ArrayCreator implements ParcelCreator<Object[]> {\n" +
        "\n" +
        "    @Override\n" +
        "    public void writeToParcel(Parcel parcel, Object[] object, Type type, Class<Object[]> rawType) throws Exception {\n" +
        "        Object[] target = (Object[]) object;\n" +
        "        if (object == null) {\n" +
        "            parcel.writeInt(-1);\n" +
        "            return;\n" +
        "        }\n" +
        "        parcel.writeInt(target.length);\n" +
        "        Class<?> subType = rawType.getComponentType();\n" +
        "        ParcelCreator creator = Binder.getParcelCreator(subType);\n" +
        "        if (creator == null) {\n" +
        "            throw new RuntimeException(\"not found creator for \" + subType);\n" +
        "        }\n" +
        "        for (Object o : target) {\n" +
        "            creator.writeToParcel(parcel, o, subType, subType);\n" +
        "        }\n" +
        "    }\n" +
        "\n" +
        "    @Override\n" +
        "    public Object[] readFromParcel(Parcel parcel, Type type, Class<Object[]> rawType) throws Exception {\n" +
        "        int N = parcel.readInt();\n" +
        "        if (N == -1) {\n" +
        "            return null;\n" +
        "        }\n" +
        "        Class<?> subType = rawType.getComponentType();\n" +
        "        if (subType == null) {\n" +
        "            return null;\n" +
        "        }\n" +
        "        Object[] list = (Object[]) Array.newInstance(subType, N);\n" +
        "\n" +
        "        ParcelCreator creator = Binder.getParcelCreator(subType);\n" +
        "        if (creator == null) {\n" +
        "            throw new RuntimeException(\"not found creator for \" + subType);\n" +
        "        }\n" +
        "        for (int i = 0; i < N; i++) {\n" +
        "            list[i] = (creator.readFromParcel(parcel, subType, subType));\n" +
        "        }\n" +
        "        return list;\n" +
        "    }\n" +
        "}";
public static final String BINDER = "package com.zero.support.binder;\n" +
        "\n" +
        "import android.os.IBinder;\n" +
        "import android.util.SparseArray;\n" +
        "\n" +
        "import java.io.File;\n" +
        "import java.lang.reflect.Array;\n" +
        "import java.lang.reflect.Type;\n" +
        "import java.util.HashMap;\n" +
        "import java.util.List;\n" +
        "import java.util.Map;\n" +
        "\n" +
        "@SuppressWarnings(\"all\")\n" +
        "public class Binder {\n" +
        "    private static final Map<Class<?>, BinderCreator<?>> creators = new HashMap<>();\n" +
        "\n" +
        "    private static final Map<Type, ParcelCreator> parcelCreators = new HashMap<>();\n" +
        "    private static final EntranceParcelCreator entranceParcelCreator = new EntranceParcelCreator();\n" +
        "    private static final BinderCreator<Object> DEFAULT_CREATOR = new DynamicBinderCreator<>();\n" +
        "\n" +
        "    static {\n" +
        "        registerParcelCreator(Array.class, new ArrayCreator());\n" +
        "        registerParcelCreator(List.class, new ListParcelCreator());\n" +
        "        registerParcelCreator(SparseArray.class, new SparseArrayParcelCreator());\n" +
        "        registerParcelCreator(Map.class, new MapParcelCreator());\n" +
        "        registerParcelCreator(File.class, new FileParcelCreator());\n" +
        "    }\n" +
        "\n" +
        "\n" +
        "    public static void registerBinderCreator(Class<?> cls, BinderCreator<?> creator) {\n" +
        "        creators.put(cls, creator);\n" +
        "    }\n" +
        "\n" +
        "    public static void registerParcelCreator(Class<?> cls, ParcelCreator<?> creator) {\n" +
        "        parcelCreators.put(cls, creator);\n" +
        "    }\n" +
        "\n" +
        "\n" +
        "    static {\n" +
        "        registerParcelCreator(List.class, new ListParcelCreator());\n" +
        "    }\n" +
        "\n" +
        "    public static BinderCreator<?> getBindCreator(Class<?> cls) {\n" +
        "        BinderCreator<?> creator = creators.get(cls);\n" +
        "        if (creator == null) {\n" +
        "            creator = DEFAULT_CREATOR;\n" +
        "        }\n" +
        "        return creator;\n" +
        "    }\n" +
        "\n" +
        "\n" +
        "    public static ParcelCreator<?> getParcelCreator(Class<?> cls) {\n" +
        "        ParcelCreator<?> creator = parcelCreators.get(cls);\n" +
        "        if (creator == null) {\n" +
        "            creator = entranceParcelCreator;\n" +
        "        }\n" +
        "        return creator;\n" +
        "    }\n" +
        "\n" +
        "    public static IBinder peekBinder(Object object) {\n" +
        "        if (object == null) {\n" +
        "            return null;\n" +
        "        }\n" +
        "        return BinderProxy.peekBinder(object);\n" +
        "    }\n" +
        "\n" +
        "    public static IBinder asBinder(Object object, Class<?> cls) {\n" +
        "        return BinderStub.of(object, cls);\n" +
        "    }\n" +
        "\n" +
        "    public static <T> T asInterface(IBinder binder, Class<T> cls) {\n" +
        "        return BinderProxy.asInterface(binder, cls);\n" +
        "    }\n" +
        "\n" +
        "}\n";
public static final String BINDER_CREATOR = "package com.zero.support.binder;\n" +
        "\n" +
        "import android.os.IBinder;\n" +
        "\n" +
        "public interface BinderCreator<T> {\n" +
        "    T asInterface(IBinder binder, Class<? extends T> cls);\n" +
        "\n" +
        "    IBinder asBinder(T t, Class<? extends T> cls);\n" +
        "}";
public static final String BINDER_EXCEPTION = "package com.zero.support.binder;\n" +
        "\n" +
        "import android.os.RemoteException;\n" +
        "\n" +
        "public class BinderException extends RemoteException {\n" +
        "    public BinderException() {\n" +
        "    }\n" +
        "\n" +
        "    public BinderException(String message) {\n" +
        "        super(message);\n" +
        "    }\n" +
        "}\n";
public static final String BINDER_NAME = "package com.zero.support.binder;\n" +
        "\n" +
        "import java.lang.annotation.ElementType;\n" +
        "import java.lang.annotation.Retention;\n" +
        "import java.lang.annotation.RetentionPolicy;\n" +
        "import java.lang.annotation.Target;\n" +
        "\n" +
        "@Target({ElementType.TYPE, ElementType.METHOD})\n" +
        "@Retention(RetentionPolicy.RUNTIME)\n" +
        "public @interface BinderName {\n" +
        "    String value();\n" +
        "}\n";
public static final String BINDER_PROXY = "package com.zero.support.binder;\n" +
        "\n" +
        "import android.os.IBinder;\n" +
        "import android.os.Parcel;\n" +
        "import android.os.RemoteException;\n" +
        "import android.text.TextUtils;\n" +
        "\n" +
        "import java.lang.reflect.InvocationHandler;\n" +
        "import java.lang.reflect.Method;\n" +
        "import java.lang.reflect.Proxy;\n" +
        "import java.lang.reflect.Type;\n" +
        "import java.util.HashMap;\n" +
        "import java.util.Map;\n" +
        "\n" +
        "class BinderProxy implements InvocationHandler, IBinder.DeathRecipient {\n" +
        "    private final IBinder remote;\n" +
        "    private final ClassHolder holder;\n" +
        "    private static final Map<IBinder, Object> localBinders = new HashMap<>();\n" +
        "\n" +
        "    public static IBinder peekBinder(Object object) {\n" +
        "        synchronized (localBinders) {\n" +
        "            for (Map.Entry<IBinder, Object> entry : localBinders.entrySet()) {\n" +
        "                if (entry.getValue() == object) {\n" +
        "                    return entry.getKey();\n" +
        "                }\n" +
        "            }\n" +
        "        }\n" +
        "        return null;\n" +
        "    }\n" +
        "\n" +
        "    public BinderProxy(IBinder remote, Class<?> cls) throws RemoteException {\n" +
        "        this.remote = remote;\n" +
        "        this.holder = new ClassHolder(cls, true);\n" +
        "        String descriptor = remote.getInterfaceDescriptor();\n" +
        "        if (!TextUtils.equals(descriptor, holder.getName())) {\n" +
        "            throw new BinderException(\"not exist \" + descriptor);\n" +
        "        }\n" +
        "        remote.linkToDeath(this, 0);\n" +
        "    }\n" +
        "\n" +
        "    @SuppressWarnings(\"all\")\n" +
        "    public static <T> T asInterface(IBinder remote, Class<T> cls) {\n" +
        "        if (remote == null) {\n" +
        "            return null;\n" +
        "        }\n" +
        "        synchronized (localBinders) {\n" +
        "            Object object = localBinders.get(remote);\n" +
        "            if (object == null) {\n" +
        "                try {\n" +
        "                    object = Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, new BinderProxy(remote, cls));\n" +
        "                } catch (RemoteException e) {\n" +
        "                    e.printStackTrace();\n" +
        "                    return null;\n" +
        "                }\n" +
        "                localBinders.put(remote, object);\n" +
        "            }\n" +
        "            return (T) object;\n" +
        "        }\n" +
        "    }\n" +
        "\n" +
        "\n" +
        "    @Override\n" +
        "    @SuppressWarnings(\"all\")\n" +
        "    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {\n" +
        "        if (method.getDeclaringClass() == Object.class) {\n" +
        "            return method.invoke(this, args);\n" +
        "        }\n" +
        "        Parcel data = Parcel.obtain();\n" +
        "        Parcel reply = Parcel.obtain();\n" +
        "        try {\n" +
        "            MethodHolder methodHolder = holder.getBinderMethod(method.getName());\n" +
        "            data.writeString(methodHolder.name);\n" +
        "            Type[] types = methodHolder.types;\n" +
        "\n" +
        "            for (int i = 0; i < types.length; i++) {\n" +
        "                ParcelCreator creator = Binder.getParcelCreator(methodHolder.params[i]);\n" +
        "                creator.writeToParcel(data, args[i], methodHolder.types[i], methodHolder.params[i]);\n" +
        "            }\n" +
        "            remote.transact(IBinder.FIRST_CALL_TRANSACTION, data, reply, 0);\n" +
        "            reply.readException();\n" +
        "            if (method.getReturnType() != void.class) {\n" +
        "                ParcelCreator creator = Binder.getParcelCreator(methodHolder.returnCls);\n" +
        "                return creator.readFromParcel(reply, methodHolder.returnType, methodHolder.returnCls);\n" +
        "            }\n" +
        "            return null;\n" +
        "        } catch (Exception e) {\n" +
        "            e.printStackTrace();\n" +
        "            throw new RemoteException(String.valueOf(e));\n" +
        "        } finally {\n" +
        "            data.recycle();\n" +
        "            reply.recycle();\n" +
        "        }\n" +
        "\n" +
        "    }\n" +
        "\n" +
        "    @Override\n" +
        "    public void binderDied() {\n" +
        "        synchronized (localBinders) {\n" +
        "            localBinders.remove(remote);\n" +
        "            remote.unlinkToDeath(this, 0);\n" +
        "        }\n" +
        "    }\n" +
        "}";
public static final String BINDER_SERIALIZABLE = "package com.zero.support.binder;\n" +
        "\n" +
        "public interface BinderSerializable {\n" +
        "\n" +
        "}";
public static final String BINDER_STUB = "package com.zero.support.binder;\n" +
        "\n" +
        "import android.os.IBinder;\n" +
        "import android.os.IInterface;\n" +
        "import android.os.Parcel;\n" +
        "import android.os.RemoteException;\n" +
        "\n" +
        "import java.util.WeakHashMap;\n" +
        "\n" +
        "class BinderStub extends android.os.Binder implements IInterface {\n" +
        "    private final static WeakHashMap<Object, BinderStub> localBinders = new WeakHashMap<>();\n" +
        "    private final Object target;\n" +
        "    private final ClassHolder holder;\n" +
        "\n" +
        "    private BinderStub(Object target, Class<?> cls) {\n" +
        "        this.target = target;\n" +
        "        this.holder = new ClassHolder(cls, false);\n" +
        "        attachInterface(this, holder.getName());\n" +
        "    }\n" +
        "\n" +
        "    public static android.os.Binder peek(Object object) {\n" +
        "        synchronized (localBinders) {\n" +
        "            return localBinders.get(object);\n" +
        "        }\n" +
        "    }\n" +
        "\n" +
        "    public static android.os.Binder of(Object object, Class<?> cls) {\n" +
        "        if (object == null) {\n" +
        "            return null;\n" +
        "        }\n" +
        "        synchronized (localBinders) {\n" +
        "            BinderStub binder = localBinders.get(object);\n" +
        "            if (binder == null) {\n" +
        "                binder = new BinderStub(object, cls);\n" +
        "                localBinders.put(object, binder);\n" +
        "            }\n" +
        "            return binder;\n" +
        "        }\n" +
        "    }\n" +
        "\n" +
        "    @SuppressWarnings(\"all\")\n" +
        "    @Override\n" +
        "    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {\n" +
        "        if (code == INTERFACE_TRANSACTION) {\n" +
        "            reply.writeString(holder.getName());\n" +
        "            return true;\n" +
        "        } else if (code != IBinder.FIRST_CALL_TRANSACTION) {\n" +
        "            return false;\n" +
        "        }\n" +
        "        String name = data.readString();\n" +
        "        MethodHolder method = holder.getBinderMethod(name);\n" +
        "        if (method == null) {\n" +
        "            return super.onTransact(code, data, reply, flags);\n" +
        "        }\n" +
        "        try {\n" +
        "            Object[] params = new Object[method.types.length];\n" +
        "            for (int i = 0; i < method.types.length; i++) {\n" +
        "                ParcelCreator creator = Binder.getParcelCreator(method.params[i]);\n" +
        "                params[i] = creator.readFromParcel(data, method.types[i], method.params[i]);\n" +
        "            }\n" +
        "            Object object = method.method.invoke(target, params);\n" +
        "            reply.writeNoException();\n" +
        "            if (method.returnType != Void.class) {\n" +
        "                ParcelCreator creator = Binder.getParcelCreator(method.returnCls);\n" +
        "                creator.writeToParcel(reply, object, method.returnType, method.returnCls);\n" +
        "            }\n" +
        "\n" +
        "        } catch (Exception e) {\n" +
        "            reply.writeException(e);\n" +
        "        }\n" +
        "        return true;\n" +
        "    }\n" +
        "\n" +
        "\n" +
        "    @Override\n" +
        "    public IBinder asBinder() {\n" +
        "        return this;\n" +
        "    }\n" +
        "\n" +
        "\n" +
        "}";
public static final String CLASS_HOLDER = "package com.zero.support.binder;\n" +
        "\n" +
        "import java.lang.reflect.Method;\n" +
        "import java.util.HashMap;\n" +
        "import java.util.Map;\n" +
        "\n" +
        "class ClassHolder {\n" +
        "    private final Map<String, MethodHolder> methods;\n" +
        "    private final String name;\n" +
        "\n" +
        "    public ClassHolder(Class<?> cls, boolean proxy) {\n" +
        "        Method[] methods = cls.getMethods();\n" +
        "        this.methods = new HashMap<>(methods.length);\n" +
        "        MethodHolder methodHolder;\n" +
        "        BinderName nameAno = cls.getAnnotation(BinderName.class);\n" +
        "        if (nameAno != null) {\n" +
        "            this.name = nameAno.value();\n" +
        "        } else {\n" +
        "            this.name = cls.getName();\n" +
        "        }\n" +
        "        for (Method method : methods) {\n" +
        "            if (proxy) {\n" +
        "                this.methods.put(method.getName(), new MethodHolder(method));\n" +
        "            } else {\n" +
        "                methodHolder = new MethodHolder(method);\n" +
        "                this.methods.put(methodHolder.name, methodHolder);\n" +
        "            }\n" +
        "        }\n" +
        "    }\n" +
        "\n" +
        "    public String getName() {\n" +
        "        return name;\n" +
        "    }\n" +
        "\n" +
        "    public MethodHolder getBinderMethod(String name) {\n" +
        "        return methods.get(name);\n" +
        "    }\n" +
        "}";
public static final String DYNAMIC_BINDER_CREATOR = "package com.zero.support.binder;\n" +
        "\n" +
        "import android.os.IBinder;\n" +
        "\n" +
        "import com.zero.support.binder.Binder;\n" +
        "import com.zero.support.binder.BinderCreator;\n" +
        "\n" +
        "class DynamicBinderCreator<T> implements BinderCreator<T> {\n" +
        "\n" +
        "    @Override\n" +
        "    public T asInterface(IBinder binder, Class<? extends T> cls) {\n" +
        "        return Binder.asInterface(binder, cls);\n" +
        "    }\n" +
        "\n" +
        "    @Override\n" +
        "    public IBinder asBinder(T t, Class<? extends T> cls) {\n" +
        "        return Binder.asBinder(t, cls);\n" +
        "    }\n" +
        "}\n";
public static final String ENTRANCE_PARCEL_CREATOR = "package com.zero.support.binder;\n" +
        "\n" +
        "import android.os.Bundle;\n" +
        "import android.os.IBinder;\n" +
        "import android.os.Parcel;\n" +
        "import android.os.Parcelable;\n" +
        "\n" +
        "import com.zero.support.binder.Binder;\n" +
        "import com.zero.support.binder.BinderCreator;\n" +
        "import com.zero.support.binder.BinderException;\n" +
        "import com.zero.support.binder.BinderSerializable;\n" +
        "import com.zero.support.binder.ParcelCreator;\n" +
        "\n" +
        "import java.lang.reflect.Array;\n" +
        "import java.lang.reflect.Type;\n" +
        "\n" +
        "@SuppressWarnings(\"all\")\n" +
        "class EntranceParcelCreator implements ParcelCreator<Object> {\n" +
        "\n" +
        "    @Override\n" +
        "    public void writeToParcel(Parcel reply, Object object, Type type, Class rawType) throws Exception {\n" +
        "        if (rawType.isInterface()) {\n" +
        "            if (object == null) {\n" +
        "                reply.writeStrongBinder(null);\n" +
        "            } else {\n" +
        "                BinderCreator creator = Binder.getBindCreator(rawType);\n" +
        "                reply.writeStrongBinder(creator.asBinder(object, rawType));\n" +
        "            }\n" +
        "        } else if (rawType.isPrimitive()) {\n" +
        "            if (rawType == int.class) {\n" +
        "                reply.writeInt((Integer) object);\n" +
        "            } else if (rawType == float.class) {\n" +
        "                reply.writeFloat((float) object);\n" +
        "            } else if (rawType == double.class) {\n" +
        "                reply.writeDouble((double) object);\n" +
        "            } else if (rawType == boolean.class) {\n" +
        "                reply.writeInt(((boolean) object) ? 1 : 0);\n" +
        "            } else if (rawType == byte.class) {\n" +
        "                reply.writeByte((byte) object);\n" +
        "            } else if (rawType == long.class) {\n" +
        "                reply.writeLong((long) object);\n" +
        "            } else if (rawType == char.class) {\n" +
        "                reply.writeInt((char) object);\n" +
        "            } else if (rawType == short.class) {\n" +
        "                reply.writeInt((short) object);\n" +
        "            }\n" +
        "        } else if (rawType == String.class) {\n" +
        "            reply.writeString((String) object);\n" +
        "        } else if (Bundle.class.isAssignableFrom(rawType)) {\n" +
        "            reply.writeBundle((Bundle) object);\n" +
        "        } else if (rawType == (IBinder.class)) {\n" +
        "            reply.writeStrongBinder((IBinder) object);\n" +
        "        } else {\n" +
        "            ParcelCreator creator = getCreator(rawType);\n" +
        "            if (creator != null) {\n" +
        "                creator.writeToParcel(reply, object, type, rawType);\n" +
        "            } else if (Parcelable.class.isAssignableFrom(rawType)) {\n" +
        "                reply.writeParcelable((Parcelable) object, 0);\n" +
        "            } else {\n" +
        "                throw new BinderException(\"not found creator for \" + rawType);\n" +
        "            }\n" +
        "        }\n" +
        "    }\n" +
        "\n" +
        "    @Override\n" +
        "    public Object readFromParcel(Parcel data, Type type, Class rawType) throws Exception {\n" +
        "        Object object;\n" +
        "        if (rawType.isInterface()) {\n" +
        "            IBinder binder = data.readStrongBinder();\n" +
        "            if (binder == null) {\n" +
        "                object = null;\n" +
        "            } else {\n" +
        "                BinderCreator creator = Binder.getBindCreator(rawType);\n" +
        "                object = creator.asInterface(binder, rawType);\n" +
        "            }\n" +
        "        } else if (rawType.isPrimitive()) {\n" +
        "            if (rawType == int.class) {\n" +
        "                object = data.readInt();\n" +
        "            } else if (rawType == float.class) {\n" +
        "                object = data.readFloat();\n" +
        "            } else if (rawType == double.class) {\n" +
        "                object = data.readDouble();\n" +
        "            } else if (rawType == boolean.class) {\n" +
        "                object = data.readInt() == 1;\n" +
        "            } else if (rawType == byte.class) {\n" +
        "                object = data.readByte();\n" +
        "            } else if (rawType == long.class) {\n" +
        "                object = data.readLong();\n" +
        "            } else if (rawType == char.class) {\n" +
        "                object = (char) data.readInt();\n" +
        "            } else if (rawType == short.class) {\n" +
        "                object = (short) data.readInt();\n" +
        "            } else {\n" +
        "                throw new BinderException(\"not found creator for \" + rawType);\n" +
        "            }\n" +
        "        } else if (rawType == String.class) {\n" +
        "            object = data.readString();\n" +
        "        } else if (Bundle.class.isAssignableFrom(rawType)) {\n" +
        "            object = data.readBundle();\n" +
        "        } else if (rawType == (IBinder.class)) {\n" +
        "            object = data.readStrongBinder();\n" +
        "        } else {\n" +
        "            ParcelCreator creator = getCreator(rawType);\n" +
        "            if (creator != null) {\n" +
        "                object = creator.readFromParcel(data, type, rawType);\n" +
        "            } else if (Parcelable.class.isAssignableFrom(rawType)) {\n" +
        "                object = data.readParcelable(rawType.getClassLoader());\n" +
        "            } else {\n" +
        "                throw new BinderException(\"not found creator for \" + rawType);\n" +
        "            }\n" +
        "        }\n" +
        "        return object;\n" +
        "    }\n" +
        "\n" +
        "    @SuppressWarnings(\"all\")\n" +
        "    static ParcelCreator getCreator(Class<?> rawType) {\n" +
        "        ParcelCreator creator = null;\n" +
        "        if (BinderSerializable.class.isAssignableFrom(rawType)) {\n" +
        "            creator = Binder.getParcelCreator(BinderSerializable.class);\n" +
        "        } else if (rawType.isArray()) {\n" +
        "            creator = Binder.getParcelCreator(Array.class);\n" +
        "        }\n" +
        "        if (creator == null) {\n" +
        "            creator = Binder.getParcelCreator(rawType);\n" +
        "        }\n" +
        "        return creator;\n" +
        "    }\n" +
        "}\n";

public static final String FILE_PARCEL_CREATOR = "package com.zero.support.binder;\n" +
        "\n" +
        "import android.os.Parcel;\n" +
        "\n" +
        "import java.io.File;\n" +
        "import java.lang.reflect.Type;\n" +
        "\n" +
        "public class FileParcelCreator implements ParcelCreator<File> {\n" +
        "    @Override\n" +
        "    public void writeToParcel(Parcel parcel, File target, Type type, Class<File> rawType) throws Exception {\n" +
        "        if (target == null) {\n" +
        "            parcel.writeString(null);\n" +
        "        } else {\n" +
        "            parcel.writeString(target.getAbsolutePath());\n" +
        "        }\n" +
        "    }\n" +
        "\n" +
        "    @Override\n" +
        "    public File readFromParcel(Parcel parcel, Type type, Class<File> rawType) throws Exception {\n" +
        "        String path = parcel.readString();\n" +
        "        if (path == null) {\n" +
        "            return null;\n" +
        "        } else {\n" +
        "            return new File(path);\n" +
        "        }\n" +
        "    }\n" +
        "}\n";

public static final String LIST_PARCEL_CREATOR = "package com.zero.support.binder;\n" +
        "\n" +
        "import android.os.Parcel;\n" +
        "\n" +
        "import com.zero.support.binder.Binder;\n" +
        "import com.zero.support.binder.ParcelCreator;\n" +
        "\n" +
        "import java.lang.reflect.ParameterizedType;\n" +
        "import java.lang.reflect.Type;\n" +
        "import java.util.ArrayList;\n" +
        "import java.util.List;\n" +
        "\n" +
        "class ListParcelCreator implements ParcelCreator<List<?>> {\n" +
        "\n" +
        "    @Override\n" +
        "    public void writeToParcel(Parcel parcel, List<?> target, Type type, Class<List<?>> rawType) throws Exception {\n" +
        "        ParameterizedType parameterizedType = (ParameterizedType) type;\n" +
        "\n" +
        "        Type[] types = parameterizedType.getActualTypeArguments();\n" +
        "        if (types.length == 0) {\n" +
        "            parcel.writeList(target);\n" +
        "            return;\n" +
        "        }\n" +
        "        if (target == null) {\n" +
        "            parcel.writeInt(-1);\n" +
        "            return;\n" +
        "        }\n" +
        "        parcel.writeInt(target.size());\n" +
        "        ParcelCreator creator = Binder.getParcelCreator((Class<?>) types[0]);\n" +
        "        if (creator == null) {\n" +
        "            throw new RuntimeException(\"not found creator for \" + types[0]);\n" +
        "        }\n" +
        "        for (Object o : target) {\n" +
        "            creator.writeToParcel(parcel, o, types[0], (Class<?>) types[0]);\n" +
        "        }\n" +
        "    }\n" +
        "\n" +
        "    @Override\n" +
        "    public List<?> readFromParcel(Parcel parcel, Type type, Class<List<?>> rawType) throws Exception {\n" +
        "        ParameterizedType parameterizedType = (ParameterizedType) type;\n" +
        "        Type[] types = parameterizedType.getActualTypeArguments();\n" +
        "        if (types.length == 0) {\n" +
        "            return parcel.readArrayList(getClass().getClassLoader());\n" +
        "        }\n" +
        "\n" +
        "        int N = parcel.readInt();\n" +
        "        if (N == -1) {\n" +
        "            return null;\n" +
        "        }\n" +
        "        List list = new ArrayList();\n" +
        "        ParcelCreator creator = Binder.getParcelCreator((Class<?>) types[0]);\n" +
        "        if (creator == null) {\n" +
        "            throw new RuntimeException(\"not found creator for \" + types[0]);\n" +
        "        }\n" +
        "        for (int i = 0; i < N; i++) {\n" +
        "            list.add(creator.readFromParcel(parcel, types[0], (Class<?>) types[0]));\n" +
        "        }\n" +
        "        return list;\n" +
        "    }\n" +
        "}";
public static final String MAP_PARCEL_CREATOR = "\n" +
        "package com.zero.support.binder;\n" +
        "\n" +
        "import android.os.Parcel;\n" +
        "\n" +
        "import com.zero.support.binder.Binder;\n" +
        "import com.zero.support.binder.ParcelCreator;\n" +
        "\n" +
        "import java.lang.reflect.ParameterizedType;\n" +
        "import java.lang.reflect.Type;\n" +
        "import java.util.HashMap;\n" +
        "import java.util.Map;\n" +
        "\n" +
        "@SuppressWarnings(\"all\")\n" +
        "class MapParcelCreator implements ParcelCreator<Map<?, ?>> {\n" +
        "\n" +
        "\n" +
        "    @Override\n" +
        "    public void writeToParcel(Parcel parcel, Map<?, ?> target, Type type, Class<Map<?, ?>> rawType) throws Exception {\n" +
        "        ParameterizedType parameterizedType = (ParameterizedType) type;\n" +
        "\n" +
        "        Type[] types = parameterizedType.getActualTypeArguments();\n" +
        "        if (types.length == 0) {\n" +
        "            parcel.writeMap(target);\n" +
        "            return;\n" +
        "        }\n" +
        "        if (target == null) {\n" +
        "            parcel.writeInt(-1);\n" +
        "            return;\n" +
        "        }\n" +
        "        parcel.writeInt(target.size());\n" +
        "        ParcelCreator keyCreator = Binder.getParcelCreator((Class<?>) types[0]);\n" +
        "        ParcelCreator valueCreator = Binder.getParcelCreator((Class<?>) types[1]);\n" +
        "        if (keyCreator == null || valueCreator == null) {\n" +
        "            throw new RuntimeException(\"not found creator for \" + types[0] + \" \" + types[1]);\n" +
        "        }\n" +
        "        for (Map.Entry<?, ?> entry : target.entrySet()) {\n" +
        "            keyCreator.writeToParcel(parcel, entry.getKey(), types[0], (Class<?>) types[0]);\n" +
        "            valueCreator.writeToParcel(parcel, entry.getValue(), types[1], (Class) types[1]);\n" +
        "        }\n" +
        "    }\n" +
        "\n" +
        "    @Override\n" +
        "    public Map<?, ?> readFromParcel(Parcel parcel, Type type, Class<Map<?, ?>> rawType) throws Exception {\n" +
        "        ParameterizedType parameterizedType = (ParameterizedType) type;\n" +
        "        Type[] types = parameterizedType.getActualTypeArguments();\n" +
        "        if (types.length == 0) {\n" +
        "            return parcel.readHashMap(getClass().getClassLoader());\n" +
        "        }\n" +
        "\n" +
        "        int N = parcel.readInt();\n" +
        "        if (N == -1) {\n" +
        "            return null;\n" +
        "        }\n" +
        "        Map map = new HashMap(N);\n" +
        "        ParcelCreator keyCreator = Binder.getParcelCreator((Class<?>) types[0]);\n" +
        "        ParcelCreator valueCreator = Binder.getParcelCreator((Class<?>) types[1]);\n" +
        "        if (keyCreator == null || valueCreator == null) {\n" +
        "            throw new RuntimeException(\"not found creator for \" + types[0]);\n" +
        "        }\n" +
        "        for (int i = 0; i < N; i++) {\n" +
        "            map.put(keyCreator.readFromParcel(parcel, types[0], (Class<?>) types[0]), valueCreator.readFromParcel(parcel, types[1], (Class<?>) types[1]));\n" +
        "        }\n" +
        "        return map;\n" +
        "    }\n" +
        "}";
public static final String METHOD_HOLDER = "package com.zero.support.binder;\n" +
        "\n" +
        "import java.lang.reflect.Method;\n" +
        "import java.lang.reflect.Type;\n" +
        "\n" +
        "class MethodHolder {\n" +
        "    public String name;\n" +
        "    public Method method;\n" +
        "    public Type[] types;\n" +
        "    public Class<?>[] params;\n" +
        "\n" +
        "    public Type returnType;\n" +
        "    public Class<?> returnCls;\n" +
        "\n" +
        "    public MethodHolder(Method method) {\n" +
        "        this.method = method;\n" +
        "        this.types = method.getGenericParameterTypes();\n" +
        "        this.returnType = method.getGenericReturnType();\n" +
        "        BinderName nameAno = method.getAnnotation(BinderName.class);\n" +
        "        if (nameAno != null) {\n" +
        "            this.name = nameAno.value();\n" +
        "        } else {\n" +
        "            this.name = method.getName();\n" +
        "        }\n" +
        "        this.params = method.getParameterTypes();\n" +
        "        this.returnCls = method.getReturnType();\n" +
        "    }\n" +
        "}";
public static final String PARCEL_CREATOR = "package com.zero.support.binder;\n" +
        "\n" +
        "import android.os.Parcel;\n" +
        "\n" +
        "import java.lang.reflect.Type;\n" +
        "\n" +
        "public interface ParcelCreator<T> {\n" +
        "    void writeToParcel(Parcel parcel, T target, Type type, Class<T> rawType) throws Exception;\n" +
        "\n" +
        "    T readFromParcel(Parcel parcel, Type type, Class<T> rawType) throws Exception;\n" +
        "}";
public static final String SPARSE_ARRAY_PARCEL_CREATOR = "\n" +
        "package com.zero.support.binder;\n" +
        "\n" +
        "import android.os.Parcel;\n" +
        "import android.util.SparseArray;\n" +
        "\n" +
        "import com.zero.support.binder.Binder;\n" +
        "import com.zero.support.binder.ParcelCreator;\n" +
        "\n" +
        "import java.lang.reflect.ParameterizedType;\n" +
        "import java.lang.reflect.Type;\n" +
        "import java.util.ArrayList;\n" +
        "import java.util.List;\n" +
        "\n" +
        "@SuppressWarnings(\"all\")\n" +
        "class SparseArrayParcelCreator implements ParcelCreator<SparseArray<?>> {\n" +
        "\n" +
        "    @Override\n" +
        "    public void writeToParcel(Parcel parcel, SparseArray<?> target, Type type, Class<SparseArray<?>> rawType) throws Exception {\n" +
        "        ParameterizedType parameterizedType = (ParameterizedType) type;\n" +
        "\n" +
        "        Type[] types = parameterizedType.getActualTypeArguments();\n" +
        "        if (types.length == 0) {\n" +
        "            parcel.writeSparseArray(target);\n" +
        "            return;\n" +
        "        }\n" +
        "        if (target == null) {\n" +
        "            parcel.writeInt(-1);\n" +
        "            return;\n" +
        "        }\n" +
        "        parcel.writeInt(target.size());\n" +
        "        ParcelCreator creator = Binder.getParcelCreator((Class<?>) types[0]);\n" +
        "        if (creator == null) {\n" +
        "            throw new RuntimeException(\"not found creator for \" + types[0]);\n" +
        "        }\n" +
        "        for (int i = 0; i < target.size(); i++) {\n" +
        "            parcel.writeInt(target.keyAt(i));\n" +
        "            creator.writeToParcel(parcel, target.valueAt(i), types[0], (Class<?>) types[0]);\n" +
        "        }\n" +
        "    }\n" +
        "\n" +
        "    @Override\n" +
        "    public SparseArray<?> readFromParcel(Parcel parcel, Type type, Class<SparseArray<?>> rawType) throws Exception {\n" +
        "        ParameterizedType parameterizedType = (ParameterizedType) type;\n" +
        "        Type[] types = parameterizedType.getActualTypeArguments();\n" +
        "        if (types.length == 0) {\n" +
        "            return parcel.readSparseArray(getClass().getClassLoader());\n" +
        "        }\n" +
        "\n" +
        "        int N = parcel.readInt();\n" +
        "        if (N == -1) {\n" +
        "            return null;\n" +
        "        }\n" +
        "        SparseArray array = new SparseArray();\n" +
        "        ParcelCreator creator = Binder.getParcelCreator((Class<?>) types[0]);\n" +
        "        if (creator == null) {\n" +
        "            throw new RuntimeException(\"not found creator for \" + types[0]);\n" +
        "        }\n" +
        "        for (int i = 0; i < N; i++) {\n" +
        "            array.put(parcel.readInt(),creator.readFromParcel(parcel, types[0], (Class<?>) types[0]));\n" +
        "        }\n" +
        "        return array;\n" +
        "    }\n" +
        "}";

}