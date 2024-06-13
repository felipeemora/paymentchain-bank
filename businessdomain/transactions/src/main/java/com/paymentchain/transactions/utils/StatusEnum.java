package com.paymentchain.transactions.utils;

public enum StatusEnum {
    PENDIENTE(01, "Pendiente"),
    LIQUIDADA(02, "Liquidada"),
    RECHAZADA(03, "Rechazada"),
    CANCELADA(04, "Cancelada");

    private final int code;
    private final String description;

    StatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static StatusEnum fromCode(int code) {
        for (StatusEnum status : StatusEnum.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
