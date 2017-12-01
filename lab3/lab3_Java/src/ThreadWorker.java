import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThreadWorker extends Thread {

    Matrix matrix1;
    Matrix matrix2;
    Matrix result;
    List<Tuple> indexes;

    public ThreadWorker(Matrix matrix1, Matrix matrix2, Matrix result) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.result = result;
        this.indexes = new ArrayList<>();
    }

    public void addIndexes(int i, int j)
    {
        indexes.add(new Tuple(i,j));
    }

    @Override
    public void run(){

        //addition of 2 matrices

        /*for (Tuple i: indexes)
            result.sum(i.getI(),i.getJ(),matrix1,matrix2);*/


        //multiplication of 2 matrices
        for (Tuple i: indexes)
            result.prod(i.getI(),i.getJ(),matrix1,matrix2);

    }
}
