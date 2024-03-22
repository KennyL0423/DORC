package grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

public class Grid {

    public HashMap<Integer, Cell> grid;
    public HashMap<Cell, Integer> cell_index;
    private double cellWidth;
    public int nrows;
    public static int ncols;

    // TODO: maintain a neighbor list for each point?

    public Grid(double eps) {
        cellWidth = eps / Math.sqrt(2);
        grid = new HashMap<>();
        cell_index = new HashMap<>();
    }

    public Grid(int rows, int cols, double eps) {
        cellWidth = eps / Math.sqrt(2);
        nrows = rows;
        ncols = cols;
        grid = new HashMap<>();
        cell_index = new HashMap<>();
    }

    public int getNrows() {
        return nrows;
    }
    public int getNcols(){
        return ncols;
    }

    /**
     *Calculates the list of neighbor Cells of a given Cell.
     * @param i
     * @param j
     * @return a list of neighbor Cells of current cell
     */
    public List<Cell> calculateNeighboringCells(int i, int j) {
        List<Cell> nCells = new ArrayList<>();
        for (int row = i - 2; row <= i + 2; row++) {
            for (int col = j - 2; col <= j + 2; col++) {
                // Check if the current cell is within grid bounds
                if (row >= 0 && row < nrows && col >= 0 && col < ncols) {
                    // Exclude the cell itself and corner cells
                    if (!(row == i && col == j) && !(Math.abs(row - i) == 2 && Math.abs(col - j) == 2)) {
                        // Add the cell if it exists
                        if (this.hasCell(row, col)) {
                            nCells.add(this.getCell(row, col));
                        }
                    }
                }
            }
        }
        return nCells;
    }

    public int[] calculateNearNoiseCell(int i, int j){
        boolean[][] visited = new boolean[nrows][ncols];
        Queue<Integer> queue = new LinkedList<>();
        int key = i * ncols + j;
        queue.add(key);
        visited[i][j] = true;

        int[] di = {1, -1, 0, 0};
        int[] dj = {0, 0, 1, -1};
        int nc[] = new int [2];
        while(!queue.isEmpty()) {
            int curKey = queue.poll();
            int curI = curKey / ncols;
            int curJ = curKey % ncols;
            if (this.hasCell(curI, curJ) && this.getCell(curI, curJ).getNoiseList()) {
                nc[0] = curI;
                nc[1] = curJ;
                return nc;
            }
            for (int k = 0; k < 4; k++) {
                int newI = curI + di[k];
                int newJ = curJ + dj[k];
                if (newI >= 0 && newI < nrows && newJ >= 0 && newJ < ncols && !visited[newI][newJ]) {
                    visited[newI][newJ] = true;
                    queue.add(newI * ncols + newJ);
                }
            }
        }
        nc[0] = -1;
        nc[1] = -1;
        return nc;
    }

    public int[] calculateNearestNoiseCell(int i, int j) {
        // 使用队列支持BFS
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[nrows][ncols]; // 记录已访问的单元格

        queue.offer(new int[]{i, j}); // 初始单元格入队
        visited[i][j] = true; // 标记为已访问

        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int curRow = cell[0], curCol = cell[1];

            // 检查当前单元格是否含有噪声点
            if (this.hasCell(curRow, curCol) && this.getCell(curRow, curCol).getNoiseList()) {
                return new int[]{curRow, curCol}; // 找到最近的含噪声单元格
            }

            // 遍历当前单元格的所有邻居
            int[] dRow = {-1, 1, 0, 0};
            int[] dCol = {0, 0, -1, 1};
            for (int k = 0; k < 4; k++) {
                int newRow = curRow + dRow[k], newCol = curCol + dCol[k];
                // 检查新单元格是否在网格范围内且未被访问
                if (newRow >= 0 && newRow < nrows && newCol >= 0 && newCol < ncols && !visited[newRow][newCol]) {
                    queue.offer(new int[]{newRow, newCol}); // 新单元格入队
                    visited[newRow][newCol] = true; // 标记为已访问
                }
            }
        }

        return new int[]{-1, -1}; // 如果网格中没有噪声点，返回[-1, -1]
    }

    /**
     *Calculates the nearest noise cell of a cell
     * @param i
     * @param j
     * @return a cell's i and j
     */
    public int[] oldcalculateNearestNoiseCell(int i, int j) {
//        Cell nCell = null;
        int mv=1000000000;
        int temp=1000000000;//Integer.MAX_VALUE;
        int tempJ=1000000000;//Integer.MAX_VALUE;
        int ti=10000;//Integer.MAX_VALUE;
        int tj=10000;//Integer.MAX_VALUE;
        // start from the target and check right
        // TODO: related with nrows and ncols
        for (int row = i; row <= nrows; row++) {
            // then check for the bottom
            for (int col = j; col <= ncols; col++) {
                // if the found noise cell is closer, then break
                if((ti*ti+tj*tj)<=(Math.pow((col-j), 2)+(Math.pow((row-i), 2))))
                {
                    break;
                }
                // if there exists cell's corresponding key
                if(this.hasCell(row, col))
                {
                    // if this cell has noise point
                    if(this.getCell(row, col).getNoiseList())
                    {
                        // tempJ and temp record the row and col
                        tempJ=col;
                        temp=row;
                        // t is more like delta, measures the difference of nearest noise col and j
                        tj=col-j;
                        ti=row-i;
                        break;
                    }
                }
            }
            // check for the top, only needs to check for the cols within delta j to j
            for (int col = j; col >= j-(tempJ-j) && col>=0; col--) {
                if((ti*ti+tj*tj)<=(Math.pow((col-j), 2)+(Math.pow((row-i), 2))))
                {
                    break;
                }
                if(this.hasCell(row, col))
                {
                    if(this.getCell(row, col).getNoiseList())
                    {
                        tempJ=col;
                        temp=row;
                        tj=j-col;
                        ti=row-i;
                        break;
                    }
                }
            }
        }
        // check for the left
        for (int row = i; row >= i-Math.abs(ti) && row >=0; row--) {//i-Math.abs(t-i)
            // check for the bottom
            for (int col = j; col <= ncols; col++) {//j-Math.abs(t-j)
                if((ti*ti+tj*tj)<=(Math.pow((col-j), 2)+(Math.pow((row-i), 2))))
                {
                    break;
                }
                if(this.hasCell(row, col))
                {
                    if(this.getCell(row, col).getNoiseList())
                    {
                        temp=row;
                        tempJ=col;
                        tj=col-j;
                        ti=i-row;

                        break;
                    }
                }
            }
            // check for the top
            for (int col = j; col >=j-Math.abs(tempJ-j) && col>=0; col--) {//j-(tempJ-j)
                if((ti*ti+tj*tj)<=(Math.pow((col-j), 2)+(Math.pow((row-i), 2))))
                {
                    break;
                }
                if(this.hasCell(row, col))
                {
                    if(this.getCell(row, col).getNoiseList())
                    {
                        tempJ=col;
                        temp=row;
                        tj=j-col;
                        ti=i-row;
                        break;
                    }
                }
            }
        }
        int [] nc= new int[2];//{temp, tempJ};
        nc[0]=temp;
        nc[1]=tempJ;
        return nc;
    }

    public int[] calculateNearestNonNoiseCell(int i, int j) {
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[nrows][ncols]; // Track visited cells

        // Initialize with the starting cell
        queue.offer(new int[]{i, j});
        visited[i][j] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int curRow = current[0], curCol = current[1];

            // Check if the current cell is a non-noise cell
            if (this.hasCell(curRow, curCol) && this.getCell(curRow, curCol).getNonNoiseList()) {
                return new int[]{curRow, curCol}; // Return the position of the found cell
            }

            // Directions to move: up, down, left, right
            int[] dRow = {-1, 1, 0, 0};
            int[] dCol = {0, 0, -1, 1};

            for (int k = 0; k < 4; k++) {
                int newRow = curRow + dRow[k];
                int newCol = curCol + dCol[k];

                // Check bounds and whether the cell has been visited
                if (newRow >= 0 && newRow < nrows && newCol >= 0 && newCol < ncols && !visited[newRow][newCol]) {
                    queue.offer(new int[]{newRow, newCol}); // Add to queue for exploration
                    visited[newRow][newCol] = true;
                }
            }
        }

        // Return [-1, -1] if no non-noise cell is found
        return new int[]{-1, -1};
    }

    /**
     *Calculates the list of neighbor Cells of a given Cell.
     * @param i
     * @param j
     * @return a list of neighbor Cells of current cell
     */
    public int[] oldcalculateNearestNonNoiseCell(int i, int j) {
        boolean[][] visited = new boolean[nrows][ncols];
        Queue<Integer> queue = new LinkedList<>();
        int key = i * (ncols + 1) + j;
        queue.add(key);
        visited[i][j] = true;

        int[] di = {1, -1, 0, 0};
        int[] dj = {0, 0, 1, -1};
        int nc[] = new int [2];
        while(!queue.isEmpty()) {
            int cur_key = queue.poll();
            int cur_i = cur_key/(ncols+1); // rownum
            int cur_j = cur_key%(ncols+1); // colnum
            if(this.hasCell(cur_i, cur_j)){
                if(this.getCell(cur_i, cur_j).getNonNoiseList())
                {
                    nc[0] = cur_i;
                    nc[1] = cur_j;
                    return nc;
                }
            }
            for (int k=0; k<4; k++){
                int new_i = cur_i + di[k];
                int new_j = cur_j + dj[k];
                if(new_i>=0 && new_i<nrows && new_j>=0 && new_j<ncols && !visited[new_i][new_j]){
                    int new_key = new_i * (ncols + 1) + new_j;
                    visited[new_i][new_j] = true;
                    queue.add(new_key);
                }
            }
        }
        nc[0] = -1;
        nc[1] = -1;
        return nc;
    }
    public int[] calculateNearNonNoiseCell(int i, int j) {
//      similar to calculateNearestNoiseCell
//      Cell nCell = null;
        int mv=1000000000;
        int temp=1000000000;//Integer.MAX_VALUE;
        int tempJ=1000000000;//Integer.MAX_VALUE;
        int ti=10000;//Integer.MAX_VALUE;
        int tj=10000;//Integer.MAX_VALUE;
        for (int row = i; row <= nrows; row++) {
            for (int col = j; col <= ncols; col++) {
                if((ti*ti+tj*tj)<=(Math.pow((col-j), 2)+(Math.pow((row-i), 2))))
                {
                    break;
                }
                if(this.hasCell(row, col))
                    if(this.getCell(row, col).getNonNoiseList())
                    {
                        tempJ=col;
                        temp=row;
                        tj=col-j;
                        ti=row-i;
                        break;
                    }
            }
            for (int col = j; col >= j-Math.abs(tempJ-j) && col>=0; col--) {
                if((ti*ti+tj*tj)<=(Math.pow((col-j), 2)+(Math.pow((row-i), 2))))
                {
                    break;
                }
                if(this.hasCell(row, col))
                    if(this.getCell(row, col).getNonNoiseList())
                    {
                        tempJ=col;
                        temp=row;
                        tj=j-col;
                        ti=row-i;
                        break;
                    }
            }
        }
        for (int row = i; row >=0; row--) {//i-Math.abs(t-i)
            for (int col = j; col <= ncols; col++) {//j-Math.abs(t-j)
                if((ti*ti+tj*tj)<=(Math.pow((col-j), 2)+(Math.pow((row-i), 2))))
                {
                    break;
                }
                if(this.hasCell(row, col))
                    if(this.getCell(row, col).getNonNoiseList())
                    {
                        temp=row;
                        tempJ=col;
                        tj=col-j;
                        ti=i-row;
                        break;
                    }
            }
            for (int col = j; col >=j-Math.abs(tempJ-j) && col>=0; col--) {//j-(tempJ-j)
                if((ti*ti+tj*tj)<=(Math.pow((col-j), 2)+(Math.pow((row-i), 2))))
                {
                    break;
                }
                if(this.hasCell(row, col))
                    if(this.getCell(row, col).getNonNoiseList())
                    {
                        tempJ=col;
                        temp=row;
                        tj=j-col;
                        ti=i-row;
                        break;
                    }

            }
        }
        int [] nc= new int[2];//{temp, tempJ};
        nc[0]=temp;
        nc[1]=tempJ;
        return nc;
    }

    public boolean hasCell(int i, int j) {
        int key = i * ncols + j; // Corrected key calculation
        return grid.containsKey(key);
    }

    public void setPointInCell(int x, int y, Point newPoint) {
        int key = x * ncols + y; // Corrected key calculation
        grid.putIfAbsent(key, new Cell(key)); // Use putIfAbsent to simplify the logic
        grid.get(key).addPoint(newPoint);
    }

    public Cell getCell(int x, int y) {
        return grid.get(x * ncols + y); // Corrected key calculation
    }

    public Cell getCell(int x) {
        return grid.get(x);
    }

    public void setCell(int x, int y, Cell cell) {
        grid.put(x * (ncols + 1) + y, cell);
    }

    public int getLength() {
        return grid.size();
    }

    public int getColLength(int i) {
        return ncols;
    }

    public Cell getCellbyKey(int key) {
        return grid.get(key);
    }

    public double getCellWidth() {
        return cellWidth;
    }

}
