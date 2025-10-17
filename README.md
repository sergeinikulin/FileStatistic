## Инструкция по работе с программой

## замечания:

1) Программа работает из консоли.
2) Количество строк с комментариями  реализовано для файлов `JAVA`, `BASH`, `XML` в файле `FileAnalyze.java`
3) Скриншоты работы программы решил не вставлять, команды которые надо подать на вход и вывод описал в `README.md` файле
4) Тестовая директория для программы лежит в `FileStatistic\src\main\resources`
5) Также ко всему этому я написал тесты, которые лежат в директории `src/test/java` : FileStatisticsTest и OutputFormatTest

### Перед запуском программы, java классы необходимо скомпиллировать:

```cmd
# 1. навигируемся в директорию с программой
PS C:\> cd C:\Users\NIKULIN\IdeaProjects\FileStatistic

# 2. навигируемся в директорию с java файлами
PS C:\Users\NIKULIN\IdeaProjects\FileStatistic> cd src/main/java/ru/sergeinikulin/fileStatistic

# 3. компиллируем java файлы
PS C:\Users\NIKULIN\IdeaProjects\FileStatistic\src\main\java\ru\sergeinikulin\fileStatistic> javac *.java 
```

### Как использовать программу:

#### -path параметр

```cmd
PS C:\Users\NIKULIN\IdeaProjects\FileStatistic\src\main\java> java -cp . ru.sergeinikulin.fileStatistic.Main ../resources
```

Вывод: 

```java
File Statistics:
================

Extension: .java
  Files: 5
  Size: 17454 bytes
  Total lines: 518
  Non-empty lines: 424
  Comment lines: 4

Extension: .sh
  Files: 1
  Size: 28 bytes
  Total lines: 2
  Non-empty lines: 2
  Comment lines: 1

Extension: .json
  Files: 1
  Size: 234 bytes
  Total lines: 12
  Non-empty lines: 11
  Comment lines: 0
```

*/resources* в команде - это путь, с которым мы работаем, т.е параметр `<path>`. Для удобства тестирования мы будем использовать отдельную директорию, но ее можно в команде поменять на нужную.

#### --recursive параметр

```cmd
PS C:\Users\NIKULIN\IdeaProjects\FileStatistic\src\main\java> java -cp . ru.sergeinikulin.fileStatistic.Main ../resources --recursive
```

Вывод:

```
File Statistics:
================

Extension: .java
  Files: 3
  Size: 13863 bytes
  Total lines: 430
  Non-empty lines: 347
  Comment lines: 4

Extension: .sh
  Files: 3
  Size: 84 bytes
  Total lines: 6
  Non-empty lines: 6
  Comment lines: 3

Extension: .json
  Files: 3
  Size: 702 bytes
  Total lines: 36
  Non-empty lines: 33
  Comment lines: 0
```

мы полностью рекурсивно пробежались по всей директории и собрали статистику. 

#### --max-depth параметр

```cmd
PS C:\Users\NIKULIN\IdeaProjects\FileStatistic\src\main\java> java -cp . ru.sergeinikulin.fileStatistic.Main ../resources --recursive --max-depth=2
```

Вывод

```
File Statistics:
================

Extension: .sh
  Files: 2
  Size: 56 bytes
  Total lines: 4
  Non-empty lines: 4
  Comment lines: 2

Extension: .json
  Files: 2
  Size: 468 bytes
  Total lines: 24
  Non-empty lines: 22
  Comment lines: 0
```

Мы при анализе погрузились только на определенную грубину, заданную в параметре --max-depth

#### --include-ext

```cmd
PS C:\Users\NIKULIN\IdeaProjects\FileStatistic\src\main\java> java -cp . ru.sergeinikulin.fileStatistic.Main ../resources --recursive --include-ext=java
```

```
File Statistics:
================

Extension: .java
  Files: 3
  Size: 13863 bytes
  Total lines: 430
  Non-empty lines: 347
  Comment lines: 4
```

Вывел **только** статистику по java файлам во **всех** директориях.

#### **--thread**

```cmd
PS C:\Users\NIKULIN\IdeaProjects\FileStatistic\src\main\java> java -cp . ru.sergeinikulin.fileStatistic.Main ../resources --recursive --include-ext=java,xml --thread=4 
```

```
File Statistics:
================

Extension: .java
  Files: 3
  Size: 13863 bytes
  Total lines: 430
  Non-empty lines: 347
  Comment lines: 4

Extension: .xml
  Files: 1
  Size: 409 bytes
  Total lines: 11
  Non-empty lines: 11
  Comment lines: 3
```

#### --exclude-ext

```cmd
PS C:\Users\NIKULIN\IdeaProjects\FileStatistic\src\main\java> java -cp . ru.sergeinikulin.fileStatistic.Main ../resources --recursive --exclude-ext=java,json
```

```
File Statistics:
================

Extension: .sh
  Files: 3
  Size: 84 bytes
  Total lines: 6
  Non-empty lines: 6
  Comment lines: 3

Extension: .xml
  Files: 1
  Size: 409 bytes
  Total lines: 11
  Non-empty lines: 11
  Comment lines: 3
```

#### --json

```cmd
PS C:\Users\NIKULIN\IdeaProjects\FileStatistic\src\main\java> java -cp . ru.sergeinikulin.fileStatistic.Main ../resources --recursive --exclude-ext=java,json --output=json
```

```
{
  "statistics": [
    {
      "extension": "sh",
      "files": 3,
      "size": 84,
      "totalLines": 6,
      "nonEmptyLines": 6,
      "commentLines": 3
    },
    {
      "extension": "xml",
      "files": 1,
      "size": 409,
      "totalLines": 11,
      "nonEmptyLines": 11,
      "commentLines": 3
    }
  ]
}
```

#### 

