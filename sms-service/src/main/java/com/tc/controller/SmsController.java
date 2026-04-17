package com.tc.controller;

import com.tc.excepion.ShopException;
import com.tc.result.ResponseEnum;
import com.tc.service.SmsService;
import com.tc.util.RandomUtil;
import com.tc.util.RegexValidateUtil;
import com.tc.util.ResultVOUtil;
import com.tc.vo.ResultVO;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/sms")
public class SmsController {
    @Resource
    private SmsService smsService;
    @Resource
    private RedisTemplate redisTemplate;

    @GetMapping("/send/{mobile}")
    public ResultVO sendSms(@PathVariable("mobile") String mobile) {
        if(mobile == null) throw new ShopException(ResponseEnum.MOBILE_NULL.getMsg());
        boolean flag = RegexValidateUtil.checkMobile(mobile);
        if(!flag) throw new ShopException(ResponseEnum.MOBILE_ERROR.getMsg());
        String code = RandomUtil.getSixBitRandom();
        boolean flag2 = smsService.sendSms(mobile, code);
        if(flag2){
            this.redisTemplate.opsForValue().set("uushop-sms-code-"+mobile, code,5, TimeUnit.MINUTES);
            return ResultVOUtil.success("短信发送成功！");
        }

        return ResultVOUtil.fail("短信发送失败！");
    }
}
