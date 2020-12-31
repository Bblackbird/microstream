package com.bblackbird.spi;

public interface CommandContext {

    Object getCommandContextId();

    String getCommandContextName();

    String getClusterName();

    int getMachineId();

    String getMachineName();

    String getMemberName();

    int getPriority();

    String getProcessName();

    String getRackName();

    String getRoleName();

    String getSiteName();

}
