package org.example.mapstruct.conversion;

import org.example.mapstruct.dto.UserRequest;
import org.example.mapstruct.entity.User;
import org.junit.jupiter.api.Test;

/**
 * @author renc
 */
class SimpleUserMapperTest {

    @Test
    void convert() {
        UserRequest userRequest = new UserRequest();
        userRequest.setName("张三");
        userRequest.setSex("M");
        userRequest.setAge(10);
        userRequest.setProvince("北京");
        userRequest.setCity("北京");
        userRequest.setArea("丰台");
        userRequest.setInteresting("游戏");
        userRequest.setStreet("淮坊街道");
        User user = SimpleUserMapper.INSTANCE.convert(userRequest);
        System.out.println(user);
    }
}