package com.bblackbird.violation;

public class ErrorCodeTypeDescription {

    public String Enabled;
    public String Name;
    public String Message;
    public String Severity;
    public String Source;
    public String Type;
    public String Exception;

/*    public ErrorCodeTypeDescription(String enabled, String name, String message, String severity, String source, String type, String exception) {
        Enabled = enabled;
        Name = name;
        Message = message;
        Severity = severity;
        Source = source;
        Type = type;
        Exception = exception;
    }*/

    public String generateLine() {
        StringBuilder line = new StringBuilder();
        line.append(Name).append("(")
                .append(Message).append(",")
                .append(Severity).append(",")
                .append(Source).append(",")
                .append(Type).append(",")
                .append(Exception).append(")");

        return line.toString();
    }


    public String getEnabled() {
        return Enabled;
    }

    public void setEnabled(String enabled) {
        Enabled = enabled;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getSeverity() {
        return Severity;
    }

    public void setSeverity(String severity) {
        Severity = severity;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String source) {
        Source = source;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getException() {
        return Exception;
    }

    public void setException(String exception) {
        Exception = exception;
    }

    @Override
    public String toString() {
        return "ErrorCodeTypeDescription{" +
                "Enabled='" + Enabled + '\'' +
                ", Name='" + Name + '\'' +
                ", Message='" + Message + '\'' +
                ", Severity='" + Severity + '\'' +
                ", Source='" + Source + '\'' +
                ", Type='" + Type + '\'' +
                ", Exception='" + Exception + '\'' +
                '}';
    }

    public boolean signalIfEnabled() {
        return "ON".equalsIgnoreCase(Enabled);
    }
}
