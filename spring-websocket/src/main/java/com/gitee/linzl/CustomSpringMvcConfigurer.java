package com.gitee.linzl;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Objects;

@Configuration
public class CustomSpringMvcConfigurer {

    @Resource
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @PostConstruct
    public void addConversionConfig() {
        ConfigurableWebBindingInitializer initializer = (ConfigurableWebBindingInitializer)
                requestMappingHandlerAdapter.getWebBindingInitializer();
        if (Objects.nonNull(initializer) && Objects.nonNull(initializer.getConversionService())) {
            GenericConversionService genericConversionService =
                    (GenericConversionService) initializer.getConversionService();
            //genericConversionService.addConverter(new StringToDateConverter());
        }
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder
                .serializerByType(Long.class, ToStringSerializer.instance)
                .serializerByType(Long.TYPE, ToStringSerializer.instance);
    }
}