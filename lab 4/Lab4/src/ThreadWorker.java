import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class ThreadWorker extends Thread {

    Matrix matrix1;
    Matrix matrix2;
    Matrix matrix3;
    Matrix result1;
    Matrix result2;
    int i;
    private Lock lock;
    private Condition ready;
    private boolean isPartial;

    public ThreadWorker(int i,Matrix matrix1, Matrix matrix2, Matrix matrix3, Matrix result1, Matrix result2, boolean isPartial, Lock lock, Condition condition) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.matrix3 = matrix3;
        this.result1 = result1;
        this.result2 = result2;
        this.i = i;
        this.lock = lock;
        this.isPartial = isPartial;
        this.ready = condition;
    }


    @Override
    public void run(){

        try {
            if(isPartial){
                lock.lock();
                result1.prod1(i,matrix1,matrix2);
                ready.signal();
                lock.unlock();
            }
            else{
                lock.lock();
                while(!isRowReady(i))
                    ready.await();
                result2.prod1(i,result1,matrix3);
                lock.unlock();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //result.prod1(i,matrix1,matrix2,threadno);
    }

    private boolean isRowReady(int linie){
        for(int col = 0; col <result1.getColumnNo(); col++) {
            if (result1.getValues()[linie][col]== 0){
                return false;
            }
        }
        return true;
    }
}
