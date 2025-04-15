# 文档管理模块

本模块用于管理系统的文档，提供上传、存储、检索和处理功能。

## 重构说明

本模块是从原JPA实现迁移到MyBatis-Plus实现的结果，主要包含以下内容：

1. 实体类：使用MyBatis-Plus注解替代JPA注解
2. Mapper接口：定义数据访问方法
3. XML配置：实现SQL查询和更新操作
4. 服务接口和实现：提供业务逻辑功能
5. 控制器：提供HTTP接口
6. 迁移服务：支持从原JPA实现迁移数据到MyBatis-Plus实现

## 功能特点

- 支持文档上传和下载
- 支持文档内容提取和处理
- 支持按关键词、区域、部门等高级搜索
- 支持文档状态管理
- 支持逻辑删除

## 数据结构

文档实体类包含以下字段：

- id：主键ID
- fileName：文件名
- fileId：文件ID
- contentType：内容类型
- content：文档内容
- district：区域信息
- department：部门信息
- keywords：关键词
- createTime：创建时间
- updateTime：更新时间
- createUser：创建用户
- segmentCount：分段数量
- fileSize：文件大小
- filePath：文件路径
- status：状态
- deleted：逻辑删除字段

## 接口列表

### 分页获取文档列表
GET /api/documents/page

### 高级搜索
GET /api/documents/search

### 获取文档详情
GET /api/documents/{id}

### 获取文档内容
GET /api/documents/{id}/content

### 获取与关键词相关的文档
GET /api/documents/keyword

### 获取用户的文档
GET /api/documents/user/{createUser}

### 获取特定状态的文档
GET /api/documents/status/{status}

### 上传文档
POST /api/documents/upload

### 更新文档
PUT /api/documents/{id}

### 更新文档状态
PUT /api/documents/{id}/status

### 删除文档
DELETE /api/documents/{id} 