package org.mapstruct.extensions.spring.converter;

import javax.annotation.Generated;
import org.example.mapstruct.dto.UserRequest;
import org.example.mapstruct.entity.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.extensions.spring.converter.ConversionServiceAdapterGenerator",
    date = "2022-06-24T05:37:28.717Z"
)
@Component
public class ConversionServiceAdapter {
  private final ConversionService conversionService;

  public ConversionServiceAdapter(@Lazy final ConversionService conversionService) {
    this.conversionService = conversionService;
  }

  public User mapUserRequestToUser(final UserRequest source) {
    return conversionService.convert(source, User.class);
  }
}
