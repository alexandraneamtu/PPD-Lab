import sun.management.counter.Units;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    private static int THREADSNO = 100;
    private static ExecutorService executorService = Executors.newFixedThreadPool(THREADSNO);

    private static Matrix matrix1;
    private static Matrix matrix2;
    //static int[][] resultmatrix;


    public static void main(String[] args) {

        matrix1 = new Matrix(1000, 1000, true);
        matrix2 = new Matrix(1000, 1000, true);

        //printMatrix(matrix1.getRowNo(),matrix1.getColumnNo(),matrix1);
        System.out.println();
        //printMatrix(matrix2.getRowNo(),matrix2.getColumnNo(),matrix2);
        Matrix result;


        long startTime = System.nanoTime();
        //sum with future
//        if(sumValidation()==true) {
//            result = new Matrix(matrix1.getRowNo(), matrix1.getColumnNo(), false);
//            try {
//                result = execThreads2(result);
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//        }
//        else
//            return;


        //multiplication with future
        if (prodValidation()) {

            result = new Matrix(matrix1.getRowNo(), matrix2.getColumnNo(), false);
            try {
                result = execThreads2(result);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } else
            return;

        //sum with threadpool
//        if(sumValidation()==true) {
//            result = new Matrix(matrix1.getRowNo(), matrix1.getColumnNo(), false);
//            try {
//                result = execThreads3(result);
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//        }
//        else
//            return;


        //multiplication with threadpool
//        if (prodValidation()) {
//
//            result = new Matrix(matrix1.getRowNo(), matrix2.getColumnNo(), false);
//            try {
//                result = execThreads3(result);
//            } catch (InterruptedException | ExecutionException e) {
//                e.printStackTrace();
//            }
//        } else
//            return;


        long endTime = System.nanoTime();

        System.out.println();

        //printMatrix(result.getRowNo(),result.getColumnNo(),result);

        double time = (endTime - startTime);
        System.out.println("Execution time:" + time);
    }

    public static void printMatrix(int x, int y, Matrix a) {
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++)
                System.out.print(a.getValues()[i][j] + " ");
            System.out.println();
        }
    }


    public static boolean sumValidation() {
        if (matrix1.getValues().length != matrix2.getValues().length) {
            System.out.println("errorrrrr1");
            return false;
        } else
            for (int i = 0; i < matrix1.getRowNo(); i++)
                if (matrix1.getValues()[i].length != matrix2.getValues()[i].length) {
                    System.out.println("errorrrr2");
                    return false;
                }
        return true;
    }

    public static boolean prodValidation() {
        if (matrix1.getColumnNo() != matrix2.getRowNo()) {
            System.out.println("errorrrrr1");
            return false;
        }
        return true;
    }

    public static Matrix execThreads1(Matrix result) throws InterruptedException, ExecutionException {
        List<ThreadWorker> threadList = new ArrayList<>();
        ThreadWorker[] threads = new ThreadWorker[THREADSNO];

        for (int i = 0; i < THREADSNO; i++) {
            threads[i] = new ThreadWorker(matrix1, matrix2, result);
        }


        int currentindex = 0;
        for (int i = 0; i < matrix1.getRowNo(); i++)
            for (int j = 0; j < matrix2.getColumnNo(); j++) {
                threads[currentindex].addIndexes(i, j);
                currentindex++;
                if (currentindex == THREADSNO)
                    currentindex = 0;
            }

        for (Thread thread : threads)
            thread.start();

        for (Thread thread : threads)
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
/*        for(int i=0;i<matrix1.getRowNo();i++)
            for(int j=0;j<matrix2.getColumnNo();j++)
                result.setValues(i,j,futures.get(i*matrix1.getRowNo()+j).get());*/
        return result;

    }

    public static Matrix execThreads2(Matrix result) throws InterruptedException, ExecutionException {
        List<WorkerCallable> threads = new ArrayList<>();

        for (int i = 0; i < matrix1.getRowNo(); i++)
            for (int j = 0; j < matrix2.getColumnNo(); j++) {
                threads.add(new WorkerCallable(matrix1, matrix2, i, j));
            }

        List<Future<Integer>> futures = executorService.invokeAll(threads);

        for (int i = 0; i < matrix1.getRowNo(); i++)
            for (int j = 0; j < matrix2.getColumnNo(); j++)
                result.setValues(i, j, futures.get(i * matrix1.getRowNo() + j).get());

        executorService.shutdown();

        return result;


    }

    public static Matrix execThreads3(Matrix result) throws InterruptedException, ExecutionException {
        List<ExecutorWorker> threads = new ArrayList<>();

        for (int i = 0; i < matrix1.getRowNo(); i++)
            for (int j = 0; j < matrix2.getColumnNo(); j++) {
                threads.add(new ExecutorWorker(matrix1, matrix2, i, j, result));
            }

        //List<Future<Integer>> futures = executorService.invokeAll(threads);

        /*
        for(int i=0;i<matrix1.getRowNo();i++)
            for(int j=0;j<matrix2.getColumnNo();j++)
                result.setValues(i,j,futures.get(i*matrix1.getRowNo()+j).get());
                */

        for (ExecutorWorker worker : threads)
            executorService.execute(worker);

        executorService.shutdown();

        try {
            executorService.awaitTermination(1000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

}

