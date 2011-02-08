package Calculation.math;

import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.OutputStream;
import java.io.InputStream;
import javax.xml.stream.*;
/**
   Jama = Java Matrix class.
<P>
   The Java Matrix Class provides the fundamental operations of numerical
   linear algebra.  Various constructors create Matrices from two dimensional
   arrays of float precision floating point numbers.  Various "gets" and
   "sets" provide access to submatrices and matrix elements.  Several methods
   implement basic matrix arithmetic, including matrix addition and
   multiplication, matrix norms, and element-by-element array operations.
   Methods for reading and printing matrices are also included.  All the
   operations in this version of the Matrix Class involve real matrices.
   Complex matrices may be handled in a future version.
<P>
   Five fundamental matrix decompositions, which consist of pairs or triples
   of matrices, permutation vectors, and the like, produce results in five
   decomposition classes.  These decompositions are accessed by the Matrix
   class to compute solutions of simultaneous linear equations, determinants,
   inverses and other matrix functions.  The five decompositions are:
<P><UL>
   <LI>Cholesky Decomposition of symmetric, positive definite matrices.
   <LI>LU Decomposition of rectangular matrices.
   <LI>QR Decomposition of rectangular matrices.
   <LI>Singular Value Decomposition of rectangular matrices.
   <LI>Eigenvalue Decomposition of both symmetric and nonsymmetric square matrices.
</UL>
<DL>
<DT><B>Example of use:</B></DT>
<P>
<DD>Solve a linear system A x = b and compute the residual norm, ||b - A x||.
<P><PRE>
      float[][] vals = {{1.,2.,3},{4.,5.,6.},{7.,8.,10.}};
      Matrix A = new Matrix(vals);
      Matrix b = Matrix.random(3,1);
      Matrix x = A.solve(b);
      Matrix r = A.times(x).minus(b);
      float rnorm = r.normInf();
</PRE></DD>
</DL>

@author The MathWorks, Inc. and the National Institute of Standards and Technology.
@version 5 August 1998
*/

public class Matrix implements Cloneable, Serializable
{

/* ------------------------
   Class variables
 * ------------------------ */

   private static final long serialVersionUID = 1L;

/** Array for internal storage of elements.
   @serial internal array storage.
   */
   public float[][] A;

   /**Number of rows. @serial number of rows. */
   private int m;
   /**Number of columns. @serial number of columns. */
   private int n;

/* ------------------------
   Constructors
 * ------------------------ */

   /** Construct an m-by-n matrix of zeros.
   @param m    Number of rows.
   @param n    Number of colums.
   */

   public Matrix (int m, int n) {
      this.m = m;
      this.n = n;
      A = new float[m][n];
   }

   /** Construct an m-by-n constant matrix.
   @param m    Number of rows.
   @param n    Number of colums.
   @param s    Fill the matrix with this scalar value.
   */

   public Matrix (int m, int n, float s) {
      this.m = m;
      this.n = n;
      A = new float[m][n];
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            A[i][j] = s;
         }
      }
   }

   /** Construct a matrix from a 2-D array.
   @param A    Two-dimensional array of floats.
   @exception  IllegalArgumentException All rows must have the same length
   @see        #constructWithCopy
   */

   public Matrix (float[][] A) {
      m = A.length;
      n = A[0].length;
      for (int i = 0; i < m; i++) {
         if (A[i].length != n) {
            throw new IllegalArgumentException("All rows must have the same length.");
         }
      }
      this.A = A;
   }

   /** Construct a matrix quickly without checking arguments.
   @param A    Two-dimensional array of floats.
   @param m    Number of rows.
   @param n    Number of colums.
   */

   public Matrix (float[][] A, int m, int n) {
      this.A = A;
      this.m = m;
      this.n = n;
   }

   /** Construct a matrix from a one-dimensional packed array
   @param vals One-dimensional array of floats, packed by columns (ala Fortran).
   @param m    Number of rows.
   @exception  IllegalArgumentException Array length must be a multiple of m.
   */

   public Matrix (float vals[], int m) {
      this.m = m;
      n = (m != 0 ? vals.length/m : 0);
      if (m*n != vals.length) {
         throw new IllegalArgumentException("Array length must be a multiple of m.");
      }
      A = new float[m][n];
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            A[i][j] = vals[i+j*m];
         }
      }
   }

   /**
    * Construct a matrix from a Vector of float[]
    * @param rows	A Vector<float[]> containing float[] items. Each float[] item
    * is a row of the matrix to create. All float[] items must be of the same length.
    */
   public Matrix (Vector<float[]> rows, boolean clone){
	   m = rows.size();
	   n = rows.get(0).length;
	   A = new float[m][n];
	   if(clone){
		   for(int i=0; i<m; i++)
			   A[i] = rows.get(i).clone();
	   }
	   else{
		   for(int i=0; i<m; i++)
		   A[i] = rows.get(i);
	   }

	   // sanity check:
	   for(int i=0; i<m; i++)
		   if(A[i].length != n)
			   (new IllegalArgumentException("Length of row "+i+" is "+ A[i].length+". Should be "+n)).printStackTrace();
   }

/* ------------------------
   Public Methods
 * ------------------------ */

   /** Construct a matrix from a copy of a 2-D array.
   @param A    Two-dimensional array of floats.
   @exception  IllegalArgumentException All rows must have the same length
   */

   public static Matrix constructWithCopy(float[][] A) {
      int m = A.length;
      int n = A[0].length;
      Matrix X = new Matrix(m,n);
      float[][] C = X.getArray();
      for (int i = 0; i < m; i++) {
         if (A[i].length != n) {
            throw new IllegalArgumentException
               ("All rows must have the same length.");
         }
         for (int j = 0; j < n; j++) {
            C[i][j] = A[i][j];
         }
      }
      return X;
   }

   /** Make a deep copy of a matrix
   */

   public Matrix copy () {
      Matrix X = new Matrix(m,n);
      float[][] C = X.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            C[i][j] = A[i][j];
         }
      }
      return X;
   }

   /** Clone the Matrix object.
   */

   public Object clone () {
      return this.copy();
   }

   /** Access the internal two-dimensional array.
   @return     Pointer to the two-dimensional array of matrix elements.
   */

   public float[][] getArray () {
      return A;
   }

   /** Copy the internal two-dimensional array.
   @return     Two-dimensional array copy of matrix elements.
   */

   public float[][] getArrayCopy () {
      float[][] C = new float[m][n];
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            C[i][j] = A[i][j];
         }
      }
      return C;
   }

   /** Make a one-dimensional column packed copy of the internal array.
   @return     Matrix elements packed in a one-dimensional array by columns.
   */

   public float[] getColumnPackedCopy () {
      float[] vals = new float[m*n];
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            vals[i+j*m] = A[i][j];
         }
      }
      return vals;
   }

   /** Make a one-dimensional row packed copy of the internal array.
   @return     Matrix elements packed in a one-dimensional array by rows.
   */

   public float[] getRowPackedCopy () {
      float[] vals = new float[m*n];
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            vals[i*n+j] = A[i][j];
         }
      }
      return vals;
   }

   /** Get row dimension.
   @return     m, the number of rows.
   */

   public int getRowDimension () {
      return m;
   }

   public int getColumnDimension () {
      return n;
   }

   public float get (int i, int j) {
      return A[i][j];
   }


   public Matrix getMatrix (int i0, int i1, int j0, int j1) {
      Matrix X = new Matrix(i1-i0+1,j1-j0+1);
      float[][] B = X.getArray();
      try {
         for (int i = i0; i <= i1; i++) {
            for (int j = j0; j <= j1; j++) {
               B[i-i0][j-j0] = A[i][j];
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
      return X;
   }

   public Matrix getMatrix (int[] r, int[] c) {
      Matrix X = new Matrix(r.length,c.length);
      float[][] B = X.getArray();
      try {
         for (int i = 0; i < r.length; i++) {
            for (int j = 0; j < c.length; j++) {
               B[i][j] = A[r[i]][c[j]];
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
      return X;
   }


   public Matrix getMatrix (int i0, int i1, int[] c) {
      Matrix X = new Matrix(i1-i0+1,c.length);
      float[][] B = X.getArray();
      try {
         for (int i = i0; i <= i1; i++) {
            for (int j = 0; j < c.length; j++) {
               B[i-i0][j] = A[i][c[j]];
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
      return X;
   }

   public Matrix getMatrix (int[] r, int j0, int j1) {
      Matrix X = new Matrix(r.length,j1-j0+1);
      float[][] B = X.getArray();
      try {
         for (int i = 0; i < r.length; i++) {
            for (int j = j0; j <= j1; j++) {
               B[i][j-j0] = A[r[i]][j];
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
      return X;
   }

   public void set (int i, int j, float s) {
      A[i][j] = s;
   }

   public void setMatrix (int i0, int i1, int j0, int j1, Matrix X) {
      try {
         for (int i = i0; i <= i1; i++) {
            for (int j = j0; j <= j1; j++) {
               A[i][j] = X.get(i-i0,j-j0);
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
   }

   public void setMatrix (int[] r, int[] c, Matrix X) {
      try {
         for (int i = 0; i < r.length; i++) {
            for (int j = 0; j < c.length; j++) {
               A[r[i]][c[j]] = X.get(i,j);
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
   }

   public void setMatrix (int[] r, int j0, int j1, Matrix X) {
      try {
         for (int i = 0; i < r.length; i++) {
            for (int j = j0; j <= j1; j++) {
               A[r[i]][j] = X.get(i,j-j0);
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
   }

   public void setMatrix (int i0, int i1, int[] c, Matrix X) {
      try {
         for (int i = i0; i <= i1; i++) {
            for (int j = 0; j < c.length; j++) {
               A[i][c[j]] = X.get(i-i0,j);
            }
         }
      } catch(ArrayIndexOutOfBoundsException e) {
         throw new ArrayIndexOutOfBoundsException("Submatrix indices");
      }
   }

   /** Matrix transpose.
   @return    A'
   */

   public Matrix transpose () {
      Matrix X = new Matrix(n,m);
      float[][] C = X.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            C[j][i] = A[i][j];
         }
      }
      return X;
   }

   /** One norm
   @return    maximum column sum.
   */

   public float norm1 () {
      float f = 0;
      for (int j = 0; j < n; j++) {
         float s = 0;
         for (int i = 0; i < m; i++) {
            s += Math.abs(A[i][j]);
         }
         f = Math.max(f,s);
      }
      return f;
   }

   public float normInf () {
      float f = 0;
      for (int i = 0; i < m; i++) {
         float s = 0;
         for (int j = 0; j < n; j++) {
            s += Math.abs(A[i][j]);
         }
         f = Math.max(f,s);
      }
      return f;
   }

   public float normF () {
      float f = 0;
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            f = (float) Maths.hypot(f,A[i][j]);
         }
      }
      return f;
   }

   public Matrix uminus () {
      Matrix X = new Matrix(m,n);
      float[][] C = X.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            C[i][j] = -A[i][j];
         }
      }
      return X;
   }

   /** C = A + B
   @param B    another matrix
   @return     A + B
   */

   public Matrix plus (Matrix B) {
      checkMatrixDimensions(B);
      Matrix X = new Matrix(m,n);
      float[][] C = X.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            C[i][j] = A[i][j] + B.A[i][j];
         }
      }
      return X;
   }

   /** A = A + B
   @param B    another matrix
   @return     A + B
   */

   public Matrix plusEquals (Matrix B) {
      checkMatrixDimensions(B);
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            A[i][j] = A[i][j] + B.A[i][j];
         }
      }
      return this;
   }

   /** C = A - B
   @param B    another matrix
   @return     A - B
   */

   public Matrix minus (Matrix B) {
      checkMatrixDimensions(B);
      Matrix X = new Matrix(m,n);
      float[][] C = X.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            C[i][j] = A[i][j] - B.A[i][j];
         }
      }
      return X;
   }

   /** A = A - B
   @param B    another matrix
   @return     A - B
   */

   public Matrix minusEquals (Matrix B) {
      checkMatrixDimensions(B);
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            A[i][j] = A[i][j] - B.A[i][j];
         }
      }
      return this;
   }

   /** Element-by-element multiplication, C = A.*B
   @param B    another matrix
   @return     A.*B
   */

   public Matrix arrayTimes (Matrix B) {
      checkMatrixDimensions(B);
      Matrix X = new Matrix(m,n);
      float[][] C = X.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            C[i][j] = A[i][j] * B.A[i][j];
         }
      }
      return X;
   }

   /** Element-by-element multiplication in place, A = A.*B
   @param B    another matrix
   @return     A.*B
   */

   public Matrix arrayTimesEquals (Matrix B) {
      checkMatrixDimensions(B);
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            A[i][j] = A[i][j] * B.A[i][j];
         }
      }
      return this;
   }

   /** Element-by-element right division, C = A./B
   @param B    another matrix
   @return     A./B
   */

   public Matrix arrayRightDivide (Matrix B) {
      checkMatrixDimensions(B);
      Matrix X = new Matrix(m,n);
      float[][] C = X.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            C[i][j] = A[i][j] / B.A[i][j];
         }
      }
      return X;
   }

   /** Element-by-element right division in place, A = A./B
   @param B    another matrix
   @return     A./B
   */

   public Matrix arrayRightDivideEquals (Matrix B) {
      checkMatrixDimensions(B);
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            A[i][j] = A[i][j] / B.A[i][j];
         }
      }
      return this;
   }

   /** Element-by-element left division, C = A.\B
   @param B    another matrix
   @return     A.\B
   */

   public Matrix arrayLeftDivide (Matrix B) {
      checkMatrixDimensions(B);
      Matrix X = new Matrix(m,n);
      float[][] C = X.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            C[i][j] = B.A[i][j] / A[i][j];
         }
      }
      return X;
   }

   /** Element-by-element left division in place, A = A.\B
   @param B    another matrix
   @return     A.\B
   */

   public Matrix arrayLeftDivideEquals (Matrix B) {
      checkMatrixDimensions(B);
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            A[i][j] = B.A[i][j] / A[i][j];
         }
      }
      return this;
   }

   /** Multiply a matrix by a scalar, C = s*A
   @param s    scalar
   @return     s*A
   */

   public Matrix times (float s) {
      Matrix X = new Matrix(m,n);
      float[][] C = X.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            C[i][j] = s*A[i][j];
         }
      }
      return X;
   }

   /** Multiply a matrix by a scalar in place, A = s*A
   @param s    scalar
   @return     replace A by s*A
   */

   public Matrix timesEquals (float s) {
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            A[i][j] = s*A[i][j];
         }
      }
      return this;
   }

   /** Linear algebraic matrix multiplication, A * B
   @param B    another matrix
   @return     Matrix product, A * B
   @exception  IllegalArgumentException Matrix inner dimensions must agree.
   */

   //n:4097 m:40 B.n:1 B.m:4097
   public Matrix times (Matrix B) {
      if (B.m != n) {
         throw new IllegalArgumentException("Matrix inner dimensions must agree.");
      }
      Matrix X = new Matrix(m,B.n);
      float[][] C = X.getArray();
      float[] Bcolj = new float[n];
      for (int j = 0; j < B.n; j++) {
         for (int k = 0; k < n; k++) {
            Bcolj[k] = B.A[k][j];
         }
         for (int i = 0; i < m; i++) {
            float[] Arowi = A[i];
            float s = 0;
            for (int k = 0; k < n; k++) {
            	//Utils.Dbg("i1:%d j1:%d  %f %f", i, k,Arowi[k],Bcolj[k]);
            //Utils.Dbg( " %f * %f = %f",Arowi[k],Bcolj[k],Arowi[k]*Bcolj[k]);
               s += Arowi[k]*Bcolj[k];
            }
            C[i][j] = s;
         }
      }
      return X;
   }

   public Matrix timesTriangular (Matrix B)
   {
      if (B.m != n)
         throw new IllegalArgumentException("Matrix inner dimensions must agree.");

      Matrix X = new Matrix(m,B.n);
      float[][] c = X.getArray();
      float[][] b;
      float s = 0;
      float[] Arowi;
      float[] Browj;

      b = B.getArray();
      //multiply with each row of A
      for (int i = 0; i < m; i++)
      {
        Arowi = A[i];

        //for all columns of B
        for (int j = 0; j < B.n; j++)
        {
          s = 0;
          Browj = b[j];
          //since B being triangular, this loop uses k <= j
          for (int k = 0; k <= j; k++)
          {
            s += Arowi[k] * Browj[k];
          }
          c[i][j] = s;
        }
      }
      return X;
   }


   /**
    * X.diffEquals() calculates differences between adjacent columns of this
    * matrix. Consequently the size of the matrix is reduced by one. The result
    * is stored in this matrix object again.
    */
   public void diffEquals()
   {
       float[] col = null;
       for(int i = 0; i < A.length; i++)
       {
           col = new float[A[i].length - 1];

           for(int j = 1; j < A[i].length; j++)
               col[j-1] = Math.abs(A[i][j] - A[i][j - 1]);

           A[i] = col;
       }
       n--;
   }

   /**
    * X.logEquals() calculates the natural logarithem of each element of the
    * matrix. The result is stored in this matrix object again.
    */
   public void logEquals()
   {
       for(int i = 0; i < A.length; i++)
         for(int j = 0; j < A[i].length; j++)
           A[i][j] = (float) Math.log(A[i][j]);
   }

   /**
    * X.powEquals() calculates the power of each element of the matrix. The
    * result is stored in this matrix object again.
    */
   public void powEquals(float exp)
   {
       for(int i = 0; i < A.length; i++)
         for(int j = 0; j < A[i].length; j++)
           A[i][j] = (float) Math.pow(A[i][j], exp);
   }

   /**
    * X.powEquals() calculates the power of each element of the matrix.
    *
    * @return Matrix
    */
   public Matrix pow(float exp)
   {
       Matrix X = new Matrix(m,n);
       float[][] C = X.getArray();

       for (int i = 0; i < m; i++)
         for (int j = 0; j < n; j++)
           C[i][j] = (float) Math.pow(A[i][j], exp);

       return X;
   }





   /**
    * X.thrunkAtLowerBoundariy(). All values smaller than the given one are set
    * to this lower boundary.
    */
   public void thrunkAtLowerBoundary(float value)
   {
       for(int i = 0; i < A.length; i++)
         for(int j = 0; j < A[i].length; j++)
         {
             if(A[i][j] < value)
                 A[i][j] = value;
         }
   }

   public float trace () {
      float t = 0;
      for (int i = 0; i < Math.min(m,n); i++) {
         t += A[i][i];
      }
      return t;
   }

   public static Matrix random (int m, int n) {
      Matrix A = new Matrix(m,n);
      float[][] X = A.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            X[i][j] = (float) Math.random();
         }
      }
      return A;
   }

   /** Generate identity matrix
   @param m    Number of rows.
   @param n    Number of colums.
   @return     An m-by-n matrix with ones on the diagonal and zeros elsewhere.
   */

   public static Matrix identity (int m, int n) {
      Matrix A = new Matrix(m,n);
      float[][] X = A.getArray();
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            X[i][j] = (i == j ? 1.0f : 0.0f);
         }
      }
      return A;
   }


   /** Print the matrix to stdout.   Line the elements up in columns
     * with a Fortran-like 'Fw.d' style format.
   @param w    Column width.
   @param d    Number of digits after the decimal.
   */

   public void print (int w, int d) {
      print(new PrintWriter(System.out,true),w,d); }

   /** Print the matrix to the output stream.   Line the elements up in
     * columns with a Fortran-like 'Fw.d' style format.
   @param output Output stream.
   @param w      Column width.
   @param d      Number of digits after the decimal.
   */

   public void print (PrintWriter output, int w, int d) {
      DecimalFormat format = new DecimalFormat();
      format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
      format.setMinimumIntegerDigits(1);
      format.setMaximumFractionDigits(d);
      format.setMinimumFractionDigits(d);
      format.setGroupingUsed(false);
      print(output,format,w+2);
   }

   /** Print the matrix to stdout.  Line the elements up in columns.
     * Use the format object, and right justify within columns of width
     * characters.
     * Note that is the matrix is to be read back in, you probably will want
     * to use a NumberFormat that is set to US Locale.
   @param format A  Formatting object for individual elements.
   @param width     Field width for each column.
   @see java.text.DecimalFormat#setDecimalFormatSymbols
   */

   public void print (NumberFormat format, int width) {
      print(new PrintWriter(System.out,true),format,width); }

   // DecimalFormat is a little disappointing coming from Fortran or C's printf.
   // Since it doesn't pad on the left, the elements will come out different
   // widths.  Consequently, we'll pass the desired column width in as an
   // argument and do the extra padding ourselves.

   /** Print the matrix to the output stream.  Line the elements up in columns.
     * Use the format object, and right justify within columns of width
     * characters.
     * Note that is the matrix is to be read back in, you probably will want
     * to use a NumberFormat that is set to US Locale.
   @param output the output stream.
   @param format A formatting object to format the matrix elements
   @param width  Column width.
   @see java.text.DecimalFormat#setDecimalFormatSymbols
   */

   public void print (PrintWriter output, NumberFormat format, int width) {
      output.println();  // start on new line.
      for (int i = 0; i < m; i++) {
         for (int j = 0; j < n; j++) {
            String s = format.format(A[i][j]); // format the number
            int padding = Math.max(1,width-s.length()); // At _least_ 1 space
            for (int k = 0; k < padding; k++)
               output.print(' ');
            output.print(s);
         }
         output.println();
      }
      output.println();   // end with blank line.
   }


  public void write(OutputStream out) throws IOException
  {
      // Zuerst wird das Wurzelelement mit Attribut geschrieben
     try
     {
       XMLOutputFactory factory = XMLOutputFactory.newInstance();
       XMLStreamWriter writer = factory.createXMLStreamWriter( out);

       writer.writeStartElement("matrix");

       writer.writeAttribute("rows", Integer.toString(m));
       writer.writeAttribute("cols", Integer.toString(n));

       for(int i = 0; i < m; i++)
       {
         writer.writeStartElement("matrixrow");
         for(int j = 0; j < n; j++)
         {
           writer.writeStartElement("cn");
           writer.writeAttribute("type","IEEE-754");
           writer.writeCharacters(getEncodedValue(A[i][j]));
           writer.writeEndElement();
         }
         writer.writeEndElement();
       }

       writer.writeEndElement();

       writer.close();
     }
     catch (XMLStreamException ex)
     {
       new IOException("a xml stream exception occured");
     }
  }
  
  public static Matrix readCSV(InputStream inputStream) throws IOException
  {
	  InputStreamReader in = new InputStreamReader(inputStream);
	  BufferedReader bufRdr  = new BufferedReader(in);
	  Vector<Vector<Float>> rowVectors = new Vector<Vector<Float>>();
	  
	  
	  String line = null;
	  int cols=0;
	  
	  //read each line of text file
	  while((line = bufRdr.readLine()) != null)
	  {	
		  cols = 0;
		  Vector<Float> row = new Vector<Float>();
				
		  StringTokenizer st = new StringTokenizer(line,",");
		  while (st.hasMoreTokens())
		  {
			  //get next token and store it in the array
			  row.add(new Float(st.nextToken()));
			  cols++;
		  }
				
		  rowVectors.add(row);	
	  }
	  
	  //close the stream
	  bufRdr.close();
	  
	  //convert to array
	  float [][] data = new float[rowVectors.size()][cols];
	  
	  for(int i = 0; i < rowVectors.size(); i++)
	  {
		  Vector<Float> row = rowVectors.get(i);
		  for(int j = 0; j < row.size(); j++)
		  {
			data[i][j] = row.get(j).floatValue();  
		  }
	  }
	  
	  return new Matrix(data);
  }

  public void read(InputStream in) throws IOException
  {
    try
    {
      XMLInputFactory factory = XMLInputFactory.newInstance();
      XMLStreamReader parser = factory.createXMLStreamReader(in);

      checkNextTag(parser, XMLStreamReader.START_ELEMENT, "matrix");

      m = Integer.parseInt(parser.getAttributeValue(null, "rows"));
      n = Integer.parseInt(parser.getAttributeValue(null, "cols"));

      A = new float[m][n];

      for (int i = 0; i < m; i++)
      {
        checkNextTag(parser, XMLStreamReader.START_ELEMENT, "matrixrow");

        for (int j = 0; j < n; j++)
        {
          checkNextTag(parser, XMLStreamReader.START_ELEMENT, "cn");

          if (!parser.getAttributeValue(null, "type").equals("IEEE-754"))
            throw new IOException("a xml stream exception occured");

          if (parser.next() != XMLStreamReader.CHARACTERS)
            throw new IOException("a xml stream exception occured");

          A[i][j] = getDecodedValue(parser.getText());

          checkNextTag(parser, XMLStreamReader.END_ELEMENT, "cn");
        }

        checkNextTag(parser, XMLStreamReader.END_ELEMENT, "matrixrow");
      }

      checkNextTag(parser, XMLStreamReader.END_ELEMENT, "matrix");
    }
    catch(XMLStreamException xse)
    {
      throw new IOException("a xml stream exception occured");
     }
  }


/* ------------------------
   Private Methods
 * ------------------------ */

   /** Check if size(A) == size(B) **/

   private void checkMatrixDimensions (Matrix B) {
      if (B.m != m || B.n != n) {
         throw new IllegalArgumentException("Matrix dimensions must agree.");
      }
   }

   private static String getEncodedValue(float v)
   {
     char[] map = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
     char[] encoded = new char[16];
     long l = Float.floatToRawIntBits(v);

     for(int i = 16; i > 0; i--)
     {
       encoded[i-1] = map[(int)(l & 0x000000000000000f)];
       l = l >>> 4;
     }

     return new String(encoded);
   }

   private static float getDecodedValue(String encoded) throws NumberFormatException
   {
     int j = 0;
     int[] map = {
                   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
                   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
                   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
                   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
                   0,  0,  0,  0,  0,  0,  0,  0,  0,  1,
                   2,  3,  4,  5,  6,  7,  8,  9,  0,  0,
                   0,  0,  0,  0,  0, 10, 11, 12, 13, 14,
                   15, 0,  0,  0,  0,  0,  0,  0,  0,  0,
                   0 , 0,  0,  0,  0,  0,  0,  0,  0,  0,
                   0 , 0,  0,  0,  0,  0,  0, 10, 11,  12,
                   13, 14,15,  0,  0,  0,  0,  0,  0,  0
                 };
     int l = 0;

     for(int i = 0; i < 16 && i < encoded.length(); i++)
     {

       try
       {
         j = map[encoded.charAt(i)];
       }
       catch(ArrayIndexOutOfBoundsException aioobe)
       {
         throw new NumberFormatException("unknown character in encoding");
       }
       l = l << 4 | (j);
     }

     return Float.intBitsToFloat(l);
   }

   private void checkNextTag(XMLStreamReader parser, int type, String tagName) throws IOException, XMLStreamException
   {
       if (!parser.hasNext())
         throw new IOException("corrupt XML representation");

       if(parser.next() != type)
         throw new IOException("corrupt XML representation");

       if(!parser.getLocalName().equals(tagName))
         throw new IOException("corrupt XML representation");
   }


   /**
    * Writes the xml representation of this object to the xml ouput stream.<br>
    * <br>
    * There is the convetion, that each call to a <code>writeXML()</code> method
    * results in one xml element in the output stream.
    *
    * @param writer XMLStreamWriter the xml output stream
    *
    * @throws IOException raised, if there are any io troubles
    * @throws XMLStreamException raised, if there are any parsing errors
    */
   public void writeXML(XMLStreamWriter writer) throws IOException, XMLStreamException
   {
     writer.writeStartElement("matrix");

     writer.writeAttribute("rows", Integer.toString(m));
     writer.writeAttribute("cols", Integer.toString(n));

     for(int i = 0; i < m; i++)
     {
       writer.writeStartElement("matrixrow");
       for(int j = 0; j < n; j++)
       {
         writer.writeStartElement("cn");
         writer.writeAttribute("type","IEEE-754");
         writer.writeCharacters(getEncodedValue(A[i][j]));
         writer.writeEndElement();
       }
       writer.writeEndElement();
     }

     writer.writeEndElement();
   }


   /**
    * Reads the xml representation of an object form the xml input stream.<br>
    * <br>
    * There is the convention, that <code>readXML()</code> starts parsing by
    * checking the start tag of this object and finishes parsing by checking the
    * end tag. The caller has to ensure, that at method entry the current token
    * is the start tag. After the method call it's the callers responsibility to
    * move from the end tag to the next token.
    *
    * @param parser XMLStreamReader the xml input stream
    *
    * @throws IOException raised, if there are any io troubles
    * @throws XMLStreamException raised, if there are any parsing errors
    */
   public void readXML(XMLStreamReader parser) throws IOException, XMLStreamException
   {
     parser.require(XMLStreamReader.START_ELEMENT, null, "matrix");

     m = Integer.parseInt(parser.getAttributeValue(null, "rows"));
     n = Integer.parseInt(parser.getAttributeValue(null, "cols"));

     A = new float[m][n];

     for (int i = 0; i < m; i++)
     {
       parser.nextTag();
       parser.require(XMLStreamReader.START_ELEMENT, null, "matrixrow");

       for (int j = 0; j < n; j++)
       {
         parser.nextTag();
         parser.require(XMLStreamReader.START_ELEMENT, null, "cn");

         if (!parser.getAttributeValue(null, "type").equals("IEEE-754"))
           throw new IOException("a xml stream exception occured");

         if (parser.next() != XMLStreamReader.CHARACTERS)
           throw new IOException("a xml stream exception occured");

         A[i][j] = getDecodedValue(parser.getText());

         parser.next();
         parser.require(XMLStreamReader.END_ELEMENT, null, "cn");
       }

       parser.next();
       parser.require(XMLStreamReader.END_ELEMENT, null, "matrixrow");
     }

     parser.next();
     parser.require(XMLStreamReader.END_ELEMENT, null,"matrix");
   }


   /**
	 * Returns the mean values along the specified dimension.
	 *
	 * @param dim
	 *            If 1, then the mean of each column is returned in a row
	 *            vector. If 2, then the mean of each row is returned in a
	 *            column vector.
	 * @return A vector containing the mean values along the specified
	 *         dimension.
	 */
	public Matrix mean(int dim) {
		Matrix result;
		switch (dim) {
		case 1:
			result = new Matrix(1, n);
			for (int currN = 0; currN < n; currN++) {
				for (int currM = 0; currM < m; currM++)
					result.A[0][currN] += A[currM][currN];
				result.A[0][currN] /= m;
			}
			return result;
		case 2:
			result = new Matrix(m, 1);
			for (int currM = 0; currM < m; currM++) {
				for (int currN = 0; currN < n; currN++) {
					result.A[currM][0] += A[currM][currN];
				}
				result.A[currM][0] /= n;
			}
			return result;
		default:
			(new IllegalArgumentException(
					"dim must be either 1 or 2, and not: " + dim))
					.printStackTrace();
			return null;
		}
	}

	/**
	 * Calculate the full covariance matrix.
	 * @return the covariance matrix
	 */
	public Matrix cov() {
		Matrix transe = this.transpose();
		Matrix result = new Matrix(transe.m, transe.m);
		for(int currM = 0; currM < transe.m; currM++){
			for(int currN = currM; currN < transe.m; currN++){
				float covMN = cov(transe.A[currM], transe.A[currN]);
				result.A[currM][currN] = covMN;
				result.A[currN][currM] = covMN;
			}
		}
		return result;
	}

	/**
	 * Calculate the covariance between the two vectors.
	 * @param vec1
	 * @param vec2
	 * @return the covariance between the two vectors.
	 */
	private float cov(float[] vec1, float[] vec2){
		float result = 0;
		int dim = vec1.length;
		if(vec2.length != dim)
			(new IllegalArgumentException("vectors are not of same length")).printStackTrace();
		float meanVec1 = mean(vec1), meanVec2 = mean(vec2);
		for(int i=0; i<dim; i++){
			result += (vec1[i]-meanVec1)*(vec2[i]-meanVec2);
		}
		return result / Math.max(1, dim-1);
//		int dim = vec1.length;
//		if(vec2.length != dim)
//			(new IllegalArgumentException("vectors are not of same length")).printStackTrace();
//		float[] times = new float[dim];
//		for(int i=0; i<dim; i++)
//			times[i] += vec1[i]*vec2[i];
//		return mean(times) - mean(vec1)*mean(vec2);
	}

	/**
	 * the mean of the values in the float array
	 * @param vec	float values
	 * @return	the mean of the values in vec
	 */
	private float mean(float[] vec){
		float result = 0;
		for(int i=0; i<vec.length; i++)
			result += vec[i];
		return result / vec.length;
	}
	
	/**
	 * Returns the sum of the component of the matrix.
	 * @return the sum
	 */
	public float sum(){
		float result = 0;
		for(float[] dArr : A)
			for(float d : dArr)
				result += d;
		return result;
	}

	/**
	 * returns a new Matrix object, where each value is set to the absolute value
	 * @return a new Matrix with all values being positive
	 */
	public Matrix abs() {
		Matrix result = new Matrix(m, n); // don't use clone(), as the values are assigned in the loop.
		for(int i=0; i<result.A.length; i++){
			for(int j=0; j<result.A[i].length; j++)
				result.A[i][j] = Math.abs(A[i][j]);
		}
		return result;
	}
	
	
	/**
	 * Writes the Matrix to an ascii-textfile that can be read by Matlab.
	 * Usage in Matlab: load('filename', '-ascii');
	 * @param filename the name of the ascii file to create, e.g. "C:\\temp\\matrix.ascii"
	 * @throws IllegalArgumentException if there is a problem with the filename
	 */
	public void writeAscii(String filename) throws IllegalArgumentException{
		PrintWriter pw;
		try {
			pw = new PrintWriter(new File(filename));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("there was a problem with the file "+filename);
		}
		for(int i = 0; i< m; i++){
			for(int j = 0; j < n; j++){
				pw.printf("%1$1.7e ", A[i][j]);
			}
			pw.printf("\r");
		}
		pw.close();
	}
}
