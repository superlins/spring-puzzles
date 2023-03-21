package org.example.data.jdbc.domain;

import java.util.List;

public class Detail {
    private Integer age;
    private String sex;
    private List<String> hobby;
    private List<String> score;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public List<String> getHobby() {
        return hobby;
    }

    public void setHobby(List<String> hobby) {
        this.hobby = hobby;
    }

    public List<String> getScore() {
        return score;
    }

    public void setScore(List<String> score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Detail{" +
                "age=" + age +
                ", sex='" + sex + '\'' +
                ", hobby=" + hobby +
                ", score=" + score +
                '}';
    }
}