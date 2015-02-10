package com.sevenflying.server.domain;

public enum CompareType {
	
	GREATER_EQUAL, GREATER, EQUAL, LESS, LESS_EQUAL;
	
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
