package Streamer;

public class DTW {

    private float pointDistance(int i, int j, float[][]  doublesOne, float[][] doublesTwo) {
    	float diff = 0;
        for (int k = 0; k < doublesOne[i].length; k++) {
            diff += ((doublesOne[i][k] - doublesTwo[j][k]) * (doublesOne[i][k] - doublesTwo[j][k]));
        }
        return diff;
    }

    private float distance2Similarity(float x) {
        return 1.0f - (x / (1 + x));
    }

    
    
    public float measure(float[][] doublesOne, float[][] doublesTwo) {

        int i, j;

        /** Build a point-to-point distance matrix */
        float[][] dP2P = new float[doublesOne.length][doublesTwo.length];
        for (i = 0; i < doublesOne.length; i++) {
            for (j = 0; j < doublesTwo.length; j++) {
                dP2P[i][j] = pointDistance(i, j, doublesOne, doublesTwo);
            }
        }

        /** Check for some special cases due to ultra short time series */
        if (doublesOne.length == 0 || doublesTwo.length == 0) {
            return Float.NaN;
        }

        if (doublesOne.length == 1 && doublesTwo.length == 1) {
            return distance2Similarity((float)Math.sqrt(dP2P[0][0]));
        }

        /**
         * Build the optimal distance matrix using a dynamic programming
         * approach
         */
        float[][] D = new float[doublesOne.length][doublesTwo.length];

        D[0][0] = dP2P[0][0]; // Starting point

        for (i = 1; i < doublesOne.length; i++) { // Fill the first column of our
            // distance matrix with optimal
            // values
            D[i][0] = dP2P[i][0] + D[i - 1][0];
        }

        if (doublesTwo.length == 1) { // doublesTwo is a point
            float sum = 0;
            for (i = 0; i < doublesOne.length; i++) {
                sum += D[i][0];
            }
            return distance2Similarity((float)Math.sqrt(sum) / doublesOne.length);
        }

        for (j = 1; j < doublesTwo.length; j++) { // Fill the first row of our
            // distance matrix with optimal
            // values
            D[0][j] = dP2P[0][j] + D[0][j - 1];
        }

        if (doublesOne.length == 1) { // doublesOne is a point
        	float sum = 0;
            for (j = 0; j < doublesTwo.length; j++) {
                sum += D[0][j];
            }
            return distance2Similarity((float)Math.sqrt(sum) / doublesTwo.length);
        }

        for (i = 1; i < doublesOne.length; i++) { // Fill the rest
            for (j = 1; j < doublesTwo.length; j++) {
                float[] steps = {D[i - 1][j - 1], D[i - 1][j], D[i][j - 1]};
                float min = Math.min(steps[0], Math.min(steps[1], steps[2]));
                D[i][j] = dP2P[i][j] + min;
            }
        }

        /**
         * Calculate the distance between the two time series through optimal
         * alignment.
         */
        i = doublesOne.length - 1;
        j = doublesTwo.length - 1;
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

        return distance2Similarity((float)Math.sqrt(dist) / k);
    }

}
