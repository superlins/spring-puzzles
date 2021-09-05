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

    STUDENT(new HashSet<>(asList(UserPermission.STUDENT_SELECT))),

    ADMIN(new HashSet<>(asList(
            UserPermission.STUDENT_ADD,
            UserPermission.STUDENT_DELETE,
            UserPermission.STUDENT_UPDATE,
            UserPermission.STUDENT_SELECT)));

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
