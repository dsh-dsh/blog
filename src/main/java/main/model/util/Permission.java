package main.model.util;

public enum Permission {

    WRITE("write"),
    MODERATE("moderate");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
