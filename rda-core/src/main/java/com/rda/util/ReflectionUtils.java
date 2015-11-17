/*******************************************************************************
 * Copyright 2011 Soofa Team(www.soofa.org).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.rda.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javassist.NotFoundException;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;

import com.rda.core.convert.StringToByteArrayConverter;
import com.rda.core.convert.StringToCharArrayConverter;
import com.rda.core.convert.StringToDateConverter;


/**
 * 代理并扩展spring的ReflectionUtils反射工具类,为soofa提供统一的工具视图
 * 
 * @see org.springframework.util.ReflectionUtils
 * @author 汪一鸣
 */
public final class ReflectionUtils extends org.springframework.util.ReflectionUtils {
	public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];
	public static final Pattern DESC_PATTERN = Pattern.compile(RegularExpression.DESC_REGEX);
	private static GenericConversionService conversionService;
	/**
	 * void(V).
	 */
	public static final char JVM_VOID = 'V';

	/**
	 * boolean(Z).
	 */
	public static final char JVM_BOOLEAN = 'Z';

	/**
	 * byte(B).
	 */
	public static final char JVM_BYTE = 'B';

	/**
	 * char(C).
	 */
	public static final char JVM_CHAR = 'C';

	/**
	 * double(D).
	 */
	public static final char JVM_DOUBLE = 'D';

	/**
	 * float(F).
	 */
	public static final char JVM_FLOAT = 'F';

	/**
	 * int(I).
	 */
	public static final char JVM_INT = 'I';

	/**
	 * long(J).
	 */
	public static final char JVM_LONG = 'J';

	/**
	 * short(S).
	 */
	public static final char JVM_SHORT = 'S';
	private static final ConcurrentMap<String, Class<?>> DESC_CLASS_CACHE = new ConcurrentHashMap<String, Class<?>>();

	static {
		conversionService = new DefaultConversionService();
		conversionService.addConverter(new StringToDateConverter());
		conversionService.addConverter(new StringToByteArrayConverter());
		conversionService.addConverter(new StringToCharArrayConverter());
	}

	/**
	 * <p>增加类型转换器,在进行setFieldValue时,自动将对象类型的数据进行转型,默认提供以下三种转换器</p>
	 * <p>StringToDateConverter,StringToByteArrayConverter,StringToByteArrayConverter</p>
	 * 
	 * @param converter
	 * @author 汪一鸣
	 */
	public static void addConverter(Converter<?, ?> converter) {
		conversionService.addConverter(converter);
	}

	public static void setFieldValue(final Object target, final String fname, final Class<?> ftype, final Object fvalue)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		if (target == null || StringUtils.isBlank(fname)
				|| (fvalue != null && !ftype.isAssignableFrom(fvalue.getClass()))) {
			return;
		}
		final Class<? extends Object> clazz = target.getClass();
		try {
			final Method method = clazz.getDeclaredMethod(
					"set" + Character.toUpperCase(fname.charAt(0)) + fname.substring(1), ftype);
			if (!Modifier.isPublic(method.getModifiers())) {
				method.setAccessible(true);
			}
			method.invoke(target, fvalue);

		} catch (Exception me) {
			final Field field = clazz.getDeclaredField(fname);
			if (!Modifier.isPublic(field.getModifiers())) {
				field.setAccessible(true);
			}
			field.set(target, fvalue);
		}
	}

	/**
	 * desc to class. "[Z" => boolean[].class "[[Ljava/util/Map;" => java.util.Map[][].class
	 * 
	 * @param desc desc.
	 * @return Class instance.
	 * @throws ClassNotFoundException
	 */
	public static Class<?> desc2class(String desc) throws ClassNotFoundException {
		return desc2class(ClassHelper.getClassLoader(), desc);
	}

	/**
	 * desc to class. "[Z" => boolean[].class "[[Ljava/util/Map;" => java.util.Map[][].class
	 * 
	 * @param cl ClassLoader instance.
	 * @param desc desc.
	 * @return Class instance.
	 * @throws ClassNotFoundException
	 */
	private static Class<?> desc2class(ClassLoader cl, String desc) throws ClassNotFoundException {
		switch (desc.charAt(0)) {
			case JVM_VOID:
				return void.class;
			case JVM_BOOLEAN:
				return boolean.class;
			case JVM_BYTE:
				return byte.class;
			case JVM_CHAR:
				return char.class;
			case JVM_DOUBLE:
				return double.class;
			case JVM_FLOAT:
				return float.class;
			case JVM_INT:
				return int.class;
			case JVM_LONG:
				return long.class;
			case JVM_SHORT:
				return short.class;
			case 'L':
				desc = desc.substring(1, desc.length() - 1).replace('/', '.'); // "Ljava/lang/Object;"
																				// ==>
																				// "java.lang.Object"
				break;
			case '[':
				desc = desc.replace('/', '.');  // "[[Ljava/lang/Object;" ==> "[[Ljava.lang.Object;"
				break;
			default:
				throw new ClassNotFoundException("Class not found: " + desc);
		}

		if (cl == null)
			cl = ClassHelper.getClassLoader();
		Class<?> clazz = DESC_CLASS_CACHE.get(desc);
		if (clazz == null) {
			clazz = Class.forName(desc, true, cl);
			DESC_CLASS_CACHE.put(desc, clazz);
		}
		return clazz;
	}

	/**
	 * get class array instance.
	 * 
	 * @param desc desc.
	 * @return Class class array.
	 * @throws ClassNotFoundException
	 */
	public static Class<?>[] desc2classArray(String desc) throws ClassNotFoundException {
		Class<?>[] ret = desc2classArray(ClassHelper.getClassLoader(), desc);
		return ret;
	}

	/**
	 * get class array instance.
	 * 
	 * @param cl ClassLoader instance.
	 * @param desc desc.
	 * @return Class[] class array.
	 * @throws ClassNotFoundException
	 */
	private static Class<?>[] desc2classArray(ClassLoader cl, String desc) throws ClassNotFoundException {
		if (desc.length() == 0)
			return EMPTY_CLASS_ARRAY;

		List<Class<?>> cs = new ArrayList<Class<?>>();
		Matcher m = DESC_PATTERN.matcher(desc);
		while (m.find())
			cs.add(desc2class(cl, m.group()));
		return cs.toArray(EMPTY_CLASS_ARRAY);
	}

	/**
	 * get class desc. boolean[].class => "[Z" Object.class => "Ljava/lang/Object;"
	 * 
	 * @param c class.
	 * @return desc.
	 * @throws NotFoundException
	 */
	public static String getDesc(Class<?> c) {
		StringBuilder ret = new StringBuilder();

		while (c.isArray()) {
			ret.append('[');
			c = c.getComponentType();
		}

		if (c.isPrimitive()) {
			String t = c.getName();
			if ("void".equals(t))
				ret.append(JVM_VOID);
			else if ("boolean".equals(t))
				ret.append(JVM_BOOLEAN);
			else if ("byte".equals(t))
				ret.append(JVM_BYTE);
			else if ("char".equals(t))
				ret.append(JVM_CHAR);
			else if ("double".equals(t))
				ret.append(JVM_DOUBLE);
			else if ("float".equals(t))
				ret.append(JVM_FLOAT);
			else if ("int".equals(t))
				ret.append(JVM_INT);
			else if ("long".equals(t))
				ret.append(JVM_LONG);
			else if ("short".equals(t))
				ret.append(JVM_SHORT);
		} else {
			ret.append('L');
			ret.append(c.getName().replace('.', '/'));
			ret.append(';');
		}
		return ret.toString();
	}

	/**
	 * get constructor desc. "()V", "(Ljava/lang/String;I)V"
	 * 
	 * @param c constructor.
	 * @return desc
	 */
	public static String getDesc(final Constructor<?> c) {
		StringBuilder ret = new StringBuilder("(");
		Class<?>[] parameterTypes = c.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++)
			ret.append(getDesc(parameterTypes[i]));
		ret.append(')').append('V');
		return ret.toString();
	}

	/**
	 * get method desc. int do(int arg1) => "do(I)I" void do(String arg1,boolean arg2) =>
	 * "do(Ljava/lang/String;Z)V"
	 * 
	 * @param m method.
	 * @return desc.
	 */
	public static String getDesc(final Method m) {
		StringBuilder ret = new StringBuilder(m.getName()).append('(');
		Class<?>[] parameterTypes = m.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++)
			ret.append(getDesc(parameterTypes[i]));
		ret.append(')').append(getDesc(m.getReturnType()));
		return ret.toString();
	}

	/**
	 * get class array desc. [int.class, boolean[].class, Object.class] => "I[ZLjava/lang/Object;"
	 * 
	 * @param cs class array.
	 * @return desc.
	 * @throws NotFoundException
	 */
	public static String getDesc(final Class<?>[] cs) {
		if (cs.length == 0)
			return "";

		StringBuilder sb = new StringBuilder(64);
		for (Class<?> c : cs)
			sb.append(getDesc(c));
		return sb.toString();
	}

	/**
	 * get name. java.lang.Object[][].class => "java.lang.Object[][]"
	 * 
	 * @param c class.
	 * @return name.
	 */
	public static String getName(Class<?> c) {
		if (c.isArray()) {
			StringBuilder sb = new StringBuilder();
			do {
				sb.append("[]");
				c = c.getComponentType();
			} while (c.isArray());

			return c.getName() + sb.toString();
		}
		return c.getName();
	}

	/**
	 * get method desc. "(I)I", "()V", "(Ljava/lang/String;Z)V"
	 * 
	 * @param m method.
	 * @return desc.
	 */
	public static String getDescWithoutMethodName(Method m) {
		StringBuilder ret = new StringBuilder();
		ret.append('(');
		Class<?>[] parameterTypes = m.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++)
			ret.append(getDesc(parameterTypes[i]));
		ret.append(')').append(getDesc(m.getReturnType()));
		return ret.toString();
	}

	public static Class<?> forName(String name) {
		try {
			return name2class(name);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("Not found class " + name + ", cause: " + e.getMessage(), e);
		}
	}

	/**
	 * name to class. "boolean" => boolean.class "java.util.Map[][]" => java.util.Map[][].class
	 * 
	 * @param name name.
	 * @return Class instance.
	 */
	public static Class<?> name2class(String name) throws ClassNotFoundException {
		return name2class(ClassHelper.getClassLoader(), name);
	}

	/**
	 * name to class. "boolean" => boolean.class "java.util.Map[][]" => java.util.Map[][].class
	 * 
	 * @param cl ClassLoader instance.
	 * @param name name.
	 * @return Class instance.
	 */
	private static Class<?> name2class(ClassLoader cl, String name) throws ClassNotFoundException {
		int c = 0, index = name.indexOf('[');
		if (index > 0) {
			c = (name.length() - index) / 2;
			name = name.substring(0, index);
		}
		if (c > 0) {
			StringBuilder sb = new StringBuilder();
			while (c-- > 0)
				sb.append("[");

			if ("void".equals(name))
				sb.append(JVM_VOID);
			else if ("boolean".equals(name))
				sb.append(JVM_BOOLEAN);
			else if ("byte".equals(name))
				sb.append(JVM_BYTE);
			else if ("char".equals(name))
				sb.append(JVM_CHAR);
			else if ("double".equals(name))
				sb.append(JVM_DOUBLE);
			else if ("float".equals(name))
				sb.append(JVM_FLOAT);
			else if ("int".equals(name))
				sb.append(JVM_INT);
			else if ("long".equals(name))
				sb.append(JVM_LONG);
			else if ("short".equals(name))
				sb.append(JVM_SHORT);
			else
				sb.append('L').append(name).append(';'); // "java.lang.Object" ==>
															// "Ljava.lang.Object;"
			name = sb.toString();
		} else {
			if ("void".equals(name))
				return void.class;
			else if ("boolean".equals(name))
				return boolean.class;
			else if ("byte".equals(name))
				return byte.class;
			else if ("char".equals(name))
				return char.class;
			else if ("double".equals(name))
				return double.class;
			else if ("float".equals(name))
				return float.class;
			else if ("int".equals(name))
				return int.class;
			else if ("long".equals(name))
				return long.class;
			else if ("short".equals(name))
				return short.class;
		}

		if (cl == null)
			cl = ClassHelper.getClassLoader();
		return Class.forName(name, true, cl);
	}

	/**
	 * <p>通过field名称,从指定的对象里获取field的值</p>
	 * 
	 * @param target
	 * @param fieldName
	 * @return
	 * @author 汪一鸣
	 */
	public static Object getFieldValue(Object target, String fieldName) {
		Field field = ReflectionUtils.findField(target.getClass(), fieldName);
		boolean accessible = field.isAccessible();
		ReflectionUtils.makeAccessible(field);
		Object value = ReflectionUtils.getField(field, target);
		field.setAccessible(accessible);
		return value;
	}

	/**
	 * <p>将指定的值,设置到指定对象的指定field中,支持对象的自动转型,例如类型不一样,但是field name一样的情况</p>
	 * 
	 * @param target
	 * @param fieldName
	 * @param value
	 * @author 汪一鸣
	 */
	public static void setFieldValue(Object target, String fieldName, Object value) {
		Field field = ReflectionUtils.findField(target.getClass(), fieldName);
		if (conversionService.canConvert(value.getClass(), field.getType())) {
			Object convertedValue = conversionService.convert(value, field.getType());
			boolean accessible = field.isAccessible();
			ReflectionUtils.makeAccessible(field);
			ReflectionUtils.setField(field, target, convertedValue);
			field.setAccessible(accessible);
		} else {
			throw new RuntimeException("Can't convert source to target class.Pleasd add new Converter.");
		}

	}

	/**
	 * <p>将一个String转换成指定的类型的对象</p>
	 * 
	 * @param value
	 * @param targetClass
	 * @return
	 * @author 汪一鸣
	 */
	public static Object convertString(Object value, Class<?> targetClass) {
		if (value == null) {
			return value;
		}
		if (conversionService.canConvert(value.getClass(), targetClass)) {
			Object convertedValue = conversionService.convert(value, targetClass);
			return convertedValue;
		} else {
			throw new RuntimeException("Can't convert source to target class.Pleasd add new Converter.");
		}
	}

	/**
	 * <p>查找指定类的所有field,包括其所有父类的</p>
	 * 
	 * @param targetClass
	 * @return
	 * @author 汪一鸣
	 */
	public static List<Field> findAllFields(final Class<?> targetClass) {
		Assert.notNull(targetClass, "Class must not be null");
		Class<?> searchType = targetClass;
		List<Field> allFields = new ArrayList<Field>();
		while (!Object.class.equals(searchType) && searchType != null) {
			Field[] fields = searchType.getDeclaredFields();
			for (Field field : fields) {
				allFields.add(field);
			}
			searchType = searchType.getSuperclass();
		}
		return allFields;
	}

	/**
	 * <p>查找指定类的所有field的名称,包括其所有父类的</p>
	 * 
	 * @param targetClass
	 * @return
	 * @author 汪一鸣
	 */
	public static List<String> findAllFieldNames(Class<?> targetClass) {
		Assert.notNull(targetClass, "Class must not be null");
		Class<?> searchType = targetClass;
		List<String> allFieldNames = new ArrayList<String>();
		while (!Object.class.equals(searchType) && searchType != null) {
			Field[] fields = searchType.getDeclaredFields();
			for (Field field : fields) {
				allFieldNames.add(field.getName());
			}
			searchType = searchType.getSuperclass();
		}
		return allFieldNames;
	}
}
