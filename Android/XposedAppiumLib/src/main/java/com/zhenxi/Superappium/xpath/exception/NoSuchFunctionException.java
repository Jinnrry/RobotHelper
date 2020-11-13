package com.zhenxi.Superappium.xpath.exception;

public class NoSuchFunctionException extends XpathSyntaxErrorException {
    public NoSuchFunctionException(int errorPos, String msg) {
        super(errorPos, msg);
    }
}
