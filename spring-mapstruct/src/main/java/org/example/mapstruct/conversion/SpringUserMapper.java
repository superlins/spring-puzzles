package org.example.mapstruct.conversion;

import org.example.mapstruct.dto.UserRequest;
import org.example.mapstruct.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.core.convert.converter.Converter;

/**
 * @author renc
 */
@Mapper(componentModel = "spring")
public interface SpringUserMapper extends Converter<UserRequest, User> {

    @Override
    @Mappings({
            @Mapping(target = "interest", source = "interesting"),
            @Mapping(target = "address.province", source = "province"),
            @Mapping(target = "address.city", source = "city"),
            @Mapping(target = "address.area", source = "area"),
            @Mapping(target = "address.street", source = "street"),
    })
    User convert(UserRequest source);
}
