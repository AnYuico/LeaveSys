package com.g5.mapper;

import com.g5.entity.LeaveMaterial;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 请假附件材料表 Mapper 接口
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
@Mapper
public interface LeaveMaterialMapper extends BaseMapper<LeaveMaterial> {


    LeaveMaterial selectByLeaveRequestId(Integer leaveRequestId);
}
