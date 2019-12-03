package com.sg.base.resource;

import com.sg.base.log.Logger;
import com.sg.base.util.Validator;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * @author lpw
 */
@Component("base.resource.io")
public class IoImpl implements Io {
    private static final int BUFFER_SIZE = 1024;

    @Override
    public String read(String path) {
        if (Validator.isEmpty(path))
            return null;

        try {
            if (Logger.isDebugEnable())
                Logger.debug("读取文本文件：{}", path);

            OutputStream output = new ByteArrayOutputStream();
            InputStream input = new FileInputStream(path);
            copy(input, output);
            input.close();
            output.close();

            return output.toString();
        } catch (IOException e) {
            Logger.warn(e, "读文本文件[{}]时异常！", path);

            return null;
        }
    }

    @Override
    public void write(String path, byte[] content) {
        if (Validator.isEmpty(path))
            return;

        try {
            if (Logger.isDebugEnable())
                Logger.debug("写入文件：{}", path);

            OutputStream output = new FileOutputStream(path);
            output.write(content);
            output.close();
        } catch (IOException e) {
            Logger.warn(e, "写入文件[{}]时异常！", path);
        }
    }

    @Override
    public void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        for (int length; (length = input.read(buffer)) > -1; )
            output.write(buffer, 0, length);
        output.flush();
    }

    @Override
    public void write(OutputStream output, StringBuffer source) throws IOException {
        if (source == null || source.length() == 0)
            return;

        Writer writer = new OutputStreamWriter(output);
        for (int i = 0, length = source.length(); i < length; i++)
            writer.append(source.charAt(i));
        writer.flush();
    }
}
