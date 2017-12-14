import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class Main {

    private static int vertices = 9;
    private static int[][] graphmatrix;

    public static void main(String[] args) {
	        createGraph();
	        printGraph();
	        final Graph solution = new Graph();
            ExecutorService executorService = Executors.newFixedThreadPool(vertices);
            long startTime = System.nanoTime();
            for(int i = 0; i < vertices; i++) {
                Graph graph = new Graph();
                int istart = i;
                try {
                    executorService.execute(() ->
                    {
                        try {
                            graph.findCycle(graphmatrix, istart);
                        } catch (Exception e) {
                            //The shutdown() method does one thing: prevents clients to send more work to the executor
                            // service. This means all the existing tasks will still run to completion unless other
                            // actions are taken.
                            executorService.shutdown();
                            if (solution.getPath() == null || solution.getPath().length == 0) {
                                solution.setPath(graph.getPath());
                                solution.setVertices(vertices);
                            }
                        }

                    });
                } catch (RejectedExecutionException e) {
                    e.getCause();
                }
            }

            try{
                executorService.awaitTermination(100, TimeUnit.MICROSECONDS);
                long endTime = System.nanoTime();
                System.out.println("Execution time:" + (endTime-startTime));
                solution.printPath();
                System.exit(0);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
    }

    private static void printGraph() {
        System.out.print("  | ");
        for(int i=0;i<vertices;i++)
            System.out.print(i + " | ");
        System.out.println();
        System.out.print("----------------------------------------");
        System.out.println();
        for (int i = 0; i < vertices; i++){
            System.out.print(i + " | ");
            for (int j = 0; j < vertices; j++){

                System.out.print(graphmatrix[i][j] +"   ");
            }
            System.out.println();
           }
    }

    private static void createGraph() {
        graphmatrix = new int[vertices][vertices];
        for(int i=0; i<vertices;i++)
            for(int j=0;j<vertices;j++)
                graphmatrix[i][j] = new Random().nextInt(2);
    }
}
