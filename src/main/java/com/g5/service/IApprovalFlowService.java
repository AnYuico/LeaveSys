package com.g5.service;

import com.g5.entity.dto.ApprovalRequestDTO;
import com.g5.entity.ApprovalFlow;
import com.baomidou.mybatisplus.extension.service.IService;
import com.g5.entity.vo.ApprovalListVo;

import java.util.List;

/**
 * <p>
 * 审批流程表 服务类
 * </p>
 *
 * @author Tzp
 * @since 2025-06-09
 */
public interface IApprovalFlowService extends IService<ApprovalFlow> {

    boolean approve(ApprovalRequestDTO dto, Integer approverId, byte sequence);

    List<ApprovalListVo> getPendingList(Integer approverId, byte sequence);
}
