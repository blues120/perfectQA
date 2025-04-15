package cn.stylefeng.guns.modular.instruction.controller;

import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.guns.modular.instruction.entity.Instruction;
import cn.stylefeng.guns.modular.instruction.service.InstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 指令控制器
 */
@RestController
@RequestMapping("/api/instructions")
public class InstructionController {

    @Autowired
    private InstructionService instructionService;

    /**
     * 分页获取指令列表
     */
    @GetMapping("/page")
    public ResponseData<Page<Instruction>> page(
            @RequestParam(value = "current", defaultValue = "1") long current,
            @RequestParam(value = "size", defaultValue = "10") long size) {
        return new SuccessResponseData<>(instructionService.page(current, size));
    }

    /**
     * 高级搜索
     */
    @GetMapping("/search")
    public ResponseData<Page<Instruction>> search(
            @RequestParam(value = "current", defaultValue = "1") long current,
            @RequestParam(value = "size", defaultValue = "10") long size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "isSystem", required = false) Boolean isSystem,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return new SuccessResponseData<>(
                instructionService.searchInstructions(current, size, keyword, type, isSystem, startTime, endTime)
        );
    }

    /**
     * 获取指令详情
     */
    @GetMapping("/{id}")
    public ResponseData<Instruction> detail(@PathVariable("id") Long id) {
        return new SuccessResponseData<>(instructionService.getById(id));
    }

    /**
     * 获取系统指令
     */
    @GetMapping("/system")
    public ResponseData<List<Instruction>> findSystemInstructions() {
        return new SuccessResponseData<>(instructionService.findSystemInstructions());
    }

    /**
     * 根据类型查询指令
     */
    @GetMapping("/type/{type}")
    public ResponseData<List<Instruction>> findByType(@PathVariable("type") String type) {
        return new SuccessResponseData<>(instructionService.findByType(type));
    }

    /**
     * 获取用户的指令
     */
    @GetMapping("/user/{createUser}")
    public ResponseData<List<Instruction>> findByCreateUser(@PathVariable("createUser") String createUser) {
        return new SuccessResponseData<>(instructionService.findByCreateUser(createUser));
    }

    /**
     * 创建指令
     */
    @PostMapping
    public ResponseData<Instruction> create(@RequestBody Instruction instruction) {
        return new SuccessResponseData<>(instructionService.createInstruction(instruction));
    }

    /**
     * 更新指令
     */
    @PutMapping("/{id}")
    public ResponseData<Instruction> update(@PathVariable("id") Long id, @RequestBody Instruction instruction) {
        return new SuccessResponseData<>(instructionService.updateInstruction(id, instruction));
    }

    /**
     * 删除指令
     */
    @DeleteMapping("/{id}")
    public ResponseData<Boolean> remove(@PathVariable("id") Long id) {
        return new SuccessResponseData<>(instructionService.deleteInstruction(id));
    }
}
