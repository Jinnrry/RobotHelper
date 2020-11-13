package com.zhenxi.Superappium.xpath.exception;


public class XpathSyntaxErrorException extends Exception {
    private int errorPos;


    public XpathSyntaxErrorException(int errorPos, Throwable cause) {
        super(cause);
        this.errorPos = errorPos;
    }

    public XpathSyntaxErrorException(int errorPos, String msg) {
        super(msg);
        this.errorPos = errorPos;
    }

    public XpathSyntaxErrorException(int errorPos, String message, Throwable cause) {
        super(message, cause);
        this.errorPos = errorPos;
    }
// /Users/alienhe/.gradle/caches/modules-2/files-2.1/com.virjar/ratel-extersion/1.0.4/c3247d9a6d2e125b04726c3b311ee721ef979ad2/ratel-extersion-1.0.4.jar
//    public XpathSyntaxErrorException(int errorPos, String message, Throwable cause, boolean enableSuppression,
//                                     boolean writableStackTrace) {
//        super(message, cause, enableSuppression, writableStackTrace);
//        this.errorPos = errorPos;
//    }
}
