package org.example.Domain;

import org.mindrot.jbcrypt.BCrypt;

public class Admin {
    private String username;
    private String hashedPassword;
    private boolean isLoggedIn = false;

    public Admin(String username, String password) {
        this.username = username;
        this.hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean login(String inputUsername, String inputPassword) {
        if (!username.equals(inputUsername)) return false;
        if (BCrypt.checkpw(inputPassword, hashedPassword)) {
            isLoggedIn = true;
            return true;
        }
        return false;
    }

    public void logout() { isLoggedIn = false; }
    public boolean isLoggedIn() { return isLoggedIn; }
}
