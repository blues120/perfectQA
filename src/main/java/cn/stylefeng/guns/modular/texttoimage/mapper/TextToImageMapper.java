package cn.stylefeng.guns.modular.texttoimage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.stylefeng.guns.modular.texttoimage.entity.TextToImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文本生成图片Mapper接口
 */
@Mapper
public interface TextToImageMapper extends BaseMapper<TextToImage> {

    /**
     * 根据用户ID查询图片记录
     *
     * @param userId 用户ID
     * @return 图片记录列表
     */
    @Select("SELECT * FROM text_to_images WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<TextToImage> findByUserId(@Param("userId") String userId);
}
