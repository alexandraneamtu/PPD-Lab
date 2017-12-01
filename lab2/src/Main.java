import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static int THREADSNO = 1;

    private static Matrix matrix1;
    private static Matrix matrix2;
    //static int[][] resultmatrix;
    private static ThreadWorker[] threads = new ThreadWorker[THREADSNO];



    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();


        matrix1 = new Matrix(5,5,true);
        matrix2 = new Matrix(5,5,true);

        printMatrix(matrix1.getRowNo(),matrix1.getColumnNo(),matrix1);
        System.out.println();
        printMatrix(matrix2.getRowNo(),matrix2.getColumnNo(),matrix2);
        Matrix result;



        //sum
        if(sumValidation()==true){
            result = new Matrix(matrix1.getRowNo(),matrix1.getColumnNo(),false);
            result = execThreads(result);}
        else
            return;


        //multiplication
//        if(prodValidation()==true){
//            result = new Matrix(matrix1.getRowNo(),matrix2.getColumnNo(),false);
//            result = execThreads(result);}
//        else
//            return;

        System.out.println();

        printMatrix(result.getRowNo(),result.getColumnNo(),result);
        long endTime = System.currentTimeMillis();
        double time = (endTime - startTime);
        System.out.println("Execution time:"+time/1000);

    }

    public static void printMatrix(int x, int y, Matrix a){
        for(int i=0;i<x;i++) {
            for (int j = 0; j < y; j++)
                System.out.print(a.getValues()[i][j] + " ");
            System.out.println();
        }
    }


    public static boolean sumValidation(){
        if(matrix1.getValues().length != matrix2.getValues().length) {
            System.out.println("errorrrrr1");
            return false;
        }
        else
            for(int i=0;i<matrix1.getRowNo();i++)
                if(matrix1.getValues()[i].length != matrix2.getValues()[i].length) {
                    System.out.println("errorrrr2");
                    return false;
                }
        return true;
    }

    public static boolean prodValidation(){
        if(matrix1.getColumnNo() != matrix2.getRowNo()) {
            System.out.println("errorrrrr1");
            return false;
        }
        return true;
    }

    public static Matrix execThreads(Matrix result){
        for(int i=0; i< THREADSNO; i++){
            threads[i] = new ThreadWorker(matrix1,matrix2,result);
        }


        int currentindex = 0;
        for(int i=0;i<matrix1.getRowNo();i++)
            for (int j = 0; j < matrix2.getColumnNo(); j++) {
                threads[currentindex].addIndexes(i,j);
                currentindex++;
                if(currentindex == THREADSNO)
                    currentindex = 0;
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

        return result;

    }




}

