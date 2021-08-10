package main.model.util;

public enum Permission {

    READ("user:read"),
    WRITE("user:write"),
    MODERATE("user:moderate");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
