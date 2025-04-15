package cn.stylefeng.guns.modular.chat.service.impl;

import cn.stylefeng.guns.modular.chat.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;

@Service
public class DatabaseServiceImpl implements DatabaseService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(.*)(\\b)(SELECT|INSERT|UPDATE|DELETE|DROP|UNION|ALTER)(\\b)(.*)"
    );
    
    @Override
    public List<Map<String, Object>> executeQuery(String sql) {
        try {
            if (!validateSql(sql)) {
                throw new RuntimeException("SQL语句包含潜在的危险操作");
            }
            
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            throw new RuntimeException("执行SQL查询失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public int executeUpdate(String sql) {
        try {
            if (!validateSql(sql)) {
                throw new RuntimeException("SQL语句包含潜在的危险操作");
            }
            
            return jdbcTemplate.update(sql);
        } catch (Exception e) {
            throw new RuntimeException("执行SQL更新失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, String> getTableStructure(String tableName) {
        try {
            String sql = "SELECT column_name, data_type, character_maximum_length " +
                        "FROM information_schema.columns " +
                        "WHERE table_name = ? " +
                        "ORDER BY ordinal_position";
                        
            List<Map<String, Object>> columns = jdbcTemplate.queryForList(sql, tableName);
            
            Map<String, String> structure = new HashMap<>();
            for (Map<String, Object> column : columns) {
                String columnName = (String) column.get("column_name");
                String dataType = (String) column.get("data_type");
                Object maxLength = column.get("character_maximum_length");
                
                String typeInfo = dataType;
                if (maxLength != null) {
                    typeInfo += "(" + maxLength + ")";
                }
                
                structure.put(columnName, typeInfo);
            }
            
            return structure;
        } catch (Exception e) {
            throw new RuntimeException("获取表结构失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<String> getTableList() {
        try {
            String sql = "SELECT table_name FROM information_schema.tables " +
                        "WHERE table_schema = 'public' " +
                        "ORDER BY table_name";
                        
            return jdbcTemplate.queryForList(sql, String.class);
        } catch (Exception e) {
            throw new RuntimeException("获取表列表失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean validateSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }
        
        String normalizedSql = sql.trim().toLowerCase();
        
        if (SQL_INJECTION_PATTERN.matcher(normalizedSql).matches()) {
            return false;
        }
        
        if (!normalizedSql.startsWith("select")) {
            return false;
        }
        
        return true;
    }
} 