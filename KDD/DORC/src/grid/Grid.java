package grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Grid {

    public HashMap<Integer, Cell> grid;
    private double cellWidth;
    public int nrows;
    public static int ncols;

    // TODO: maintain a neighbor list for each point?

    public Grid(double eps) {
        cellWidth = eps / Math.sqrt(2);
        grid = new HashMap<>();
    }

    public Grid(int rows, int cols, double eps) {
        cellWidth = eps / Math.sqrt(2);
        nrows = rows;
        ncols = cols;
        grid = new HashMap<>();
    }

    /**
     *Calculates the list of neighbor Cells of a given Cell.
     * @param i
     * @param j
     * @return a list of neighbor Cells of current cell
     */
    public List<Cell> calculateNeighboringCells(int i, int j) {
        List<Cell> nCells = new ArrayList<>();

        for (int row = i - 2; row <= i + 2; row++) {    // traversing rows near this cell
            boolean rowInBounds = (row >= 0) && (row < nrows);  // row index must in grid
            if (rowInBounds) {
                for (int col = j - 2; col <= j + 2; col++) {    // traversing cols near this cell
                    boolean colInBounds = (col >= 0) && (col < ncols);  // col index must in grid
                    if (colInBounds) {
                        boolean isCorner, isCenter;
                        //isCenter = row == i && col == j;
                        isCorner = (row == i - 2 && col == j - 2) || (row == i + 2 && col == j + 2) || (row == i + 2 && col == j - 2) || (row == i - 2 && col == j + 2);
                        if (!isCorner) {
                            // if it's not in corner, according to fig4
                            // it is not a neighboring cell of this cell
                            if (this.hasCell(row, col)) {
                                nCells.add(this.getCell(row, col));
                            }
                        }
                    }
                }
            }
        }
        return nCells;
    }

    /**
     *Calculates the nearest noise cell of a cell
     * @param i
     * @param j
     * @return a cell's i and j
     */
    public int[] calculateNearestNoiseCell(int i, int j) {
//        Cell nCell = null;
        int mv=1000000000;
        int temp=1000000000;//Integer.MAX_VALUE;
        int tempJ=1000000000;//Integer.MAX_VALUE;
        int ti=10000;//Integer.MAX_VALUE;
        int tj=10000;//Integer.MAX_VALUE;
        // start from the target and check right
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
        for (int row = i; row >= i-ti && row >=0; row--) {//i-Math.abs(t-i)
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


    /**
     *Calculates the list of neighbor Cells of a given Cell.
     * @param i
     * @param j
     * @return a list of neighbor Cells of current cell
     */
    public int[] calculateNearestNonNoiseCell(int i, int j) {
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
        int key = i * (ncols + 1) + j;
        return grid.containsKey(key);
    }

    public void setPointInCell(int x, int y, Point newPoint) {
        int key = x * (ncols + 1) + y;
        if (!grid.containsKey(key)) {
            grid.put(key, new Cell());
            grid.get(key).addPoint(newPoint);
        } else {
            grid.get(key).addPoint(newPoint);
        }
    }



    public Cell getCell(int x, int y) {
        return grid.get(x * (ncols + 1) + y);   // key = x*(ncols+1) + y
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
