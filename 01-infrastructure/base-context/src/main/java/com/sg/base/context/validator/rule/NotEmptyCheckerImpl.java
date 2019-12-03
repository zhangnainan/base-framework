package com.sg.base.context.validator.rule;

import com.sg.base.context.validator.Checker;
import com.sg.base.util.Validator;
import org.springframework.stereotype.Controller;


/**
 * NotEmptyCheckerImpl
 *
 * @author Dai Wenqing
 * @date 2015/12/7
 */
@Controller("error.parameter.not.empty")
public class NotEmptyCheckerImpl implements Checker {
    @Override
    public boolean validate(String value, String[] paramters) {
        if (value == null || value.isEmpty())
            return false;
        return !Validator.isEmpty(value);
    }
}
