package com.tc.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tc.entity.User;
import com.tc.excepion.ShopException;
import com.tc.form.UserLoginForm;
import com.tc.form.UserRegisterForm;
import com.tc.result.ResponseEnum;
import com.tc.service.UserService;
import com.tc.util.JwtUtil;
import com.tc.util.MD5Util;
import com.tc.util.RegexValidateUtil;
import com.tc.util.ResultVOUtil;
import com.tc.vo.ResultVO;
import com.tc.vo.UserLoginVO;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

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
    @Resource
    private RedisTemplate redisTemplate;


    @PostMapping("/register")
    public ResultVO register(@RequestBody UserRegisterForm Form){
        String mobile = Form.getMobile();
        boolean chackMobile = RegexValidateUtil.checkMobile(mobile);
        if(!chackMobile) throw new ShopException(ResponseEnum.MOBILE_ERROR.getMsg());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile",mobile);
        User user = this.userService.getOne(queryWrapper);
        if(user!=null) throw new ShopException(ResponseEnum.MOBILE_EXIST.getMsg());
//        这里应该从redis中获取，但redis还没配置好，先直接用一个数据
//        String code = "66666";

        String code = (String) redisTemplate.opsForValue().get("uushop-sms-code-"+mobile);
        if(!code.equals(Form.getCode())) throw new ShopException(ResponseEnum.CODE_ERROR.getMsg());
        User newUser = new User();
        newUser.setMobile(mobile);
//        密码加密
        newUser.setPassword(MD5Util.getSaltMD5(Form.getPassword()));
        boolean flag = this.userService.save(newUser);
        if(!flag) throw new ShopException(ResponseEnum.USER_REGISTER_ERROR.getMsg());
        return ResultVOUtil.success(null);
    }

    @GetMapping("/login")
    public ResultVO login(UserLoginForm Form){
        String mobile = Form.getMobile();
        boolean chackMobile = RegexValidateUtil.checkMobile(mobile);
        if(!chackMobile) throw new ShopException(ResponseEnum.MOBILE_ERROR.getMsg());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("mobile",mobile);
        User user = this.userService.getOne(queryWrapper);
        if (user == null) throw new ShopException(ResponseEnum.USER_NOT_EXIST.getMsg());
        boolean flag = MD5Util.getSaltverifyMD5(Form.getPassword(),user.getPassword());
        if(!flag) throw new ShopException(ResponseEnum.PASSWORD_ERROR.getMsg());
        String token = JwtUtil.createToken(user.getUserId(),  user.getMobile());
        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setToken(token);
        userLoginVO.setUserId(user.getUserId());
        userLoginVO.setMobile(user.getMobile());
        userLoginVO.setPassword(user.getPassword());

        return ResultVOUtil.success(userLoginVO);
    }

    @GetMapping("/checkToken")
//    public ResultVO checkToken(HttpServletRequest request){
    public ResultVO checkToken(String token){
//        String token = request.getHeader("token");
        boolean flag = JwtUtil.checkToken(token);
        if(!flag) throw new ShopException(ResponseEnum.TOKEN_ERROR.getMsg());
        return ResultVOUtil.success(null);
    }

}
