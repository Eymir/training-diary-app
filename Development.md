# Модель данных v3 #

<img src='http://www.gliffy.com/pubdoc/4349858/L.png' />

Training (список тренировок):
  * name - название тренировки;
  * position - положение тренировки;

Exercise (Список упражнений и их тип):
  * name - название упражнения;
  * type\_id - FK на типы тренировок (ExerciseType);

ExerciseInTraining (таблица соответствий тренировка - упражнение):
  * training\_id - FK на тренировку;
  * exercise\_id - FK на упражнение;
  * position - положение упражнения в тренировке;

TrainingStat (История выполнения упражнений):
  * training\_date - дата и время подхода (Измеряется целым числом - UnixTime, количество миллисекунд с 1970 года);
  * exercise\_id - на конкретное упражнение;
  * value - значение в формате (AA.AxBBxCC) !!!Важно: этот формат, чрезвычайно не устойчив к изменениям в измерениях - например при изменении положения измерений между собой в типе, данные становятся ошибочными.

ExerciseType (Тип упражнения):
  * name - название типа;
  * icon\_res - имя файла иконки;

Measure (Измерение):
  * name - название измерения;
  * max - максимальное значение;
  * step - шаг между значениями (напр.: 0.5);
  * type - не очевидное поле, тип измерения если 0 - обычное числовое, 1 - время, для него нужен другой барабан, и для него мах и step по другому считаются 0-миллисекунды, 1-секунды, 2-минуты, 3-часы (напр.: max-2,step-1 означают,что выбрать можно будет только минуты и секунды);

MesureExType (Соответствие типов упражнений и входящих в них измерений):
  * ex\_type\_id - FK на ExerciseType;
  * mesure\_id - FK на Mesure;
    * position - положение измерения в типе упражнения;

# Модель данных v2 #

<img src='http://www.gliffy.com/pubdoc/4266488/L.png' />

Trainingtable (просто список тренировок):
  * name - Хранит названия тренировок
  * exercise - Пустое поле, нигде не используется

ExerciseTable (Список упражнений и их тип):
  * exercise - название упражнения
  * type - тип упражнения: 1 - Силовое; 2 - Циклическое;

TrainingProgTable (таблица соответствий тренировка - упражнение):
  * trainingname - название тренировки
  * exercise - название упражнения
  * exidintr - номер упражнения в тренировке


TrainingStat (История выполнения упражнений):
  * trainingdate - Дата
  * trainingday - название тренировочного дня
  * exercise - Название упражнения
  * exercisetype - Тип упражнения
    * power - Вес или расстояние (кг, м)
    * count - Количество повторений или время (разы, секунды)