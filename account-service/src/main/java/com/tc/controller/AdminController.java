package com.tc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tc.entity.Admin;
import com.tc.excepion.ShopException;
import com.tc.form.AdminLoginForm;
import com.tc.result.ResponseEnum;
import com.tc.service.AdminService;
import com.tc.util.JwtUtil;
import com.tc.util.ResultVOUtil;
import com.tc.vo.AdminLoginVO;
import com.tc.vo.ResultVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tc
 * @since 2026-04-14
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Resource
    private AdminService adminService;
    @GetMapping("/login")
    public ResultVO login(AdminLoginForm form){
        String username = form.getUsername();
        QueryWrapper<Admin>  queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        Admin admin = this.adminService.getOne(queryWrapper);
        if(admin == null) throw new ShopException(ResponseEnum.ADMIN_USERNAME_NOT_EXIST.getMsg());
        String password = form.getPassword();
        if(!admin.getPassword().equals(password)){
            throw new ShopException(ResponseEnum.ADMIN_PASSWORD_ERROR.getMsg());
        }
        AdminLoginVO adminLoginVO = new AdminLoginVO();
        adminLoginVO.setAdminId(admin.getAdminId());
        adminLoginVO.setUsername(username);
        adminLoginVO.setPassword(password);
        adminLoginVO.setImgUrl(admin.getImgUrl());
        adminLoginVO.setName(admin.getName());
        String token = JwtUtil.createToken(admin.getAdminId(),admin.getUsername());
        adminLoginVO.setToken(token);

        return ResultVOUtil.success(adminLoginVO);
    }

    @GetMapping("/checkToken")
    public ResultVO checkToken(HttpServletRequest request){
        String token = request.getHeader("token");
        boolean flag = JwtUtil.checkToken(token);
        if(!flag) throw new ShopException(ResponseEnum.TOKEN_ERROR.getMsg());
        return ResultVOUtil.success(null);
    }
}
