package com.g5.common;

public enum StatusEnum {
    APPLYING((byte) 1, "申请中"),
    APPROVED((byte)2, "已批准"),
    REJECTED((byte)3, "已拒绝"),
    CANCELLED((byte)4, "已销假");

    private final byte code;
    private final String desc;

    StatusEnum(byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(byte code) {
        for (StatusEnum type : values()) {
            if (type.code == code) {
                return type.desc;
            }
        }
        return "未知类型";
    }
}
