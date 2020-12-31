package com.bblackbird.violation;

public enum FDTError {

    EMPTY("E", "EMPTY", "EMPTY"),
    ER_001_PRD_INACTIVE_ERR("ERR_001", "PRD_INACTIVE_ERR", "Product is not active or valid");

    private final String errorCode;
    private final String errorMessage;
    private final String errorDescription;
    private final String fullErrorDescription;

    FDTError(String errorCode, String errorMessage, String errorDescription) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDescription = errorDescription;
        fullErrorDescription = errorCode + "|" + errorMessage + "|" + errorDescription;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public String getFullErrorDescripion() {
        return fullErrorDescription;
    }
}
