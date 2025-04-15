package cn.stylefeng.guns.modular.arxiv.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.stylefeng.guns.modular.arxiv.entity.ArxivChat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Arxiv论文聊天记录Mapper接口
 */
@Mapper
public interface ArxivChatMapper extends BaseMapper<ArxivChat> {

    /**
     * 根据用户ID查询聊天记录
     *
     * @param userId 用户ID
     * @return 聊天记录列表
     */
    @Select("SELECT * FROM arxiv_chats WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<ArxivChat> findByUserId(@Param("userId") String userId);
}
