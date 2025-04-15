package cn.stylefeng.guns.modular.instruction.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.stylefeng.guns.modular.instruction.entity.Instruction;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 指令服务接口
 */
public interface InstructionService extends IService<Instruction> {

    /**
     * 分页查询指令
     *
     * @param current 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    Page<Instruction> page(long current, long size);

    /**
     * 高级搜索
     *
     * @param current 当前页
     * @param size 每页大小
     * @param keyword 关键词
     * @param type 类型
     * @param isSystem 是否系统指令
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    Page<Instruction> searchInstructions(
            long current,
            long size,
            String keyword,
            String type,
            Boolean isSystem,
            LocalDateTime startTime,
            LocalDateTime endTime
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
    List<Instruction> findByType(String type);

    /**
     * 根据创建用户查询指令
     *
     * @param createUser 创建用户
     * @return 指令列表
     */
    List<Instruction> findByCreateUser(String createUser);

    /**
     * 创建指令
     *
     * @param instruction 指令信息
     * @return 创建后的指令
     */
    Instruction createInstruction(Instruction instruction);

    /**
     * 更新指令
     *
     * @param id 指令ID
     * @param instruction 更新信息
     * @return 更新后的指令
     */
    Instruction updateInstruction(Long id, Instruction instruction);

    /**
     * 删除指令
     *
     * @param id 指令ID
     * @return 是否成功
     */
    boolean deleteInstruction(Long id);
}
