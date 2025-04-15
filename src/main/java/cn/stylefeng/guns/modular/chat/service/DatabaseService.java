package cn.stylefeng.guns.modular.chat.service;

import java.util.List;
import java.util.Map;

public interface DatabaseService {
    
    /**
     * 执行SQL查询
     * @param sql SQL查询语句
     * @return 查询结果列表
     */
    List<Map<String, Object>> executeQuery(String sql);
    
    /**
     * 执行SQL更新
     * @param sql SQL更新语句
     * @return 影响的行数
     */
    int executeUpdate(String sql);
    
    /**
     * 获取数据库表结构
     * @param tableName 表名
     * @return 表结构信息
     */
    Map<String, String> getTableStructure(String tableName);
    
    /**
     * 获取数据库表列表
     * @return 表名列表
     */
    List<String> getTableList();
    
    /**
     * 验证SQL语句
     * @param sql SQL语句
     * @return 验证结果
     */
    boolean validateSql(String sql);
} 