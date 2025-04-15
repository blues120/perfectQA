package cn.stylefeng.guns.modular.instruction.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.stylefeng.guns.modular.instruction.entity.Instruction;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 指令Mapper接口
 */
public interface InstructionMapper extends BaseMapper<Instruction> {

    /**
     * 搜索指令
     *
     * @param page 分页参数
     * @param keyword 关键词
     * @param type 类型
     * @param isSystem 是否系统指令
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    Page<Instruction> searchInstructions(
            Page<Instruction> page,
            @Param("keyword") String keyword,
            @Param("type") String type,
            @Param("isSystem") Boolean isSystem,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 获取系统指令
     *
     * @return 系统指令列表
     */
    List<Instruction> findSystemInstructions();

    /**
     * 根据类型查询指令
     *
     * @param type 类型
     * @return 指令列表
     */
    List<Instruction> findByType(@Param("type") String type);

    /**
     * 根据创建用户查询指令
     *
     * @param createUser 创建用户
     * @return 指令列表
     */
    List<Instruction> findByCreateUser(@Param("createUser") String createUser);
}
