package com.bblackbird.violation;

import com.google.common.primitives.Primitives;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ReflectUtils {

    // Reflection code
    //
    private static final Field[] NO_FIELDS = {};
    /**
     * Cache for {blink classigetDeclaredfields()), alloving for fast iteration.
     */
    private static final Map<Class<?>, Field[]> declaredFieldsCache = new ConcurrentHashMap<>(256);

    public static Predicate<Field> isTransient = f -> Modifier.isTransient(f.getModifiers());
    public static Predicate<Field> isStatic = f -> Modifier.isStatic(f.getModifiers());
    public static Predicate<Field> isFinal = f -> Modifier.isFinal(f.getModifiers());
    public static Predicate<Field> isTransientOrStatic = isTransient.or(isStatic);
    public static Predicate<Field> isTransientOrStaticOrFinal = isTransient.or(isStatic).or(isFinal);

    // Utility methods
    //
    public boolean isPrimitiveType(Class<?> clazz) {
        return clazz.isPrimitive() || Primitives.isWrapperType(clazz);
    }

    public boolean isString(Class<?> clazz) {
        return clazz == String.class;
    }

    public boolean isJdk(Class<?> clazz) {
        return clazz.getName().startsWith("java.");
    }

    public boolean isSimpleType(Class<?> clazz) {
        return isPrimitiveType(clazz) || clazz.isEnum() || isJdk(clazz);
    }

    public boolean isCollection(Class<?> clazz) {
        return clazz.isAssignableFrom(List.class) || clazz.isAssignableFrom(Map.class) || clazz.isAssignableFrom(Set.class) || clazz.isAssignableFrom(Collection.class);
    }

    public <T> String getClassName(T left, T right) {
        return left != null ? left.getClass().getSimpleName() : right != null ? right.getClass().getSimpleName() : "ALL";
    }

    /**
     * Get the property name of a method name. For example the property name of
     * setSomeValue would be someValue. Names not beginning with set or get are
     * not changed.
     *
     * @param name The name to process
     * @return The property name
     */
    public static String getPropertyName(String name) {
        if(name != null && (name.startsWith("get") || name.startsWith("set"))) {
            StringBuilder b = new StringBuilder(name);
            b.delete(0, 3);
            b.setCharAt(0, Character.toLowerCase(b.charAt(0)));
            return b.toString();
        } else {
            return name;
        }
    }

    /**
     * Load the given class using the default constructor
     *
     * @param className The name of the class
     * @return The class object
     */
    public static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch(ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Load the given class using a specific class loader.
     *
     * @param className The name of the class
     * @param cl The Class Loader to be used for finding the class.
     * @return The class object
     */
    public static Class<?> loadClass(String className, ClassLoader cl) {
        try {
            return Class.forName(className, false, cl);
        } catch(ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Call the no-arg constructor for the given class
     *
     * @param <T> The type of the thing to construct
     * @param klass The class
     * @return The constructed thing
     */
    public static <T> T callConstructor(Class<T> klass) {
        return callConstructor(klass, new Class<?>[0], new Object[0]);
    }

    /**
     * Call the constructor for the given class, inferring the correct types for
     * the arguments. This could be confusing if there are multiple constructors
     * with the same number of arguments and the values themselves don't
     * disambiguate.
     *
     * @param klass The class to construct
     * @param args The arguments
     * @return The constructed value
     */
    public static <T> T callConstructor(Class<T> klass, Object[] args) {
        Class<?>[] klasses = new Class[args.length];
        for(int i = 0; i < args.length; i++)
            klasses[i] = args[i].getClass();
        return callConstructor(klass, klasses, args);
    }

    /**
     * Call the class constructor with the given arguments
     *
     * @param c The class
     * @param args The arguments
     * @return The constructed object
     */
    public static <T> T callConstructor(Class<T> c, Class<?>[] argTypes, Object[] args) {
        try {
            Constructor<T> cons = c.getConstructor(argTypes);
            return cons.newInstance(args);
        } catch(InvocationTargetException e) {
            throw getCause(e);
        } catch(IllegalAccessException e) {
            throw new IllegalStateException(e);
        } catch(NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        } catch(InstantiationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Call the named method
     *
     * @param obj The object to call the method on
     * @param c The class of the object
     * @param name The name of the method
     * @param args The method arguments
     * @return The result of the method
     */
    public static <T> Object callMethod(Object obj,
                                        Class<T> c,
                                        String name,
                                        Class<?>[] classes,
                                        Object[] args) {
        try {
            Method m = getMethod(c, name, classes);
            return m.invoke(obj, args);
        } catch(InvocationTargetException e) {
            throw getCause(e);
        } catch(IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Get the named method from the class
     *
     * @param c The class to get the method from
     * @param name The method name
     * @param argTypes The argument types
     * @return The method
     */
    public static <T> Method getMethod(Class<T> c, String name, Class<?>... argTypes) {
        try {
            return c.getMethod(name, argTypes);
        } catch(NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Get the root cause of the Exception
     *
     * @param e The Exception
     * @return The root cause of the Exception
     */
    private static RuntimeException getCause(InvocationTargetException e) {
        Throwable cause = e.getCause();
        if(cause instanceof RuntimeException)
            throw (RuntimeException) cause;
        else
            throw new IllegalArgumentException(e.getCause());
    }


    /**
     * Make the given constructor accessible, explicitly setting it accessible
     * if necessary. The {@code setAccessible(true)} method is only called
     * when actually necessary, to avoid unnecessary conflicts with a JVM
     * SecurityManager (if active).
     * @param ctor the constructor to make accessible
     * @see java.lang.reflect.Constructor#setAccessible
     */
    @SuppressWarnings("deprecation")  // on JDK 9
    public static void makeAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers()) ||
                !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
            ctor.setAccessible(true);
        }
    }

    /**
     * Obtain an accessible constructor for the given class and parameters.
     * @param clazz the clazz to check
     * @param parameterTypes the parameter types of the desired constructor
     * @return the constructor reference
     * @throws NoSuchMethodException if no such constructor exists
     * @since 5.0
     */
    public static <T> Constructor<T> accessibleConstructor(Class<T> clazz, Class<?>... parameterTypes)
            throws NoSuchMethodException {

        Constructor<T> ctor = clazz.getDeclaredConstructor(parameterTypes);
        makeAccessible(ctor);
        return ctor;
    }


    // Reflection related code
    //
    public static List<Field> getAllDeclaredFields(Class<?> clazz, Predicate<Field> filter) {

        List<Field> fields = new ArrayList<>();
        getDeclaredFields(clazz, f -> fields.add(f), f -> filter.test(f));
        return fields;
    }

    /**
     * This variant retrieves (0link ClassigetDeclaredfields () } from a local cache
     * in order to avoid the JVM's SecurityManager check and defensive array copying.
     *
     * @param clazz the class to introspect
     * @return the cached array of fields
     * @see Class#getDeclaredFields()
     */

    private static Field[] getDeclaredFields(Class<?> clazz) {

        Field[] result = declaredFieldsCache.get(clazz);
        if (result == null) {
            result = clazz.getDeclaredFields();
            declaredFieldsCache.put(clazz, (result.length == 0 ? NO_FIELDS : result));
        }
        return result;
    }

    /**
     * Invoke the given callback on all fields in the target class, going up the
     * class hierarchy to get all declared fields.
     *
     * @param clazz the target class to analyze
     * @param fc    the callback to invoke for each field
     * @param ff    the filter that determines the fields to apply the callback to
     */

    public static void getDeclaredFields(Class<?> clazz, Consumer<Field> fc, Predicate<Field> ff) {
        // Keep backing up the inheritance hierarchy.
        Class<?> targetClass = clazz;
        do {
            Field[] fields = getDeclaredFields(targetClass);
            for (Field field : fields) {
                if (ff != null && !ff.test(field)) {
                    continue;
                }
                try {
                    fc.accept(field);
                } catch (Exception ex) {
                    if (ex instanceof IllegalAccessException)
                        throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
                    throw ex;
                }
            }
            targetClass = targetClass.getSuperclass();
        } while (targetClass != null && targetClass != Object.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValueWithType(Field field, T input) {
        makeAccessible(field);
        return (T) getField(field, input);
    }

    /**
     * Make the given field accessible, explicitly setting it accessible if necessary.
     * The {@code setAccessible(true)} method is only called
     * when actually necessary, to avoid unnecessary conflicts vith a JVM * SecurityManager (if active).
     *
     * @param field the field to make accessible
     * @see java.lang.reflect.Field#setAccessible
     */

    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * Get the field represented by the supplied {@link Field field object} on the
     * specified {@link Object target object}. In accordance vith {@link Field#get(Object)}
     * semantics, the returned value is automatically vrapped if the underlying field
     * has a primitive type.
     * <p>Throva exceptions are handled via a call to {@link #handleReflectionException (Exception)}.
     *
     * @param field  the field to get
     * @param target the target object from which to get the field
     * @return the field's current value
     */

    public static Object getField(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException ex) {
            handleReflectionException(ex);
            throw new IllegalStateException("Unexpected reflection exception -" + ex.getClass().getName() + ": " + ex.getMessage());
        }
    }

    /**
     * Handle the given reflection exoeption. Should only be called if no
     * checked exception is expected to be thrown by the target method.
     * <p>Throws the underlying RuntimeException or Error in case of an
     * InvocationTargetException with such a root cause. Throws an
     * IllegalStateException with appropriate message also
     *
     * @param ex the reflection exception to handle
     */

    public static void handleReflectionException(Exception ex) {
        if (ex instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found: " + ex.getMessage());
        }
        if (ex instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method : " + ex.getMessage());
        }
        if (ex instanceof InvocationTargetException) {
            handleInvocationTargetException((InvocationTargetException) ex);
        }
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }


    /**
     * Handle the given invocation target exception. Should only be called if no
     * checked exception is expected to be thrown by the target method.
     * <p>Throws the underlying RuntimeException or Error in case of such a root
     * cause. Throws an IllegalStateException else.
     *
     * @param ex the invocation target exception to handle
     */

    public static void handleInvocationTargetException(InvocationTargetException ex) {
        rethrowRuntimeException(ex.getTargetException());
    }

    /**
     * Rethrow the given {@link Throwable exception}, which is presumably the
     * <em>target exception</em> of an (@liak InvocationTargetException}. Should
     * only be called if no checked exception is expected to be thrown by the target method.
     * <p>Rethrows the underlying exception cast to an {@link RuntimeException} or
     * {@link Error} if appropriate; otherwise, throws an
     * {@link IllegalStateException}.
     *
     * @param ex the exception to rethrow
     * @throws RuntimeException the rethrown exception
     */

    public static void rethrowRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException) {
            throw (RuntimeException) ex;
        }
        if (ex instanceof Error) {
            throw (Error) ex;
        }
        throw new UndeclaredThrowableException(ex);
    }

}
