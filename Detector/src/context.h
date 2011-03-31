#ifndef __CONTEXT_H__
#define __CONTEXT_H__

#include "wmark.h"
#include "hthres.h"

typedef struct
{
	WAVEFILE    *win;					// pointer to input .wav file
	WSAMPLE		xwav[NCHMAX*NFREQ];		// I/O vector
	float		xx[NFREQ];		 		// buffer for both x(t) and x(w)
	float		Xc[NFREQ];				// x(w) cosine part
	float		Xs[NFREQ];				// x(w) sine part
	float		h[NFREQ];				// MCLT window
	float		ya[3*NFREQ/2];			// MCLT internal buffers - analysis
	float		yb[3*NFREQ/2];			// DCT internal buffers - analysis

	float       Ht[NFREQ];				// hearing threshold
	float		bufA[NFREQ];			// x(w) of the current 3-block
	short int	htA[NFREQ];				// audibility of the current 3-block

	float		fm_lookup[NFREQ];		// precomputed fletcher munson limits
	float		*sine_lookup;			// sine look up table
	float		dbcut, dbnoisefloor, fdnoisefloor;

	int			watermark[CHIPSPERBLOCK][FRAMESPERWINDOW][NWATERMARKS];

	int			permutation[CHIPSPERBLOCK][BITSPERWINDOW];

	long		pointers[FRAMESPERWINDOW][SRTIME];

	SSDBANDS	ssdecoder;
	int			startfreq, endfreq;

	PAYLOAD		payload[SRTIME][SRFREQ][NWATERMARKS];
	RESULT		buffer[3];
	RESULT		bestcase;
	int			pbuff;

	float	blocksize, framesize; 
	long	blocksperwindow, blocksperbit, blocksperframe;
	long	searchstep, framesperbit; 
} context;

context * create_context_from_file(const char * filename);
context * create_context_from_stdin();
void search_watermark(context * ctx);
void destroy_context(context *ctx);

#endif
