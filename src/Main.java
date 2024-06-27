import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    private static final int QUEUE_CAPACITY = 100;
    private static final int TEXT_COUNT = 10_000;
    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(QUEUE_CAPACITY);


    private static int maxACount = 0;
    private static int maxBCount = 0;
    private static int maxCCount = 0;

    public static void main(String[] args) {

        Thread generatorThread = new Thread(() -> {
            for (int i = 0; i < TEXT_COUNT; i++) {
                String text = GenerateText.generateText("abc", 100_000);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread aCounterThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String text = queueA.take();
                    int count = countChar(text, 'a');
                    if (count > maxACount) {
                        maxACount = count;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread bCounterThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String text = queueB.take();
                    int count = countChar(text, 'b');
                    if (count > maxBCount) {
                        maxBCount = count;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread cCounterThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    String text = queueC.take();
                    int count = countChar(text, 'c');
                    if (count > maxCCount) {
                        maxCCount = count;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        generatorThread.start();
        aCounterThread.start();
        bCounterThread.start();
        cCounterThread.start();

        try {
            generatorThread.join();
            aCounterThread.interrupt();
            bCounterThread.interrupt();
            cCounterThread.interrupt();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Строка с максимальным количеством 'a' содержит: " + maxACount + " 'a')");
        System.out.println("Строка с максимальным количеством 'b' содержит: " + maxBCount + " 'b')");
        System.out.println("Строка с максимальным количеством 'c' содержит: " + maxCCount + " 'c')");
    }

    private static int countChar(String text, char ch) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == ch) {
                count++;
            }
        }
        return count;
    }
}