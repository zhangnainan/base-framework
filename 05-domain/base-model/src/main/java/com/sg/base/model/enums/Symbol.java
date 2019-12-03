package com.sg.base.model.enums;

/**
 * Symbol
 *
 * @author Dai Wenqing
 * @date 2016/4/23
 */
public enum Symbol {

    Plug("+"), Subtract("-"), Multi("*"), Div("/"), LeftParentheses("("), RightParentheses(")");

    private String type;

    Symbol(String type) {
        this.type = type;
    }
}
