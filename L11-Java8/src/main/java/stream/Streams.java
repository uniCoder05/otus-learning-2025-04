package stream;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"java:S125", "java:S3864", "java:S1130"})
public class Streams {
    private static final Logger logger = LoggerFactory.getLogger(Streams.class);

    private static final List<Student> students = Arrays.asList(
            new Student("Alex", 22, 5, 4.5),
            new Student("Maria", 22, 5, 3.5),
            new Student("John", 12, 4, 4.7),
            new Student("Bob", 22, 5, 4.8),
            new Student("Anna", 20, 3, 4.5));

    public static void main(String[] args) throws IOException {
        // creating();
        // stringsJoiner();
        // filterMapReduce();
        // System.out.println(getAvgMark());
        // groupBy();

        // streamConsumers();

        // flatMap();

        // streamNotStartedWithPeek();
    }

    public static void creating() {
        Stream<String> empty = Stream.empty();
        List<String> stringList = empty.map(String::toUpperCase).toList();
        logger.info("{}", stringList);

        var single = Stream.of(10);
        List<Integer> singleIntList = single.map(val -> val * 10).toList();
        logger.info("{}", singleIntList);

        var array = Stream.of(1, 2, 3);
        List<Integer> arrayIntList = array.map(val -> val * 10).toList();
        logger.info("{}", arrayIntList);

        var range = IntStream.range(1, 5);
        List<Integer> rangeList = range.map(val -> val * 10).boxed().toList();
        logger.info("{}", rangeList);
    }

    public static void stringsJoiner() {
        String[] arrayOfString = {"A", "B", "C", "D"};
        var result = Arrays.stream(arrayOfString).map(String::toLowerCase).collect(Collectors.joining(","));
        logger.info("{}", result);

        String result2 = String.join(",", arrayOfString);
        logger.info("{}", result2);
    }

    public static void filterMapReduce() {
        logger.info("filterMapReduce");
        var list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
        int result =
                list.stream().filter(val -> val % 2 > 0).map(val -> val * 10).reduce(0, Integer::sum);
        logger.info("result:{}", result);
    }

    public static double getAvgMark() {
        return students.stream()
                .filter(st -> st.course() == 5)
                .mapToDouble(Student::avgMark)
                .average()
                .orElseThrow(() -> new RuntimeException("Can't calculate average"));
    }

    public static void groupBy() {
        var map = students.stream().collect(Collectors.groupingBy(Student::course));
        logger.info("{}", map);
    }

    public static void streamConsumers() {
        var intStream = Stream.of(1, 2, 3, 4, 5);
        consume(intStream);
        consume(intStream);
    }

    public static void consume(Stream<Integer> stream) {
        List<Integer> rangeList = stream.toList();
        logger.info("{}", rangeList);
    }

    public static void flatMap() {
        var data = List.of(List.of(1, 2, 3, 4), List.of(10, 20, 30, 40), List.of(100, 200, 300, 400));

        var dataFlat = data.stream().flatMap(Collection::stream).toList();
        logger.info("{}", dataFlat);
    }

    public static void streamNotStartedWithPeek() {
        var dataStream = Stream.of(1, 2, 3, 4, 5).map(val -> val * 10).peek(val -> logger.info("{}", val));
        // .forEach(System.out::println);

        logger.info("{}", dataStream.toList());
    }
}
