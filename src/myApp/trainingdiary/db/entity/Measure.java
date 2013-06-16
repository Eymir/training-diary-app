package myApp.trainingdiary.db.entity;

/**
 * Created by Boris on 26.05.13.
 */
public class Measure {
    private Long id;
    private String name;
    private Integer max;
    private Double step;
    private MeasureType type;

    public Measure(Long id, String name, Integer max, Double step, MeasureType type) {
        this.id = id;
        this.name = name;
        this.max = max;
        this.step = step;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Double getStep() {
        return step;
    }

    public void setStep(Double step) {
        this.step = step;
    }

    public MeasureType getType() {
        return type;
    }

    public void setType(MeasureType type) {
        this.type = type;
    }
}
