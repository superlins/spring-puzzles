package org.example.mapstruct.entity;

import lombok.Data;
import org.example.mapstruct.vo.Address;

/**
 * @author renc
 */
@Data
public class User {

    private Long id;

    private String name;

    private String sex;

    private Integer age;

    private String interest;

    private Address address;
}
