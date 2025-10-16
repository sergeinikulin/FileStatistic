## Инструкция по работе с программой

Перед запуском программы, java классы необходимо скомпиллировать:

```cmd
# 1. навигируемся в директорию с классами программы
PS C:\> cd C:\Users\NIKULIN\IdeaProjects\FileStatistic

# 2. навигируемся в директорию с java файлами
PS C:\Users\NIKULIN\IdeaProjects\FileStatistic> cd src/main/java/ru/sergeinikulin/fileStatistic

# 3. компиллируем java файлы
PS C:\Users\NIKULIN\IdeaProjects\FileStatistic\src\main\java\ru\sergeinikulin\fileStatistic> javac *.java 
```





Как использовать программу:

```cmd
PS C:\Users\NIKULIN\IdeaProjects\FileStatistic\src\main\java> java -cp . ru.sergeinikulin.fileStatistic.Main ru/sergeinikulin/fileStatistic
```

Вывод программы: 

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

