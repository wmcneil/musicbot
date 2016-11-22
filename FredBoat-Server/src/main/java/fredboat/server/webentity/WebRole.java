package fredboat.server.webentity;

public class WebRole {

    private final long id;
    private final String name;
    private final int color;
    private final int position;
    private final int permissions;

    public WebRole(long id, String name, int color, int position, int permissions) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.position = position;
        this.permissions = permissions;
    }

    public WebRole(String id, String name, int color, int position, int permissions) {
        this.id = Long.parseLong(id);
        this.name = name;
        this.color = color;
        this.position = position;
        this.permissions = permissions;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public int getPosition() {
        return position;
    }

    public int getPermissions() {
        return permissions;
    }

}
