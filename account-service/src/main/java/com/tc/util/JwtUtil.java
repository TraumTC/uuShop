package com.tc.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {

    private static long tokenExpiration = 1000 * 60 * 60 * 24;
//            private static long tokenExpiration = 1000 * 30;
    private static String tokenSignKey = "d41d8cd98f00b204e9800998ecf8427e_d41d8cd98f00b204e9800998ecf8427e";

    public static String createToken(Integer id,String mobile){
        var key = Keys.hmacShaKeyFor(tokenSignKey.getBytes(StandardCharsets.UTF_8));

        String token = Jwts.builder()
                // 完全保留原自定义载荷
                .claim("id", id)
                .claim("mobile", mobile)
                // 完全保留原默认载荷（方法名简化，功能不变）
                .subject("uushop-user") // 原setSubject
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration)) // 原setExpiration
                .id(UUID.randomUUID().toString()) // 原setId
                // 新版签名方式：算法改为Jwts.SIG.HS256，密钥通过Keys生成（功能完全等价于原HS256+字符串密钥）
                .signWith(key, Jwts.SIG.HS256)
                // 完全保留原组装逻辑
                .compact();
        return token;
    }

    public static boolean checkToken(String token){

        if (!StringUtils.hasText(token)) { // 等价于原StringUtils.isEmpty，Spring 5.3+推荐写法
            return false;
        }
        try {
            // 新版解析器构建：必须build()，verifyWith替代setSigningKey，parseSignedClaims替代parseClaimsJws
            var key = Keys.hmacShaKeyFor(tokenSignKey.getBytes(StandardCharsets.UTF_8));
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            // 解析成功即校验通过，完全保留原逻辑
        } catch (Exception e) {
            // 任何异常（过期、签名错误、格式错误）都返回false，完全保留原逻辑
            return false;
        }
        return true;
    }
}
