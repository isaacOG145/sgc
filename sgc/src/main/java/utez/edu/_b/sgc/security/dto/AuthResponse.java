package utez.edu._b.sgc.security.dto;



public class AuthResponse {
    private String jwt;
    private Long userId;
    private String email;
    private String role;
    private long expiration;

    public AuthResponse(String jwt, Long userId, String email,String role, long expiration) {
        this.jwt = jwt;
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.expiration = expiration;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}