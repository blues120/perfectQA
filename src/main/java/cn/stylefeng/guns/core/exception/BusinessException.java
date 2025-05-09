package cn.stylefeng.guns.core.exception;

import cn.stylefeng.roses.kernel.rule.exception.AbstractExceptionEnum;
import cn.stylefeng.roses.kernel.rule.exception.base.ServiceException;

import static cn.stylefeng.guns.core.consts.ProjectConstants.PROJECT_MODULE_NAME;

/**
 * 业务异常
 *
 * @author fengshuonan
 * @since 2022-11-01 11:52:44
 */
public class BusinessException extends ServiceException {

    public BusinessException(AbstractExceptionEnum exception, String userTip) {
        super(PROJECT_MODULE_NAME, exception.getErrorCode(), userTip);
    }

    public BusinessException(AbstractExceptionEnum exception) {
        super(PROJECT_MODULE_NAME, exception);
    }

}
