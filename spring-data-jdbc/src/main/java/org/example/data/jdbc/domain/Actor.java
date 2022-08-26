package org.example.data.jdbc.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

/**
 * @author renc
 */
@Table("t_actor")
public class Actor {

    @Id
    private int id;

    private String firstName;

    private String lastName;

    private Detail detail;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Detail getDetail() {
        return detail;
    }

    public void setDetail(Detail detail) {
        this.detail = detail;
    }

    public static class Detail {
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

    @Override
    public String toString() {
        return "Actor{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", detail=" + detail +
                '}';
    }
}
