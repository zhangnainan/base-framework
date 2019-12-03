/**
 *
 */
package com.sg.base.util;

import com.sg.base.log.Logger;

import java.io.*;

/**
 * @author dwq
 */
public final class Serializer {

    /**
     * 将对象序列化为byte数据。
     *
     * @param object 要进行序列化的对象。
     * @return byte数据；如果序列化失败则返回null。
     */
    public static byte[] serialize(Object object) {
        if (object == null)
            return null;

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(output);
            objectOutput.writeObject(object);
            objectOutput.close();
            output.close();

            return output.toByteArray();
        } catch (IOException e) {
            Logger.warn(e, "序列化对象[{}]时发生异常！", object);

            return null;
        }
    }

    /**
     * 将byte数据反序列化为对象。
     *
     * @param bytes byte数据。
     * @return 对象；如果反序列化失败则返回null。
     */
    @SuppressWarnings("unchecked")
    public static <T> T unserialize(byte[] bytes) {
        if (Validator.isEmpty(bytes))
            return null;

        T object = null;
        try {
            ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes));
            object = (T) input.readObject();
            input.close();
        } catch (IOException | ClassNotFoundException e) {
            Logger.warn(e, "反序列化对象时发生异常！");
        }

        return object;
    }
}
