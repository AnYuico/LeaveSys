package com.g5.common;

public enum LeaveTypeEnum {
    SICK((byte)1, "病假"),
    PERSONAL((byte)2, "事假"),
    MARRIAGE((byte)3, "婚假"),
    MATERNITY((byte)4, "产假"),
    OTHER((byte)5, "其他");

    private final byte code;
    private final String desc;

    LeaveTypeEnum(byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public byte getCode() {
        return code;
    }

    public static String getDescByCode(byte code) {
        for (LeaveTypeEnum type : values()) {
            if (type.code == code) {
                return type.desc;
            }
        }
        return "未知类型";
    }
}
