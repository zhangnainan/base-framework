package com.sg.base.auth;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * PasswordHelper
 *
 * @author Dai Wenqing
 * @date 2016/2/28
 */
public class PasswordHelper {
    //private static RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
    private static String algorithmName = "md5";
    private static final int hashIterations = 2;
    private static String publicSalt = "_snow";

    public static String encryptPassword(String userName, String password) {
        // user.setSalt(randomNumberGenerator.nextBytes().toHex());
        return new SimpleHash(algorithmName, password, ByteSource.Util.bytes(userName + publicSalt), hashIterations).toHex();
    }
}
