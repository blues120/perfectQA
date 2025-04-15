package cn.stylefeng.guns.modular.database.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.stylefeng.guns.modular.database.entity.DatabaseChat;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据库聊天记录Mapper接口
 */
public interface DatabaseChatMapper extends BaseMapper<DatabaseChat> {

    /**
     * 根据用户ID查询聊天记录
     *
     * @param userId 用户ID
     * @return 聊天记录列表
     */
    List<DatabaseChat> findByUserId(@Param("userId") String userId);

    /**
     * 根据数据库类型查询聊天记录
     *
     * @param databaseType 数据库类型
     * @return 聊天记录列表
     */
    List<DatabaseChat> findByDatabaseType(@Param("databaseType") String databaseType);
}
