package cn.stylefeng.guns;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Web程序启动类
 *
 * @author fengshuonan
 * @since 2022-11-01 11:52:44
 */
public class GunsMainProjectServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(GunsMainProjectApplication.class);
    }

}
