# 指令管理模块

本模块用于管理系统的指令，提供创建、存储、检索和管理功能。

## 重构说明

本模块是从原JPA实现迁移到MyBatis-Plus实现的结果，主要包含以下内容：

1. 实体类：使用MyBatis-Plus注解替代JPA注解
2. Mapper接口：定义数据访问方法
3. XML配置：实现SQL查询和更新操作
4. 服务接口和实现：提供业务逻辑功能
5. 控制器：提供HTTP接口
6. 迁移服务：支持从原JPA实现迁移数据到MyBatis-Plus实现

## 功能特点

- 支持指令的创建、更新和删除
- 支持按名称、类型等条件搜索指令
- 支持系统指令和用户自定义指令的区分
- 支持指令排序
- 支持逻辑删除

## 数据结构

指令实体类包含以下字段：

- id：主键ID
- name：指令名称
- content：指令内容
- type：指令类型
- description：指令描述
- createTime：创建时间
- updateTime：更新时间
- createUser：创建用户
- isSystem：是否系统指令
- sortOrder：排序序号
- deleted：逻辑删除字段

## 接口列表

### 分页获取指令列表
GET /api/instructions/page

### 高级搜索
GET /api/instructions/search

### 获取指令详情
GET /api/instructions/{id}

### 获取系统指令
GET /api/instructions/system

### 根据类型查询指令
GET /api/instructions/type/{type}

### 获取用户的指令
GET /api/instructions/user/{createUser}

### 创建指令
POST /api/instructions

### 更新指令
PUT /api/instructions/{id}

### 删除指令
DELETE /api/instructions/{id} 