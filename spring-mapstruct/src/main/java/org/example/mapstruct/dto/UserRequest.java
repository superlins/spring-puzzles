package org.example.mapstruct.dto;

import lombok.Data;

/**
 * @author renc
 */
@Data
public class UserRequest {

    private String name;

    private String sex;

    private int age;

    private String interesting;

    private String province;

    private String city;

    private String area;

    private String street;
}
