package myApp.trainingdiary.db.entity;

import java.io.Serializable;

/**
 * Created by Boris on 04.06.13.
 */
public class Exercise implements Serializable {
    private Long id;
    private ExerciseType type;
    private String name;

    public Exercise() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExerciseType getType() {
        return type;
    }

    public void setType(ExerciseType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Exercise(Long id, ExerciseType type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", type=" + type +
                ", name='" + name + '\'' +
                '}';
    }
}
