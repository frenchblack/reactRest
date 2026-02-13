package com.spring.react.mapper.manage;

import org.apache.ibatis.annotations.Mapper;

import com.spring.react.vo.manage.AdminActionLogVO;

@Mapper
public interface AdminLogMapper {
    int insertAdminActionLog(AdminActionLogVO log);
}
