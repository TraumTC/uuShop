package com.tc.service.impl;

import com.tc.excepion.ShopException;
import com.tc.result.ResponseEnum;
import com.tc.service.SmsService;
import com.tc.util.SmsSendUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {
    @Resource
    private SmsSendUtil smsSendUtil;
    
    @Override
    public boolean sendSms(String mobile, String code) {
        try {
            boolean send =smsSendUtil.sendCode(mobile, code);
            if (send) return true;
        } catch (Exception e) {
            throw new ShopException(ResponseEnum.SMS_SEND_ERROR.getMsg());
        }
        return false;
    }
}
