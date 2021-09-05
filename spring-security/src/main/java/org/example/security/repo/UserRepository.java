package org.example.security.repo;

import org.example.security.constant.UserRole;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author renc
 */
@Repository
public class UserRepository {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    private final Map<String, User> userMap = new HashMap<String, User>() {
        {
            put("admin", new User("admin", passwordEncoder.encode("admin"), UserRole.ADMIN.getGrantedAuthorities()));
            put("student", new User("student", passwordEncoder.encode("123456"), UserRole.STUDENT.getGrantedAuthorities()));
        }
    };

    public UserDetails loadUserByUsername(String username) {
        return userMap.get(username);
    }

    public Collection<User> findAll() {
        return userMap.values();
    }
}
