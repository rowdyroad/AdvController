package Streamer;

import java.util.Vector;

/**
 * @author SVolkov
 *         Date: 26 жовт 2010
 *         Time: 16:28:47
 */
public class DTW {

    private double pointDistance(int i, int j, Vector<double[]> doublesOne, Vector<double[]> doublesTwo) {
        double diff = 0;
        for (int k = 0; k < doublesOne.get(i).length; k++) {
            diff += ((doublesOne.get(i)[k] - doublesTwo.get(j)[k]) * (doublesOne.get(i)[k] - doublesTwo.get(j)[k]));
        }
        return diff;
    }

    private double distance2Similarity(double x) {
        return (1.0 - (x / (1 + x)));
    }

    
    public double measure(double[] doublesOne, double[] doublesTwo)
    {
    	Vector<double[]> a = new Vector<double[]>();
    	a.add(doublesOne);
    	Vector<double[]> b = new Vector<double[]>();
    	b.add(doublesTwo);
    	return measure(a,b);
    }
    public double measure(Vector<double[]> doublesOne, Vector<double[]> doublesTwo) {

        int i, j;

        /** Build a point-to-point distance matrix */
        double[][] dP2P = new double[doublesOne.size()][doublesTwo.size()];
        for (i = 0; i < doublesOne.size(); i++) {
            for (j = 0; j < doublesTwo.size(); j++) {
                dP2P[i][j] = pointDistance(i, j, doublesOne, doublesTwo);
            }
        }

        /** Check for some special cases due to ultra short time series */
        if (doublesOne.size() == 0 || doublesTwo.size() == 0) {
            return Double.NaN;
        }

        if (doublesOne.size() == 1 && doublesTwo.size() == 1) {
            return distance2Similarity(Math.sqrt(dP2P[0][0]));
        }

        /**
         * Build the optimal distance matrix using a dynamic programming
         * approach
         */
        double[][] D = new double[doublesOne.size()][doublesTwo.size()];

        D[0][0] = dP2P[0][0]; // Starting point

        for (i = 1; i < doublesOne.size(); i++) { // Fill the first column of our
            // distance matrix with optimal
            // values
            D[i][0] = dP2P[i][0] + D[i - 1][0];
        }

        if (doublesTwo.size() == 1) { // doublesTwo is a point
            double sum = 0;
            for (i = 0; i < doublesOne.size(); i++) {
                sum += D[i][0];
            }
            return distance2Similarity(Math.sqrt(sum) / doublesOne.size());
        }

        for (j = 1; j < doublesTwo.size(); j++) { // Fill the first row of our
            // distance matrix with optimal
            // values
            D[0][j] = dP2P[0][j] + D[0][j - 1];
        }

        if (doublesOne.size() == 1) { // doublesOne is a point
            double sum = 0;
            for (j = 0; j < doublesTwo.size(); j++) {
                sum += D[0][j];
            }
            return distance2Similarity(Math.sqrt(sum) / doublesTwo.size());
        }

        for (i = 1; i < doublesOne.size(); i++) { // Fill the rest
            for (j = 1; j < doublesTwo.size(); j++) {
                double[] steps = {D[i - 1][j - 1], D[i - 1][j], D[i][j - 1]};
                double min = Math.min(steps[0], Math.min(steps[1], steps[2]));
                D[i][j] = dP2P[i][j] + min;
            }
        }

        /**
         * Calculate the distance between the two time series through optimal
         * alignment.
         */
        i = doublesOne.size() - 1;
        j = doublesTwo.size() - 1;
        int k = 1;
        double dist = D[i][j];

        while (i + j > 2) {
            if (i == 0) {
                j--;
            } else if (j == 0) {
                i--;
            } else {
                double[] steps = {D[i - 1][j - 1], D[i - 1][j], D[i][j - 1]};
                double min = Math.min(steps[0], Math.min(steps[1], steps[2]));

                if (min == steps[0]) {
                    i--;
                    j--;
                } else if (min == steps[1]) {
                    i--;
                } else if (min == steps[2]) {
                    j--;
                }
            }
            k++;
         //   dist += D[i][j];
        }

        return distance2Similarity(Math.sqrt(dist) / k);
    }

}
