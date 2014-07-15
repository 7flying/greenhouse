package com.sevenflying.greenhouseclient.domain;

public enum AlertType {

	GREATER, LESS, EQUAL, GREATER_EQUAL, LESS_EQUAL;


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

}
