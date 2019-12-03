package com.sg.base.model;

import com.sg.base.bean.BeanFactory;
import com.sg.base.log.Logger;
import com.sg.base.model.annotation.Jsonable;
import com.sg.base.model.support.BaseModel;
import com.sg.base.model.support.Sort;
import com.sg.base.util.Converter;
import com.sg.base.util.Validator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.lang.reflect.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author lpw
 */
public class ModelHelper {

    /**
     * 将Model转化为JSON格式的数据。转化时将调用所有get方法输出属性值。
     *
     * @param model Model实例。
     * @return JSON数据。
     */
    public static <T extends Model> JSONObject toJson(T model) {
        if (model == null)
            return null;

        JSONObject object = new JSONObject();
        Method[] methods = model.getClass().getMethods();
        for (Method method : methods) {
            String name = method.getName();
            if (name.length() < 3)
                continue;

            if (name.startsWith("get")) {
                String propertyName = name.substring(3);
                Jsonable jsonable = method.getAnnotation(Jsonable.class);
                if (jsonable == null)
                    continue;

                try {
                    object.put(propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1), getJson(method.invoke(model), jsonable));
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    Logger.warn(e, "获取Model[{}]属性[{}]值时发生异常！", model, name);
                }

            }
        }
        return object;
    }

    @SuppressWarnings("unchecked")
    protected static <T extends Model> Object getJson(Object value, Jsonable jsonable) {
        if (value == null)
            return null;

        if (value instanceof Model)
            return toJson((T) value);

        if (value instanceof Collection) {
            if (Validator.isEmpty(value))
                return null;

            JSONArray array = new JSONArray();
            for (Object object : (Collection<?>) value)
                array.add(getJson(object, jsonable));

            return array;
        }

        if (value instanceof Map) {
            if (Validator.isEmpty(value))
                return null;
            JSONObject jsonObject = new JSONObject();
            for (Object key : ((Map) value).keySet()) {
                Object v = ((Map) value).get(key);
                jsonObject.put(key.toString(), getJson(v, jsonable));
            }
            return jsonObject;

        }

        String format = jsonable.format();
        if (!Validator.isEmpty(format)) {
            if (format.startsWith("number.")) {
                int[] ns = Converter.toInts(format.substring(7));
                return Converter.toString(value, ns[0], ns[1]);
            }
        }

        return Converter.toString(value);
    }

    @SuppressWarnings("unchecked")
    protected static <T extends Model> Class<T> getModelClass(Class<T> modelClass) {
        if (modelClass.getName().endsWith("Model"))
            return modelClass;

        return getModelClass((Class<T>) modelClass.getSuperclass());
    }

    /**
     * 将JSON对象转化为Model对象。
     *
     * @param json       JSON对象。
     * @param modelClass Model类。
     * @return Model对象；如果转化失败则返回null。
     */
    public static <T extends Model> T fromJson(JSONObject json, Class<T> modelClass) {
        if (json == null || modelClass == null)
            return null;

        // ModelTable modelTable = modelTables.get(modelClass);
        T model = BeanFactory.getBean(modelClass);
        if (json.has("id"))
            model.setId(json.getString("id"));
        Map<String, Class> m = new HashMap<>();
        for (Field f : model.getClass().getDeclaredFields()) {
            Class fieldClazz = f.getType(); // 得到field的class及类型全路径

            if (fieldClazz.isPrimitive())
                continue;
            if (fieldClazz.getName().startsWith("java.lang"))
                continue; // getName()返回field的类型全路径；
            if (fieldClazz.isAssignableFrom(List.class)) // 【2】
            {
                Type fc = f.getGenericType(); // 关键的地方，如果是List类型，得到其Generic的类型
                if (fc == null)
                    continue;
                if (fc instanceof ParameterizedType) // 【3】如果是泛型参数的类型
                {
                    ParameterizedType pt = (ParameterizedType) fc;
                    Class genericClazz = (Class) pt.getActualTypeArguments()[0]; // 【4】
                    // 得到泛型里的class类型对象。
                    m.put(f.getName(), genericClazz);
                }
            }
        }

        return (T) JSONObject.toBean(json, modelClass, m);
    }

    /**
     * 获取初始化MODEL
     *
     * @return
     */
    public static BaseModel getBaseModel() {
        BaseModel baseModel = BeanFactory.getBean(BaseModel.class);
        if (baseModel instanceof Sort) {
            Sort sort = Sort.class.cast(baseModel);
            if (!Validator.isEmpty(sort))
                sort.setSort(0);
        }
        baseModel.setCreateTime(new Date());
        baseModel.setModifyTime(new Date());
        baseModel.setRemark("");
        baseModel.setDomain("0");
        baseModel.setValidFlag(0);
        //baseModel.setSort(0);
        return baseModel;
    }

    private static String getFirstUppercase(String normal) {
        return normal.toString().substring(0, 1).toUpperCase() + normal.toString().substring(1);
    }

    private static <T extends Model> Class<Model> getJsonClass(T model, String key) {

        String upperCase = getFirstUppercase(key);
        try {
            return (Class<Model>) model.getClass().getMethod("get" + upperCase).getReturnType();
        } catch (NoSuchMethodException e) {
            Logger.warn(e, "设置Model[{}]属性[{}]值时发生异常！", model.getClass(), key);
        }
        return null;
    }

    private static <T extends Model> void setValue(T model, String key, Object value) {
        try {
            String upperCase = getFirstUppercase(key);
            model.getClass().getDeclaredMethod("set" + upperCase, model.getClass().getDeclaredField(key.toString()).getType()).invoke(model,
                    convert(model.getClass().getDeclaredField(key.toString()).getType(), value));
        } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Logger.warn(e, "设置Model[{}]属性[{}]值时发生异常！", model.getClass(), key);
        }
    }

    private static Object convert(Class<?> type, Object value) {
        if (value == null || type.isInstance(value))
            return value;

        if (String.class.equals(type))
            return Converter.toString(value);

        if (int.class.equals(type) || Integer.class.equals(type))
            return Converter.toInt(value);

        if (long.class.equals(type) || Long.class.equals(type))
            return Converter.toLong(value);

        if (float.class.equals(type) || Float.class.equals(type))
            return Converter.toFloat(value);

        if (double.class.equals(type) || Double.class.equals(type))
            return Converter.toDouble(value);

        if (java.sql.Date.class.equals(type) || Timestamp.class.equals(type)) {
            Date date = Converter.toDate(value);
            if (date == null)
                return null;

            if (java.sql.Date.class.equals(type))
                return new java.sql.Date(date.getTime());

            return new Timestamp(date.getTime());
        }

        return value;
    }

    public static Object format(String format, Object value) {
        if (Validator.isEmpty(format))
            return value;

        if (format.startsWith("number.")) {
            int[] ns = Converter.toInts(format.substring(7));
            String v = (String) value;
            StringBuilder sb = new StringBuilder().append(v);
            int indexOf = v.indexOf('.');
            int point = indexOf == -1 ? 0 : (v.length() - indexOf - 1);
            for (int i = point; i < ns[0]; i++)
                sb.append('0');
            if (indexOf > -1) {
                if (point > ns[0])
                    sb.delete(indexOf + ns[0] + 1, sb.length());
                sb.deleteCharAt(indexOf);
            }

            return sb.toString();
        }

        return value;
    }

}
