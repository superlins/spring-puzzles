package org.example.security.constant;

import org.springframework.http.HttpMethod;

/**
 * @author renc
 */
public enum UserPermission {

    STUDENT_ADD("/api/v1/student", HttpMethod.POST.name(), "student:add"),

    STUDENT_UPDATE("/api/v1/student", HttpMethod.PUT.name(), "student:update"),

    STUDENT_DELETE("/api/v1/student", HttpMethod.DELETE.name(), "student:delete"),

    STUDENT_SELECT("/api/v1/student/*", HttpMethod.GET.name(), "student:select"),
    ;

    private final String uri;
    private final String method;
    private final String permission;

    UserPermission(String uri, String method, String permission) {
        this.uri = uri;
        this.method = method;
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    public String getUri() {
        return uri;
    }

    public String getMethod() {
        return method;
    }
}
