
package com.huellasdulces.config;

import com.huellasdulces.mapper.ProductMapper;
import com.huellasdulces.mapper.ProductMapperImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ProductMapperConfig {

    @Bean
    @Primary
    public ProductMapper productMapper() {
        return new ProductMapperImpl();
    }
}
