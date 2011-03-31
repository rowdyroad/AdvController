#ifndef __FXFORM_H__
#define __FXFORM_H__

void fft(float *u, int nfft);
void fdctiv(float *x, int n);
void fdstiv(float *x, int n);
void mlt_sine_window(float *h, int n);
void fmlt(float *x, float *ya, float * ha, int n);
void fimlt(float *x, float *ys, float * hs, int n);
void fmclt(float *Xc, float *Xs, float *x, float *ya, float *ha, int n);
void fimclt(float *Xc, float *Xs, float *x, float *ys, float *hs, int n);

#endif
