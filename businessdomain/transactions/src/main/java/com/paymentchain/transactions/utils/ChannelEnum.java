package com.paymentchain.transactions.utils;

public enum ChannelEnum {
    WEB(01, "Web"),
    CAJERO(02, "Cajero"),
    OFICINA(03, "Oficina");

    private final int code;
    private final String description;

    ChannelEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ChannelEnum fromCode(int code) {
        for (ChannelEnum channelEnum : ChannelEnum.values()) {
            if (channelEnum.getCode() == code) {
                return channelEnum;
            }
        }
        return null;
    }
}
