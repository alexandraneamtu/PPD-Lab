import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class WorkerCallable implements Callable<Integer>{
    private Matrix matrix1;
    private Matrix matrix2;
    private Tuple indexes;

    public WorkerCallable(Matrix matrix1, Matrix matrix2, int i,int j) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.indexes = new Tuple(i,j);
    }

    @Override
    public Integer call(){

        //addition of 2 matrices
        //return matrix1.sum2(indexes.getI(),indexes.getJ(),matrix1,matrix2);


        //multiplication of 2 matrices
        return matrix1.prod2(indexes.getI(),indexes.getJ(),matrix1,matrix2);

    }
}
