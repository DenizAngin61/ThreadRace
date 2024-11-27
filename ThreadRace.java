import java.util.ArrayList;
import java.util.List;

public class ThreadRace {

    // Ortak ArrayList'ler (Tek ve Çift sayılar için)
    private static final List<Integer> oddNumbers = new ArrayList<>();
    private static final List<Integer> evenNumbers = new ArrayList<>();

    public static void main(String[] args) {
        // 1'den 10,000'e kadar olan sayılardan oluşan liste
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 10000; i++) {
            numbers.add(i);
        }

        // 4 eşit parçaya bölmek için 4 yeni liste oluştur
        List<List<Integer>> partitions = new ArrayList<>();
        int partitionSize = 2500; // Her bir alt liste için 2500 eleman

        for (int i = 0; i < 4; i++) {
            // Yeni bağımsız alt listeler oluştur
            List<Integer> sublist = new ArrayList<>();
            for (int j = i * partitionSize; j < (i + 1) * partitionSize; j++) {
                sublist.add(numbers.get(j));
            }
            partitions.add(sublist);
        }

        // Thread'leri oluşturma
        List<Thread> threads = new ArrayList<>();
        for (List<Integer> partition : partitions) {
            Thread thread = new Thread(new NumberProcessor(partition));
            threads.add(thread);
            thread.start();
        }

        // Thread'lerin tamamlanmasını bekleme
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Sonuçları yazdırma
        System.out.println("Çift Sayılar: " + evenNumbers);
        System.out.println("Tek Sayılar: " + oddNumbers);
    }

    // Sayı işleme sınıfı (Thread olarak çalışacak)
    static class NumberProcessor implements Runnable {
        private final List<Integer> numbers;

        public NumberProcessor(List<Integer> numbers) {
            this.numbers = numbers;
        }

        @Override
        public void run() {
            for (int number : numbers) {
                if (number % 2 == 0) {
                    synchronized (evenNumbers) {
                        evenNumbers.add(number);
                    }
                } else {
                    synchronized (oddNumbers) {
                        oddNumbers.add(number);
                    }
                }
            }
        }
    }
}
