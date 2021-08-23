package org.example.security.constant;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * @author renc
 */
public enum UserRole {

    STUDENT(new HashSet<>(asList(
            UserPermission.COURSE_READ,
            UserPermission.STUDENT_READ,
            UserPermission.STUDENT_WRITE))),

    ADMIN_TRAINEE(new HashSet<>(asList(
            UserPermission.COURSE_READ,
            UserPermission.STUDENT_READ))),

    ADMIN(new HashSet<>(asList(
            UserPermission.COURSE_READ,
            UserPermission.COURSE_WRITE,
            UserPermission.STUDENT_READ,
            UserPermission.STUDENT_WRITE)));

    private Set<UserPermission> permissions;

    UserRole(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<UserPermission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }
}
