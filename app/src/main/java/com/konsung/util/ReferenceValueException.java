package com.konsung.util;

/**
 * 参考值规范限制异常检测类
 */
public class ReferenceValueException extends Exception {
    /**
     * 抛出
     * @param detailMessage 异常信息
     */
    public ReferenceValueException(String detailMessage) {
        super(detailMessage);
    }
}
