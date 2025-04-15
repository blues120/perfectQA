package cn.stylefeng.guns;

import cn.stylefeng.roses.kernel.db.starter.GunsDataSourceAutoConfiguration;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * SpringBoot方式启动类
 *
 * @author fengshuonan
 * @since 2022-11-01 11:52:44
 */
@Slf4j
@SpringBootApplication(scanBasePackages = {"cn.stylefeng", "cn.stylefeng.guns"}, exclude = {MongoAutoConfiguration.class, FlywayAutoConfiguration.class, GunsDataSourceAutoConfiguration.class})
public class GunsMainProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(GunsMainProjectApplication.class, args);
        log.info(GunsMainProjectApplication.class.getSimpleName() + " is success!");
    }

}

