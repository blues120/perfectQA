package cn.stylefeng.guns.modular.wolfram.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.stylefeng.guns.modular.wolfram.entity.WolframChat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Wolfram Alpha聊天记录Mapper接口
 */
@Mapper
public interface WolframChatMapper extends BaseMapper<WolframChat> {

    /**
     * 根据用户ID查询聊天记录
     *
     * @param userId 用户ID
     * @return 聊天记录列表
     */
    @Select("SELECT * FROM wolfram_chats WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<WolframChat> findByUserId(@Param("userId") String userId);
}
