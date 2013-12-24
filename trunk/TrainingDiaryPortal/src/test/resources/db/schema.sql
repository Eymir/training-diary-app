CREATE TABLE Training (id identity   ,name varchar(200),position integer);

CREATE TABLE Measure (id identity  ,name varchar(200),maxValue integer,step float,type integer);

CREATE TABLE ExerciseType (id identity ,name varchar(200),icon_res varchar(200) );

CREATE TABLE Exercise (id identity   ,name varchar(200), type_id integer, FOREIGN KEY(type_id) REFERENCES ExerciseType(id));

CREATE TABLE ExerciseInTraining (training_id integer,exercise_id integer,position integer,FOREIGN KEY(training_id) REFERENCES Training(id),FOREIGN KEY(exercise_id) REFERENCES Exercise(id), PRIMARY KEY (training_id, exercise_id));

CREATE TABLE MeasureExType (ex_type_id integer,measure_id integer,position integer,FOREIGN KEY(ex_type_id) REFERENCES ExerciseType(id),FOREIGN KEY(measure_id) REFERENCES Measure(id),PRIMARY KEY (ex_type_id, measure_id));

CREATE TABLE TrainingStat (id identity  ,date DOUBLE ,training_date DOUBLE,exercise_id integer,value varchar(200),training_id integer,FOREIGN KEY(exercise_id) REFERENCES Exercise(id));

CREATE TABLE android_metadata (locale varchar(200) );

CREATE TABLE sqlite_sequence(name varchar(200),seq varchar(200));