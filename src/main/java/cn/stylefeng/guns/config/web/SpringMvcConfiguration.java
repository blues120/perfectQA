package cn.stylefeng.guns.config.web;

import cn.stylefeng.guns.core.error.CustomErrorAttributes;
import cn.stylefeng.guns.core.security.AuthJwtTokenSecurityInterceptor;
import cn.stylefeng.guns.core.security.PermissionSecurityInterceptor;
import cn.stylefeng.roses.kernel.security.cors.CorsFilterConfiguration;
import cn.stylefeng.roses.kernel.wrapper.field.jackson.CustomJacksonIntrospector;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * spring mvc的配置
 *
 * @author fengshuonan
 * @since 2022-11-01 11:52:44
 */
@Configuration
@Import({cn.hutool.extra.spring.SpringUtil.class, CorsFilterConfiguration.class})
public class SpringMvcConfiguration implements WebMvcConfigurer {

    @Resource
    private AuthJwtTokenSecurityInterceptor authJwtTokenSecurityInterceptor;

    @Resource
    private PermissionSecurityInterceptor permissionSecurityInterceptor;

    /**
     * 重写系统的默认错误提示
     *
     * @author fengshuonan
     * @since 2022-11-01 11:52:44
     */
    @Bean
    public CustomErrorAttributes gunsErrorAttributes() {
        return new CustomErrorAttributes();
    }

    /**
     * json自定义序列化工具,long转string
     *
     * @author fengshuonan
     * @since 2022-11-01 11:52:44
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> {
            jacksonObjectMapperBuilder.serializerByType(Long.class, ToStringSerializer.instance).serializerByType(Long.TYPE, ToStringSerializer.instance);
            jacksonObjectMapperBuilder.annotationIntrospector(new CustomJacksonIntrospector());
        };
    }

    /**
     * 配置项目拦截器
     *
     * @author fengshuonan
     * @since 2022-11-01 11:52:44
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authJwtTokenSecurityInterceptor);
        registry.addInterceptor(permissionSecurityInterceptor);
    }

    /**
     * 静态资源映射
     *
     * @author fengshuonan
     * @since 2022-11-01 11:52:44
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/assets/");
        registry.addResourceHandler("/guns-devops/**").addResourceLocations("classpath:/guns-devops/");

        // 流程设计器
        registry.addResourceHandler("/designer/**").addResourceLocations("classpath:/designer/");
    }

}
