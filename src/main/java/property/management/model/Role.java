package property.management.model;

public enum Role {
    ADMIN,
    AGENT,
    CLIENT;
    
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}