package com.sg.base.context.validator.rule;

import com.sg.base.context.validator.Checker;
import com.sg.base.util.Converter;
import org.springframework.stereotype.Controller;

/**
 * NotBetweenCheckerImpl
 *
 * @author Dai Wenqing
 * @date 2015/12/7
 */
@Controller("error.parameter.not.between")
public class NotBetweenCheckerImpl implements Checker {
    @Override
    public boolean validate(String value, String[] paramters) {
        if (value == null || value.isEmpty())
            return false;
        if (paramters.length < 2)
            return false;
        double n = Converter.toDouble(value);
        return n >= Converter.toDouble(paramters[0]) && n <= Converter.toDouble(paramters[1]);
    }
}
