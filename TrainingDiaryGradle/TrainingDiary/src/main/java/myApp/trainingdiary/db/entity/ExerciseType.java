package myApp.trainingdiary.db.entity;

import java.util.ArrayList;

/**
 * Created by Boris on 04.06.13.
 */
public class ExerciseType {
    private Long id;
    private ExerciseTypeIcon icon;
    private String name;

    private ArrayList<Measure> measures;

    public ArrayList<Measure> getMeasures() {
        if (measures == null)
            measures = new ArrayList<Measure>();
        return measures;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExerciseTypeIcon getIcon() {
        return icon;
    }

    public void setIcon(ExerciseTypeIcon icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExerciseType(Long id, ExerciseTypeIcon icon, String name) {
        this.id = id;
        this.icon = icon;
        this.name = name;
    }
}
