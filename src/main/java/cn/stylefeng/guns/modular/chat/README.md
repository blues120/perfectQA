# 聊天历史记录模块

本模块用于管理系统的聊天历史记录，提供存储、检索、搜索和导出功能。

## 重构说明

本模块是从原JPA实现迁移到MyBatis-Plus实现的结果，主要包含以下内容：

1. 实体类：使用MyBatis-Plus注解替代JPA注解
2. Mapper接口：定义数据访问方法
3. XML配置：实现SQL查询和更新操作
4. 服务接口和实现：提供业务逻辑功能
5. 控制器：提供HTTP接口
6. 迁移服务：支持从原JPA实现迁移数据到MyBatis-Plus实现

## 功能特点

- 支持按会话ID或用户ID查询历史记录
- 支持按关键词、时间范围等高级搜索
- 支持导出聊天历史记录为文本文件
- 支持根据时间清理旧记录
- 支持获取用户常用问题
- 支持查找相关问题
- 支持区域和部门筛选

## 数据结构

聊天历史记录实体类包含以下字段：

- id：主键ID
- sessionId：会话ID
- userId：用户ID
- question：问题内容
- answer：回答内容
- createTime：创建时间
- district：区县信息
- department：部门/科室信息
- keywords：关键词
- deleted：逻辑删除字段

## 接口列表

### 创建聊天历史记录
POST /api/chat-history

### 获取会话的聊天历史记录
GET /api/chat-history/session/{sessionId}

### 获取会话的聊天历史记录（按时间升序）
GET /api/chat-history/session/{sessionId}/asc

### 获取用户的聊天历史记录
GET /api/chat-history/user/{userId}

### 搜索聊天历史记录
GET /api/chat-history/search

### 删除会话的聊天历史记录
DELETE /api/chat-history/session/{sessionId}

### 删除用户的聊天历史记录
DELETE /api/chat-history/user/{userId}

### 导出聊天历史记录
GET /api/chat-history/export/{sessionId}

### 获取用户最近的聊天记录
GET /api/chat-history/recent/{userId}

### 获取用户常用问题
GET /api/chat-history/frequent-questions/{userId}

### 清理旧聊天记录
DELETE /api/chat-history/cleanup

### 获取相关问题
GET /api/chat-history/related-questions 