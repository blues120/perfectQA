package cn.stylefeng.guns.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 配置mapper包扫描
 *
 * @author fengshuonan
 * @since 2022-11-01 11:52:44
 */
@Configuration
@MapperScan(basePackages = {"cn.stylefeng.**.mapper", "cn.stylefeng.guns.**.mapper"})
public class MapperScanConfiguration {

}
