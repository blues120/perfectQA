package cn.stylefeng.guns.core.consts;

import cn.stylefeng.guns.GunsMainProjectApplication;

/**
 * 项目的常量
 *
 * @author fengshuonan
 * @since 2022-11-01 11:52:44
 */
public interface ProjectConstants {

    /**
     * 项目的模块名称
     */
    String PROJECT_MODULE_NAME = "guns-standalone";

    /**
     * 异常枚举的步进值
     */
    String BUSINESS_EXCEPTION_STEP_CODE = "100";

    /**
     * 项目的包名，例如cn.stylefeng.guns
     */
    String ROOT_PACKAGE_NAME = GunsMainProjectApplication.class.getPackage().getName();

}
