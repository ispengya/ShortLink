package com.ispengya.shortlink.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class JwtUtils {

    /**
     * token秘钥，请勿泄露，请勿随便修改
     */
    private static String secret="sdjghdfijijkmgklmdfg";

    private static final String USER_NAME_CLAIM = "username";
    private static final String CREATE_TIME = "createTime";

    /**
     * JWT生成Token.<br/>
     * <p>
     * JWT构成: header, payload, signature
     */
    public static String createToken(String username) {
        // build token
        String token = JWT.create()
                .withClaim(USER_NAME_CLAIM, username) // 只存一个uid信息，其他的自己去redis查
                .withClaim(CREATE_TIME, new Date())
                .sign(Algorithm.HMAC256(secret)); // signature
        return token;
    }

    public static void main(String[] args) {
        JwtUtils jwtUtils = new JwtUtils();
        String token = jwtUtils.createToken("ispengya");
        System.out.println(token);
        System.out.println(jwtUtils.getUserName(token));
    }

    /**
     * 解密Token
     *
     * @param token
     * @return
     */
    public static Map<String, Claim> verifyToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaims();
        } catch (Exception e) {
            log.error("decode error,token:{}", token, e);
        }
        return null;
    }


    /**
     * 根据Token获取username
     *
     * @param token
     * @return uid
     */
    public static String getUserName(String token) {
        return Optional.ofNullable(verifyToken(token))
                .map(map -> map.get(USER_NAME_CLAIM))
                .map(Claim::asString)
                .orElse(null);
    }

}
