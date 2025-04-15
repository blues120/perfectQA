package cn.stylefeng.guns.modular.deepseek.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import cn.stylefeng.guns.modular.deepseek.entity.ShenPiOrder;
import cn.stylefeng.guns.modular.deepseek.mapper.ShenpiOrderMapper;
import cn.stylefeng.roses.kernel.auth.api.context.LoginContext;
import cn.stylefeng.roses.kernel.auth.api.pojo.login.LoginUser;
import cn.stylefeng.roses.kernel.db.api.factory.PageResultFactory;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;

/**
 * @author ☪wl
 * @date 2025/3/11 16:47
 */
@RestController
@ApiResource(name = "审批")
public class ShenPiController {

    @Resource
    private ShenpiOrderMapper shenpiOrderMapper;

    @PostResource(name = "保存审批工单", path = "/shenPiOrder/save", requiredPermission = false)
    public ResponseData<ShenPiOrder> saveOrder(@RequestBody ShenPiOrder shenPiOrder) {
        LoginUser loginUser = LoginContext.me().getLoginUser();
        shenPiOrder.setId(nextPkId());
        shenPiOrder.setCreateTime(new Date());
        shenPiOrder.setCreateUserId(shenPiOrder.getCreateUserId() == null ? loginUser.getUserId() : shenPiOrder.getCreateUserId());
        shenPiOrder.setCreateUserName(StringUtils.isNullOrEmpty(shenPiOrder.getCreateUserName()) ? loginUser.getSimpleUserInfo().getRealName() : shenPiOrder.getCreateUserName());
        this.shenpiOrderMapper.insert(shenPiOrder);
        return new SuccessResponseData<>(shenPiOrder);
    }

    public static String nextPkId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
        String number = sdf.format(System.currentTimeMillis());//202211041552160219
        //补充一个三位随机数
        int x = (int) (Math.random() * 900) + 100;
        return "SP" + number + x;
    }

    @GetResource(name = "审批工单列表", path = "/shenPiOrder/list", requiredPermission = false)
    public ResponseData<List<ShenPiOrder>> listOrder(ShenPiOrder shenPiOrder) {
        QueryWrapper<ShenPiOrder> wrapper = new QueryWrapper<>();
        wrapper.select(ShenPiOrder.class, order -> !order.getColumn().equals("shen_pi_result"));
        if (!StringUtils.isNullOrEmpty(shenPiOrder.getCreateUserName())) {
            wrapper.eq("create_user_name", shenPiOrder.getCreateUserName());
        }
        if (!StringUtils.isNullOrEmpty(shenPiOrder.getCreateTimeStart())) {
            wrapper.ge("create_time", shenPiOrder.getCreateTimeStart());
        }
        if (!StringUtils.isNullOrEmpty(shenPiOrder.getCreateTimeEnd())) {
            wrapper.le("create_time", shenPiOrder.getCreateTimeEnd());
        }
        List<ShenPiOrder> shenPiOrders = shenpiOrderMapper.selectList(null);
        return new SuccessResponseData<>(shenPiOrders);
    }

    @GetResource(name = "审批工单分页", path = "/shenPiOrder/page", requiredPermission = false)
    public ResponseData<PageResult<ShenPiOrder>> pageOrder(ShenPiOrder shenPiOrder, Integer pageNum, Integer pageSize) {
        QueryWrapper<ShenPiOrder> wrapper = new QueryWrapper<>();
        wrapper.select(ShenPiOrder.class, order -> !order.getColumn().equals("shen_pi_result"));
        if (!StringUtils.isNullOrEmpty(shenPiOrder.getCreateUserName())) {
            wrapper.like("create_user_name", shenPiOrder.getCreateUserName());
        }
        if (!StringUtils.isNullOrEmpty(shenPiOrder.getCreateTimeStart())) {
            wrapper.ge("create_time", shenPiOrder.getCreateTimeStart());
        }
        if (!StringUtils.isNullOrEmpty(shenPiOrder.getCreateTimeEnd())) {
            wrapper.le("create_time", shenPiOrder.getCreateTimeEnd());
        }
        Page<ShenPiOrder> page = this.shenpiOrderMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return new SuccessResponseData<>(PageResultFactory.createPageResult(page));
    }

    @GetResource(name = "审批工单明细", path = "/shenPiOrder/detail", requiredPermission = false)
    public ResponseData<ShenPiOrder> getOrderById(String id) {
        QueryWrapper<ShenPiOrder> wrapper = new QueryWrapper<>();
        wrapper.select("id,create_time,create_user_id,create_user_name,replace(shen_pi_result,'&quot;','\"') shen_pi_result");
        wrapper.eq("id", id);
        return new SuccessResponseData<>(shenpiOrderMapper.selectOne(wrapper));
    }

}
