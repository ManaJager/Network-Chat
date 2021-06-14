package ru.gb.java2.kochemasov.server.auth;

import java.util.Objects;

public class User {
    public User(String login, String password, String username) {
        this.login = login;
        this.password = password;
        this.username = username;
    }

    private final String login;
    private final String password;
    private final String username;


    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(login, user.login) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, password);
    }
}
