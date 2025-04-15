package cn.stylefeng.guns.modular.instruction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.stylefeng.guns.modular.instruction.entity.Instruction;
import cn.stylefeng.guns.modular.instruction.mapper.InstructionMapper;
import cn.stylefeng.guns.modular.instruction.service.InstructionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 指令服务实现类
 */
@Service
public class InstructionServiceImpl extends ServiceImpl<InstructionMapper, Instruction> implements InstructionService {

    @Override
    public Page<Instruction> page(long current, long size) {
        Page<Instruction> page = new Page<>(current, size);
        LambdaQueryWrapper<Instruction> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Instruction::getSortOrder)
                  .orderByDesc(Instruction::getCreateTime);
        return this.page(page, queryWrapper);
    }

    @Override
    public Page<Instruction> searchInstructions(
            long current,
            long size,
            String keyword,
            String type,
            Boolean isSystem,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        Page<Instruction> page = new Page<>(current, size);
        return this.baseMapper.searchInstructions(page, keyword, type, isSystem, startTime, endTime);
    }

    @Override
    public List<Instruction> findSystemInstructions() {
        return this.baseMapper.findSystemInstructions();
    }

    @Override
    public List<Instruction> findByType(String type) {
        return this.baseMapper.findByType(type);
    }

    @Override
    public List<Instruction> findByCreateUser(String createUser) {
        return this.baseMapper.findByCreateUser(createUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Instruction createInstruction(Instruction instruction) {
        // 设置默认值
        if (instruction.getIsSystem() == null) {
            instruction.setIsSystem(false);
        }

        if (instruction.getSortOrder() == null) {
            instruction.setSortOrder(0);
        }

        instruction.setCreateTime(LocalDateTime.now());
        instruction.setUpdateTime(LocalDateTime.now());

        this.save(instruction);
        return instruction;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Instruction updateInstruction(Long id, Instruction updatedInstruction) {
        Instruction instruction = this.getById(id);
        if (instruction == null) {
            throw new RuntimeException("指令不存在: " + id);
        }

        // 更新字段
        if (updatedInstruction.getName() != null) {
            instruction.setName(updatedInstruction.getName());
        }

        if (updatedInstruction.getContent() != null) {
            instruction.setContent(updatedInstruction.getContent());
        }

        if (updatedInstruction.getType() != null) {
            instruction.setType(updatedInstruction.getType());
        }

        if (updatedInstruction.getDescription() != null) {
            instruction.setDescription(updatedInstruction.getDescription());
        }

        if (updatedInstruction.getIsSystem() != null) {
            instruction.setIsSystem(updatedInstruction.getIsSystem());
        }

        if (updatedInstruction.getSortOrder() != null) {
            instruction.setSortOrder(updatedInstruction.getSortOrder());
        }

        instruction.setUpdateTime(LocalDateTime.now());

        this.updateById(instruction);
        return instruction;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteInstruction(Long id) {
        return this.removeById(id);
    }
}
