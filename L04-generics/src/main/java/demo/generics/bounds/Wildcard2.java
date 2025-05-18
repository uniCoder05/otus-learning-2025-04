package demo.generics.bounds;

import demo.generics.bounds.entries.Animal;
import demo.generics.bounds.entries.Cat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"unchecked", "rawtypes", "java:S125"})
public class Wildcard2 {
    private static final Logger logger = LoggerFactory.getLogger(Wildcard2.class);

    public static void main(String[] args) {

        List<Animal> animalList = new ArrayList<>();
        animalList.add(new Animal());
        printWild(animalList);
        // printObj(animalList); // ошибка
        printRaw(animalList);

        List<Cat> catList = new ArrayList<>();
        catList.add(new Cat());
        printWild(catList);
        // printObj(catList);  // ошибка
        printRaw(catList);

        // левый тип данных
        List<String> stringList = new ArrayList<>();
        stringList.add("подкидыш");
        printWild(stringList);
        // printObj(stringList);  // ошибка
        printRaw(stringList);

        // Можно еще и так
        List voidList = new ArrayList<>();
        voidList.add(LocalTime.now());
        printWild(voidList);
        printObj(voidList);
        printRaw(voidList);
    }

    // Unbounded Wildcards
    // List<?> - список объектов неизвестного, но конкретного типа
    private static void printWild(List<?> animalList) {
        // animalList.add("внезапно добавленная строка"); // ошибка
        // animalList.add(123);
        // animalList.add(new Animal()); // ошибка
        animalList.add(null); // ?
        animalList.forEach(animal -> logger.info("{}", animal));
    }

    // List<Object> - список объектов любого типа
    private static void printObj(List<Object> animalList) {
        animalList.add("внезапно добавленная строка");
        animalList.add(123);
        animalList.add(new Animal());
        animalList.add(null);
        animalList.forEach(animal -> logger.info("{}", animal));
    }

    // List - не выполняется никакой проверки типов
    private static void printRaw(List animalList) {
        animalList.add("внезапно добавленная строка");
        animalList.add(123);
        animalList.add(new Animal());
        animalList.add(null);
        animalList.forEach(animal -> logger.info("{}", animal));
    }
}
