package com.bblackbird.violation;

public enum FDTValidation {

    VAL_GPS("V", FDTError.EMPTY);


    private final String validationMessage;
    private final String fullErrorMessage;
    private final FDTError fosError;

    FDTValidation(String validationMessage, FDTError fosError) {
        this.validationMessage = validationMessage;
        this.fosError = fosError;
        StringBuilder full = new StringBuilder(name());
        full.append("|").append(fosError.getFullErrorDescripion());
        this.fullErrorMessage = full.toString();
    }

    public String getValidationMessage() {
        return validationMessage;
    }

    public String getFullErrorMessage() {
        return fullErrorMessage;
    }

    public FDTError getFosError() {
        return fosError;
    }
}
