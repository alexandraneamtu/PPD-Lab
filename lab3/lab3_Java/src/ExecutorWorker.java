
import java.util.concurrent.Callable;

public class ExecutorWorker implements Runnable{

    private Matrix matrix1;
    private Matrix matrix2;
    private Tuple indexes;
    private Matrix result;

    public ExecutorWorker(Matrix matrix1, Matrix matrix2, int i, int j, Matrix result) {
        this.matrix1 = matrix1;
        this.matrix2 = matrix2;
        this.indexes = new Tuple(i,j);
        this.result = result;
    }


    @Override
    public void run() {
        //addition of 2 matrices
        //result.sum(indexes.getI(),indexes.getJ(),matrix1,matrix2);


        //multiplication of 2 matrices
        result.prod(indexes.getI(),indexes.getJ(),matrix1,matrix2);
    }
}
