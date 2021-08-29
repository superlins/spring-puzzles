package org.example.r2dbc.domain;

import org.example.r2dbc.constant.Gender;
import org.example.r2dbc.constant.Type;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author renc
 */
@Table
public class Person {

    @Id
    private final String id;
    private final String name;
    private final int age;
    private Gender gender;
    private Type type;

    public Person(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Gender getGender() {
        return gender;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", type=" + type +
                ", gender=" + gender +
                '}';
    }
}