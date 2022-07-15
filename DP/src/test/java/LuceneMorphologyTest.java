import static org.junit.jupiter.api.Assertions.assertEquals;

import main.service.morphology.LuceneMorphology;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuceneMorphologyTest {

    @Test
    @DisplayName("Форматирование строки Лемматизатором")
    public void testLucene() {
        LuceneMorphology luceneMorphology = new LuceneMorphology();
        String text = "Повторное появление леопарда в Осетии позволяет предположить, что леопард постоянно обитает в некоторых районах Северного Кавказа.";
        Map<String, Long> expected = new HashMap<>();
        expected.put("повторный", 1L);
        expected.put("некоторый", 1L);
        expected.put("появление", 1L);
        expected.put("постоянно", 1L);
        expected.put("некоторые", 1L);
        expected.put("постоянный", 1L);
        expected.put("позволять", 1L);
        expected.put("северный", 1L);
        expected.put("предположить", 1L);
        expected.put("кавказ", 1L);
        expected.put("район", 1L);
        expected.put("осетия", 1L);
        expected.put("обитать", 1L);
        expected.put("леопард", 2L);

        List<String> list = luceneMorphology.luceneToList(text);
        Map<String, Long> actual = luceneMorphology.luceneToMap(list);
        assertEquals(expected, actual);
    }
}