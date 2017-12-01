import sun.jvm.hotspot.runtime.Threads;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        BufferedReader bf = null;
        int productsno;
        SuperMarket superMarket = new SuperMarket();
        int moneyFromBills = 0;


        try {
            bf = new BufferedReader(new FileReader("/Users/alexandraneamtu/Documents/An3-sem1/PPD/lab1/src/products.txt"));
            productsno = Integer.parseInt(bf.readLine());
            while (productsno != 0) {
                String line = bf.readLine();
                superMarket.addProduct(new Product(line.split(" ")[0], Integer.parseInt(line.split(" ")[1]), Integer.parseInt(line.split(" ")[2])));
                productsno--;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Initial products: ");
        List<Product> initialProducts = superMarket.getProducts();
        for (int i = 0; i < initialProducts.size(); i++) {
            System.out.println(initialProducts.get(i));
        }

        executeThreads(superMarket,40,4);


        System.out.println("\n");
        System.out.println("-------------------");
        System.out.println("\n");

        System.out.println(superMarket.toString());

        for(int i=0;i<superMarket.bills.size();i++)
            moneyFromBills += superMarket.bills.get(i).totalPrice();
        System.out.println("---->Justified money: " + superMarket.moneyFromBills());
        System.out.println("---->Justified products: ");
        List<Product> soldProducts = superMarket.getSoldProducts();
        for(int i=0;i<soldProducts.size();i++)
            System.out.println(soldProducts.get(i));

        long endTime = System.currentTimeMillis();
        double time = (endTime - startTime);
        System.out.println("Execution time:"+time/1000);

    }

    static void executeThreads(SuperMarket superMarket, int threadsNo, int billsNo)
    {

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadsNo; i++) {
            if(i%4 == 0)
            {
                threads.add(new Thread(() -> System.out.println("Justified money at this time:" + superMarket.moneyFromBills())));
            }
            threads.add(new Thread(() -> {
                superMarket.generateBills(billsNo);
            }));
        }

        for(Thread thread : threads)
            thread.start();

        for(Thread thread : threads)
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
    }
}
