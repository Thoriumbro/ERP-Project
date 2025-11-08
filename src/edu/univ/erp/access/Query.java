package edu.univ.erp.access;

public class Query {

    public static final String INSERT_USER =
            "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

    public static final String GET_USER_BY_USERNAME =
            "SELECT * FROM users WHERE username = ?";

    public static final String DELETE_USER =
            "DELETE FROM users WHERE username = ?";

    public static final String UPDATE_USER_ROLE =
            "UPDATE users SET role = ? WHERE username = ?";

}
