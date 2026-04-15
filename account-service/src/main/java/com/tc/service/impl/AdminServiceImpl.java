package com.tc.service.impl;

import com.tc.entity.Admin;
import com.tc.mapper.AdminMapper;
import com.tc.service.AdminService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tc
 * @since 2026-04-14
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

}
