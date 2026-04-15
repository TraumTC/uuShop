package com.tc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tc.entity.User;
import com.tc.excepion.ShopException;
import com.tc.form.UserRegisterForm;
import com.tc.result.ResponseEnum;
import com.tc.service.UserService;
import com.tc.util.RegexValidateUtil;
import com.tc.util.ResultVOUtil;
import com.tc.vo.ResultVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;


    @PostMapping("/register")
    public ResultVO register(@RequestBody UserRegisterForm Form){
        String mobile = Form.getMobile();
        boolean chackMobile = RegexValidateUtil.checkMobile(mobile);
        if(!chackMobile) throw new ShopException(ResponseEnum.MOBILE_ERROR.getMsg());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile",mobile);
        User user = this.userService.getOne(queryWrapper);
        if(user!=null) throw new ShopException(ResponseEnum.MOBILE_EXIST.getMsg());
        User newUser = new User();
        newUser.setMobile(mobile);
        newUser.setPassword(Form.getPassword());

        return ResultVOUtil.success(null);
    }


}
