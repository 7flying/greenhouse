package com.sevenflying.greenhouseclient.domain;

public enum AlertType {

	GREATER, GREATER_EQUAL, EQUAL, LESS, LESS_EQUAL; // Do not change the order

    public static final AlertType[] alertTypes = { AlertType.GREATER, AlertType.GREATER_EQUAL,
        AlertType.EQUAL, AlertType.LESS, AlertType.LESS_EQUAL };

    public String getSymbol() {
        switch (this) {
            case GREATER:
                return ">";
            case GREATER_EQUAL:
                return ">=";
            case EQUAL:
                return "=";
            case LESS:
                return "<";
            case LESS_EQUAL:
                return "<=";
            default:
                return "?";
        }
    }

    public int getIndex() {
        switch (this) {
            case GREATER:
                return 0;
            case GREATER_EQUAL:
                return 1;
            case EQUAL:
                return 2;
            case LESS:
                return 3;
            case LESS_EQUAL:
                return 4;
            default:
                return -1;
        }
    }

}
