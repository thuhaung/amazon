package com.example.amazon.Config.Mapping;

import com.example.amazon.Model.Role;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class MappingConfig {

    public Converter<List<Role>, List<String>> convertRoles = new AbstractConverter<>() {
        @Override
        protected List<String> convert(List<Role> roles) {
        return roles.stream()
                .map(role -> role.getType())
                .collect(Collectors.toList());
        }
    };

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(convertRoles);

        return modelMapper;
    }
}
