import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {


    private static Matrix matrix1;
    private static Matrix matrix2;
    private static Matrix matrix3;


    public static void main(String[] args) {

        matrix1 = new Matrix(5, 3, true);
        matrix2 = new Matrix(3, 4, true);
        matrix3 = new Matrix(4, 2, true);

        System.out.println("Matrix1:");
        printMatrix(matrix1.getRowNo(), matrix1.getColumnNo(), matrix1);
        System.out.println();
        System.out.println("Matrix2:");
        printMatrix(matrix2.getRowNo(), matrix2.getColumnNo(), matrix2);
        System.out.println();
        System.out.println("Matrix3:");
        printMatrix(matrix3.getRowNo(), matrix3.getColumnNo(), matrix3);
        Matrix result1seq;
        Matrix result2seq;
        Matrix result1threads;
        Matrix result2threads;


        if (prodValidation()) {
            result1seq = new Matrix(matrix1.getRowNo(), matrix2.getColumnNo(), false);
            result2seq = new Matrix(result1seq.getRowNo(), matrix3.getColumnNo(), false);
            result1threads = new Matrix(matrix1.getRowNo(), matrix2.getColumnNo(), false);
            result2threads = new Matrix(result1seq.getRowNo(), matrix3.getColumnNo(), false);

            execSeq(result1seq, result2seq);


            List<ThreadWorker> workers1 = new ArrayList<>();
            List<ThreadWorker> workers2 = new ArrayList<>();
            for (int i=0; i<matrix1.getRowNo();i++){
                Lock lock = new ReentrantLock();
                Condition ready = lock.newCondition();
                ThreadWorker worker1 = new ThreadWorker(i,matrix1, matrix2, matrix3, result1threads,result2threads,false, lock, ready);
                ThreadWorker worker2 = new ThreadWorker(i,matrix1, matrix2, matrix3, result1threads,result2threads,true, lock, ready);
                workers1.add(worker1);
                workers2.add(worker2);
            }

            for(ThreadWorker worker1: workers1){
                worker1.start();
            }
            for(ThreadWorker worker2: workers2){
                worker2.start();
            }

            try {
                for(ThreadWorker worker1: workers1){
                    worker1.join();
                }
                for(ThreadWorker worker2: workers2){
                    worker2.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("Partial result:");
            printMatrix(result1threads.getRowNo(),result1threads.getColumnNo(),result1threads);


            System.out.println();
            System.out.println("Resulted matrix:");
            printMatrix(result2threads.getRowNo(),result2threads.getColumnNo(),result2threads);
        }
    }



    public static void printMatrix(int x, int y, Matrix a) {
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++)
                System.out.print(a.getValues()[i][j] + " ");
            System.out.println();
        }
    }



    public static boolean prodValidation() {
        if (matrix1.getColumnNo() != matrix2.getRowNo()) {
            System.out.println("errorrrrr1");
            return false;
        }
        if (matrix2.getColumnNo() != matrix3.getRowNo()){
            System.out.println("errorrrrr1");
            return false;
        }

        return true;
    }


    public static void execSeq(Matrix result1, Matrix result2){

        for (int i=0; i<matrix1.getRowNo();i++)
            for(int j=0; j<matrix2.getColumnNo(); j++)
                result1.prod(i,j,matrix1,matrix2);

        System.out.println();
        System.out.println("First multiplication result sequential:");
        printMatrix(result1.getRowNo(),result1.getColumnNo(),result1);

        for (int i=0; i<result1.getRowNo();i++)
            for(int j=0; j<matrix3.getColumnNo(); j++)
                result2.prod(i,j,result1,matrix3);

        System.out.println();
        System.out.println("Second multiplication result sequential:");
        printMatrix(result2.getRowNo(),result2.getColumnNo(),result2);

        System.out.println();


    }


}

