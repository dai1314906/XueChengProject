package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestJwt {
    @Test
    public void testCreateJwt(){

        //密钥库文件
        String keystore = "xc.keystore";

        //密钥库密码
        String keystore_password = "xuechengkeystore";

        //密钥库文件路径
        ClassPathResource classPathResource = new ClassPathResource(keystore);

        //密钥别名
        String alias = "xckey";

        //密钥访问密码
        String key_password = "xuecheng";

        //密钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(classPathResource, keystore_password.toCharArray());
        //密钥对(公钥和私钥)
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, key_password.toCharArray());
        //获取私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();
        //jwt令牌的内容
        Map<String,String> body = new HashMap<>();
        body.put("name","itcast");
        String bodyString = JSON.toJSONString(body);
        //生成jwt令牌
        Jwt jwt = JwtHelper.encode(bodyString, new RsaSigner(aPrivate));
        //生成jwt令牌编码
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
        /*eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoiaXRjYXN0In0.SzB4Eh8JMG7DtpNfaIud7VeZGeYNTrxZDBBB3CQmAng538nTE78QKTohTnTGL3Js2aVPWlCNXE7hUr_9bU3RZ-LXZzM-QW8WRa6LMmBnfbrKgy6dFdnfKztifum9WBNKFTJZKywUnoFLl5iVhu6rh8vkdyW6okuans29_-xxfE0JNeWbQ1O17t3GaLFr8DFR72XO99FVb8W-o7YGvuXEqzSbVDcQSPpY5uJrfhuF-20-pKnoJjePEW0x1FTYku9_36Q1acVY4YSNz47yWfCvWrXWpTBSoAMG1Ih2Omx5Ssx2H3yejddeT1FA4QD21cl4auNO4EEt8zKDxgyXOlVHWQ*/
    }
    //校验jwt令牌
    @Test
    public void testVerify(){
        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1Z+7UXHlcX//vZXQKnBshPBUqdgqwFz8YH1jInmf0Ey+anKXuyopuzZ2k+MIag9swx7Bpb5UjrhbOnNf1ztLVFb1rqcCsX88VBv9DmShBPFLXrwYrPj+UovQyyHy5M0hoHmEdzUi/b2E0NUXsj84y6vdZdgkqu++O+nDVlk0iDtITBVBm0Ngizs2hhT8mmVy5Fh7yziPAe+WhybzaeHiA5Bjf7chk9O0xjP33Lpl7BjgK2YP0qsqllu69H+EjLpK0hgmG/w28H88a3EpESkej5KT2h9ARyBfec+vJos9iq9aZAQ6v/SnGRnBlNn4ZbH+KPFqnGFCXvELK9kM/l1VzwIDAQAB-----END PUBLIC KEY-----";
        //jwt令牌
        String jwtString = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJuYW1lIjoidGVzdDAyIiwidXR5cGUiOiIxMDEwMDIiLCJpZCI6IjQ5IiwiZXhwIjoxNjMyNjc3Nzg5LCJhdXRob3JpdGllcyI6WyJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX2Jhc2UiLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX2RlbCIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfbGlzdCIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2VfcGxhbiIsInhjX3RlYWNobWFuYWdlcl9jb3Vyc2UiLCJjb3Vyc2VfZmluZF9saXN0IiwieGNfdGVhY2htYW5hZ2VyIiwieGNfdGVhY2htYW5hZ2VyX2NvdXJzZV9tYXJrZXQiLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX3B1Ymxpc2giLCJjb3Vyc2VfcGljX2xpc3QiLCJ4Y190ZWFjaG1hbmFnZXJfY291cnNlX2FkZCJdLCJqdGkiOiJhZmY4M2NlZi02NDgyLTQ1MzQtOTQ5Zi04NDQ1MWFhYTgyYTYiLCJjbGllbnRfaWQiOiJYY1dlYkFwcCJ9.p1p1Gk_yMe_pauCO_oNi3-gXvPsltU8InWduQGwrgeYfzg4Pf7O6vcBUz_wrPq8DEfIfeOckZRd43rffuhtveLv_5LXeNsp8b0OJ9KXldPIPdKRH_2l0ZbAIwqlMgeXCDqPfTU2K7Yf9CH7Fhg_-I3JyrsaestmOON6g0IC8RRg0hiCeVROICR35AoGQ5a9rwpw0Q19PgVfdYAkgt4hrtNwMq_74lktSfgxoZkTCZjD3KCZguoAEvsMFCChzJsS0iTZWy3mjexvaj9vKxiPdz72ydSmdSgm_VmYJUTxVbB9m26Rxo-DYnrEq_TrDHMUhEn9IlPSWyYJWD_F2Q7jm4A";

        Jwt jwt = JwtHelper.decodeAndVerify(jwtString, new RsaVerifier(publickey));
        //拿到jwt令牌中定义的内容
        String claims = jwt.getClaims();
        System.out.println(claims);
    }
}
