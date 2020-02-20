package Classes;

public class Element_Repo {

    private String repo_id;
    private String repo_name;
    private String repo_desc;
    private int repo_stars;
    private String repo_username;
    private String repo_avatar;

    public Element_Repo() {
    }

    public Element_Repo(String repo_id, String repo_name, String repo_desc, int repo_stars, String repo_username, String repo_avatar) {
        this.repo_id = repo_id;
        this.repo_name = repo_name;
        this.repo_desc = repo_desc;
        this.repo_stars = repo_stars;
        this.repo_username = repo_username;
        this.repo_avatar = repo_avatar;
    }

    public String getRepo_id() {
        return repo_id;
    }

    public void setRepo_id(String repo_id) {
        this.repo_id = repo_id;
    }

    public String getRepo_name() {
        return repo_name;
    }

    public void setRepo_name(String repo_name) {
        this.repo_name = repo_name;
    }

    public String getRepo_desc() {
        return repo_desc;
    }

    public void setRepo_desc(String repo_desc) {
        this.repo_desc = repo_desc;
    }

    public int getRepo_stars() {
        return repo_stars;
    }

    public void setRepo_stars(int repo_stars) {
        this.repo_stars = repo_stars;
    }

    public String getRepo_username() {
        return repo_username;
    }

    public void setRepo_username(String repo_username) {
        this.repo_username = repo_username;
    }

    public String getRepo_avatar() {
        return repo_avatar;
    }

    public void setRepo_avatar(String repo_avatar) {
        this.repo_avatar = repo_avatar;
    }
}