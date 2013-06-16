package myApp.trainingdiary.db.entity;

/**
 * Created by Boris on 04.06.13.
 */
public class ExerciseType {
    private Long id;
    private String icon;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExerciseType(Long id, String icon, String name) {
        this.id = id;
        this.icon = icon;
        this.name = name;
    }
}
