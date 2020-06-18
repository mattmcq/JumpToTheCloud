import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActionTrackerTest {

    ActionTracker tracker;

    @BeforeEach
    public void setUp() {
        tracker = new ActionTracker();
    }

    @Test
    public void testAddAction() {
        addSampleActions();
        assertEquals(3, tracker.values.size());
        assertEquals(2, tracker.averages.size());
    }

    @Test
    public void testAddAction_manyActions() {
        addManyActions();
        assertEquals(6, tracker.values.size());
        assertEquals(4, tracker.averages.size());
    }

    @Test
    public void testGetStats() {
        addSampleActions();
        String results = tracker.getStats();

        /*
        The output specified in the assignment was originally:
        [
            {"action":"jump", "avg":150}, {"action":"run", "avg":75}
        ]
        However JSONObject is by definition an unordered set of name/value pairs and I was unable to get my output
        sorted as shown above (action before avg). If it's possible I would love to learn how to do it!
         */
        String expected = "[{\"avg\":150,\"action\":\"jump\"},{\"avg\":75,\"action\":\"run\"}]";
        assertEquals(expected, results);
    }

    @Test
    public void testGetStats_manyActions() {
        addManyActions();
        String results = tracker.getStats();
        String expected = "[{\"avg\":20.5,\"action\":\"bike\"},{\"avg\":150,\"action\":\"jump\"},{\"avg\":75,\"action\":\"run\"},{\"avg\":2000,\"action\":\"walk\"}]";
        assertEquals(expected, results);
    }

    @Test
    public void testAddAction_givenMultiThread() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(3);

        IntStream.range(0, 1000)
                .forEach(count -> service.submit(this::addSampleActions));
        service.awaitTermination(1000, TimeUnit.MILLISECONDS);

        assertEquals(3000, tracker.values.size());  // without 'synchronized' on addAction this fails
    }

    private void addSampleActions() {
        tracker.addAction("{\"action\":\"jump\", \"time\":100}");
        tracker.addAction("{\"action\":\"run\", \"time\":75}");
        tracker.addAction("{\"action\":\"jump\", \"time\":200}");
    }

    private void addManyActions() {
        tracker.addAction("{\"action\":\"jump\", \"time\":100}");
        tracker.addAction("{\"action\":\"run\", \"time\":75}");
        tracker.addAction("{\"action\":\"jump\", \"time\":200}");
        tracker.addAction("{\"action\":\"walk\", \"time\":2000}");
        tracker.addAction("{\"action\":\"bike\", \"time\":21}");
        tracker.addAction("{\"action\":\"bike\", \"time\":20}");
    }

}