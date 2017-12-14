public class Graph {
    private int vertices;
    private int[] path;
    private int start;
    private int pathCount;
    private int[][] graphmatrix;

    public void findCycle(int[][] gm,int start) throws Exception {
        vertices = gm.length;
        path = new int[vertices];
        for(int i=0;i<path.length;i++)
            path[i] = -1;
        graphmatrix = gm;
        this.start = start;
        path[0] = start;
        pathCount = 1;
        findPath(start);
        System.out.println("Path from "+ start +" found no solution");
    }

    public int getVertices() {
        return vertices;
    }

    public void setVertices(int vertices) {
        this.vertices = vertices;
    }

    public int[] getPath() {
        return path;
    }


    public void setPath(int[] path) {
        this.path = path;
    }

    private void findPath(int start) throws Exception {


        if(graphmatrix[start][this.start] == 1 && pathCount==vertices){
            throw new Exception("Solution found"); }

        if(pathCount == vertices)
            return;

        for(int i=0;i<vertices;i++){
            if(graphmatrix[start][i]==1){
                path[pathCount++] = i;
                graphmatrix[start][i] = 0;
                graphmatrix[i][start] = 0;
                if(!isPresent(i))
                    findPath(i);
                graphmatrix[start][i] = 1;
                path[--pathCount] = -1;
            }
        }
    }

    private boolean isPresent(int i) {
        for(int j = 0; j<pathCount-1 ;j++){
            if(path[j] == i)
                return true;
        }
        return false;
    }

    public void printPath()
    {
        //System.out.println(path);
        if(path == null){
            System.out.println("No cycles!!");
        }
        else {
            System.out.print("\nPath : ");
            for (int i = 0; i < path.length; i++)
                System.out.print(path[i] + " ");
            System.out.println();
        }
    }
}
