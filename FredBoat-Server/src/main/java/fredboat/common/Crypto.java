package fredboat.common;

import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;

public class Crypto {
    
    private static final String SALT = "87ACSRg88jJxjTlJ";
    
    public static String hash(String str){
        return BCrypt.hashpw(str, SALT);
    }
    
    public static String generateRandomString(int size){
        return RandomStringUtils.random(size);
    }
    
}
