package myApp.trainingdiary.db.entity;

/**
 * Created by Lenovo on 09.12.13.
 */
public enum ExerciseTypeIcon {
    icon_ex_count("myApp.trainingdiary:drawable/icon_ex_count"),
    icon_ex_cyclingsport("myApp.trainingdiary:drawable/icon_ex_cyclingsport"),
    icon_ex_drugs("myApp.trainingdiary:drawable/icon_ex_drugs"),
    icon_ex_dumbbell("myApp.trainingdiary:drawable/icon_ex_dumbbell"),
    icon_ex_power("myApp.trainingdiary:drawable/icon_ex_power"),
    icon_ex_pushup("myApp.trainingdiary:drawable/icon_ex_pushup"),
    icon_ex_size("myApp.trainingdiary:drawable/icon_ex_size"),
    icon_ex_weight("myApp.trainingdiary:drawable/icon_ex_weight"),
    icon_ex_yoga("myApp.trainingdiary:drawable/icon_ex_yoga"),
    icon_ex_cycle("myApp.trainingdiary:drawable/icon_ex_cycle");

    private String iconResName;

    ExerciseTypeIcon(String iconResName) {
        this.iconResName = iconResName;
    }

    public String getIconResName() {
        return iconResName;
    }

    public static ExerciseTypeIcon getByIconResName(String icon_res) {
        for (ExerciseTypeIcon exerciseTypeIcon : ExerciseTypeIcon.values()) {
            if (icon_res.equals(exerciseTypeIcon.getIconResName())) {
                return exerciseTypeIcon;
            }
        }
        throw new IllegalArgumentException("Not found Enum wiht icon_res: " + icon_res);
    }
}
