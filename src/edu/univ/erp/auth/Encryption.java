package edu.univ.erp.auth;

import org.mindrot.jbcrypt.BCrypt;

public class Encryption {

    public String encrypt(String raw) {
        return BCrypt.hashpw(raw, BCrypt.gensalt());
    }

    public boolean matches(String raw, String hashed) {
        return BCrypt.checkpw(raw, hashed);
    }
}
