CREATE TABLE Training (id integer primary key AUTO_INCREMENT,name text,position integer);

CREATE TABLE Measure (id integer primary key AUTO_INCREMENT,name text,max integer,step float,type integer);

CREATE TABLE ExerciseType (id integer primary key AUTO_INCREMENT,name text,icon_res text);

CREATE TABLE Exercise (id integer PRIMARY KEY  AUTO_INCREMENT,name text, type_id integer, FOREIGN KEY(type_id) REFERENCES ExerciseType(id));

CREATE TABLE ExerciseInTraining (training_id integer,exercise_id integer,position integer,FOREIGN KEY(training_id) REFERENCES Training(id),FOREIGN KEY(exercise_id) REFERENCES Exercise(id), PRIMARY KEY (training_id, exercise_id));

CREATE TABLE MeasureExType (ex_type_id integer,measure_id integer,position integer,FOREIGN KEY(ex_type_id) REFERENCES ExerciseType(id),FOREIGN KEY(measure_id) REFERENCES Measure(id),PRIMARY KEY (ex_type_id, measure_id));

CREATE TABLE TrainingStat (id integer primary key AUTO_INCREMENT,date datetime,training_date datetime,exercise_id integer,value text,training_id integer,FOREIGN KEY(exercise_id) REFERENCES Exercise(id));