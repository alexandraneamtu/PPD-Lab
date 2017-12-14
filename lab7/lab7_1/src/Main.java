import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Main {

    private static int SIZE=4;
    private static int NUMBER_OF_THREADS=2;
    private static List<Integer> initialnumbers = generateList(SIZE);
    private static List<Integer> prefixSum = generateZeroList(SIZE);
    private static List<Integer> prefixSumSeq = generateZeroList(SIZE);


    public static void main(String[] args) {

        System.out.println("Initial sequence of numbers:");
        System.out.println(initialnumbers + "\n");


        Node root = new Node(0,SIZE);

        long startTime = System.nanoTime();
        upPass(root);
        //System.out.println("Inorder computed sums:");
        //print1Step(root);
        //System.out.println("\n");

        downPass(root);
        //System.out.println("\nInorder computed prefixes");
        //print2Step(root);
        //System.out.println("\n");
        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000;



        System.out.println(prefixSum);
        System.out.println("Parallel execution took : " + duration + " milliseconds");
        //execSeq();
        //System.out.println(prefixSumSeq);
    }

    private static void execSeq(){
        prefixSumSeq.set(0,initialnumbers.get(0));
        for(int i=1;i<SIZE;i++){
            prefixSumSeq.set(i,prefixSumSeq.get(i-1)+initialnumbers.get(i));
        }

    }

    private static void upPass(Node root) {
        ExecutorService executorService1 = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        ExecutorService executorService2 = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        //build a binary tree with the sum of elements
        int startIndex = root.getStartIndex();
        int endIndex = root.getEndIndex();
        int middleIndex = (startIndex + endIndex) / 2;

        if (endIndex - startIndex > 1) {
            Node leftNode = new Node(startIndex, middleIndex);
            Node rightNode = new Node(middleIndex, endIndex);
            root.setLeftNode(leftNode);
            root.setRightNode(rightNode);

            executorService1.execute(() -> upPass(leftNode));
            executorService2.execute(() -> upPass(rightNode));

            executorService1.shutdown();
            executorService2.shutdown();

            //The shutdown() method does one thing: prevents clients to send more work to the executor service.
            //This means all the existing tasks will still run to completion unless other actions are taken.
            //We have to wait for all the tasks to finish

            try {
                executorService1.awaitTermination(1000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                executorService2.awaitTermination(1000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            root.setSum(leftNode.getSum() + rightNode.getSum());
        } else {
            //leafs
            root.setSum(initialnumbers.get(startIndex));
        }
    }


    private static void downPass(Node root) {
        ExecutorService executorService1 = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        ExecutorService executorService2 = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        //	Pass down a value fromLeftSum
        // the nodes are already created
        if (root.getLeftNode() != null && root.getRightNode() != null) {
            root.getLeftNode().setFromLeft(root.getFromLeft());
            root.getRightNode().setFromLeft(root.getFromLeft() + root.getLeftNode().getSum());

            executorService1.execute(() -> downPass(root.getLeftNode()));
            executorService2.execute(() -> downPass(root.getRightNode()));

            executorService1.shutdown();
            executorService2.shutdown();

            try {
                executorService1.awaitTermination(1000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                executorService2.awaitTermination(1000, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            //leaf
            prefixSum.set(root.getStartIndex(), root.getFromLeft() + initialnumbers.get(root.getStartIndex()));
        }
    }

    private static void print1Step(Node root) {
        if (root != null) {
            print1Step(root.getLeftNode());
            System.out.print(root.getSum() + " ");
            print1Step(root.getRightNode());
        }

    }

    private static void print2Step(Node root) {
        if (root != null) {
            print2Step(root.getLeftNode());
            System.out.print(root.getFromLeft() + " ");
            print2Step(root.getRightNode());
        }
    }


    private static List<Integer> generateZeroList(int size) {
        List<Integer> list = new ArrayList<>();
        for(int i=0;i<size;i++){
            list.add(0);
        }
        return list;
    }




    private static List<Integer> generateList(int size){
        List<Integer> list = new ArrayList<>();
        for(int i=0;i<size;i++){
            list.add(ThreadLocalRandom.current().nextInt(1,10));
        }
        return list;
    }
}
