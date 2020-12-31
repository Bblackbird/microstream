package com.bblackbird.spi;

import java.util.EnumSet;

public enum ProcessingState {

    Ready, Processing, Skipped, Terminated, Completed, Failed, Succeded;

    public static final EnumSet<ProcessingState> ALL = EnumSet.allOf(ProcessingState.class);

    public static final EnumSet<ProcessingState> PROCESSED = EnumSet.of(Skipped, Terminated, Failed, Succeded);

    public boolean isProcessed() {
        return PROCESSED.contains(this);
    }

}
