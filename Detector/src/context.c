#include "context.h"

// creating the decoder pointers in time
void create_pointers () 
{
	int timeindex, frame;
	float scalefactor;
	for (timeindex = 0; timeindex < SRTIME; timeindex++) {
		// compute the time scaling factor for each test
		scalefactor = 1.-TIME_RESILIENCE/100.+2.*timeindex*TIME_RESILIENCE/(100.*(SRTIME-1));
		for (frame = 0; frame < FRAMESPERWINDOW; frame++) {
			// create the pointers which point to the center of each time interval
			// where a single block of chips is used. for different scaling factors the
			// set of pointers is more or less diffused.
			pointers[frame][timeindex] = (long) (1 + (0.2 + frame) * scalefactor * blocksperwindow / FRAMESPERWINDOW) / searchstep;
			// pointers[][] point to the first block within a frame to be integrated.
			// the scheme assumes that 60% of blocks within a frame will be integrated.
			// another assumption is that the basic step is 3 blocks!!!
			// therefore we have 20% of the frame at both sides of the integration area.
			// we get the integer value of the pointer by computing [(x+1)/3]
			// thats how 01->0, 234->1 567->2 etc
			// NOTE: for MCLT=2048 the only thing that should change is 3->6 as the basic
			// step is 6 
		}
	}
#ifdef PRINT_TIME_POINTERS
    {
		FILE *f;
		int i, bit;
		f = fopen("time_pointers.m", "w");
		fprintf(f, "tp=[\n");
		for (bit = 0; bit < SRTIME; bit++) {
			for (i = 0; i < FRAMESPERWINDOW; i++) 
				fprintf(f, "%d %d %d\t", pointers[i][bit]);
			fprintf(f, "\n");
		}
		fprintf(f, "];\n");
		fclose(f);
	}
#endif
}

#ifdef TO_CEPSTRUM_WITH_FDISTV
// performing a cepstrum filtering of an mclt samples
void cepstrum_filtering(float  *inout, float  *cepstrum) {
	int i;
	// translating the signal into cepstrum
	// Rico: new cepstral filtering via DST-IV
	fdstiv(inout, NFREQ);
	// lowpass filtering
	for (i = 0; i < CF; i++)
	 	inout[i] = 0;
	// peak removal. it is important to notice that
	// abs(inout[i]) > PM works very bad. cannot explain.
	for (i = CF; i < NFREQ; i++)
		if (inout[i] > PM) inout[i] = PM;
	/*
	// highpass filtering. removed due to addition of noise.
	for (i = CM; i < Nbands; i++)
		inout[i] = 0;
	*/
	// translating the signal back to freq
	fdstiv(inout, NFREQ);
	// remove artificial spectral peak from DST-IV
	for (i = 0; i < 4; i++)
	 	inout[i] = 0;
} // dst4
#else
// performing a cepstrum filtering of an mclt samples
void cepstrum_filtering(float *inout, float *cepstrum) {
	int i;
	// translating the siugnal into cepstrum
	dct(inout, cepstrum, LOG2_NFREQ, sine_lookup);
	// lowpass filtering
	for (i = 0; i < CF; i++)
	 	inout[i] = 0;
    // peak clip off
	for (i = CF; i < NFREQ; i++)
		if (inout[i] > PM) inout[i] = PM;
	// translating the signal back to freq
	idct(inout, cepstrum, LOG2_NFREQ, sine_lookup);
} // dct
#endif

// creating the decoder pointers in frequency
void create_decoder (long fs) {
	SSBANDS s;
	int i, bit;
	float scalefactor;
    for (i = 0; i < SRFREQ; i++) {
		// computing the frequency shift scaling factor
		scalefactor = 1.-FREQ_RESILIENCE/100.+2.*i*FREQ_RESILIENCE/(100.*(SRFREQ-1));
		gensubindex(&s, fs, scalefactor);
		for (bit = 0; bit < CHIPSPERBLOCK; bit++) {
			// compute the middle frequency of each subband where
			// a single bit is embedded
		    ssdecoder.fmiddle[bit][i] = s.fmiddle[bit];
			// determine the range within this subband where
			// the detection is performed
			switch (s.fend[bit] - s.fstart[bit] + 1) {
				case  0: ssdecoder.cbs[bit][i] =  0; ssdecoder.cbe[bit][i] = 0; break;
				case  1: ssdecoder.cbs[bit][i] =  0; ssdecoder.cbe[bit][i] = 0; break;
				case  2: ssdecoder.cbs[bit][i] =  0; ssdecoder.cbe[bit][i] = 0; break;
				case  3: ssdecoder.cbs[bit][i] =  0; ssdecoder.cbe[bit][i] = 0; break;
				case  4: ssdecoder.cbs[bit][i] =  0; ssdecoder.cbe[bit][i] = 1; break;
				case  5: ssdecoder.cbs[bit][i] = -1; ssdecoder.cbe[bit][i] = 1; break;
				case  6: ssdecoder.cbs[bit][i] =  0; ssdecoder.cbe[bit][i] = 1; break;
				case  7: ssdecoder.cbs[bit][i] = -1; ssdecoder.cbe[bit][i] = 1; break;
				case  8: ssdecoder.cbs[bit][i] = -1; ssdecoder.cbe[bit][i] = 2; break;
				case  9: ssdecoder.cbs[bit][i] = -2; ssdecoder.cbe[bit][i] = 2; break;
				case 10: ssdecoder.cbs[bit][i] = -2; ssdecoder.cbe[bit][i] = 3; break;
				case 11: ssdecoder.cbs[bit][i] = -2; ssdecoder.cbe[bit][i] = 2; break;
				case 12: ssdecoder.cbs[bit][i] = -2; ssdecoder.cbe[bit][i] = 3; break;
				default: ssdecoder.cbs[bit][i] = -3; ssdecoder.cbe[bit][i] = 3;
			}
		}
	}
	// startfreq determines the lowest coeff that takes part in watermarking
	startfreq = ssdecoder.fmiddle[0][0] + ssdecoder.cbs[0][0];;
	// endfreq is the highest coeff that takes part in watermarking
	endfreq = ssdecoder.fmiddle[CHIPSPERBLOCK-1][SRFREQ-1] + ssdecoder.cbe[CHIPSPERBLOCK-1][SRFREQ-1];
#ifdef PRINT_SUBBAND_INDEX
    {
		FILE *f;
		int i;
		f = fopen("dec_subband_index.m", "w");
		fprintf(f, "dsbi=[\n");
		for (bit = 0; bit < CHIPSPERBLOCK; bit++) {
			for (i = 0; i < SRFREQ; i++) 
				fprintf(f, "%d %d\t", ssdecoder.cbs[bit][i], ssdecoder.cbe[bit][i]);
			fprintf(f, "\n");
		}
		fprintf(f, "];\n");
		fclose(f);
	}
#endif
}

// creating the watermarks
void create_watermark (int seed) {
	int		i, j, k, rows, frame, bit, p_lut;

	// how the watermark is created?
	// we have hardwired two lookup tables of rows for
	// chess watermarks of length 2 and four bits.
	// we create the larger 6 and 8 bit wide tables  
	// by crossconcatenating the smaller ones.

	// look up tables of size two and four blocks in a row
	int		lut2[2][2] = {  1, 0, 
							0, 1};
	int		lut4[6][4] = {	1, 1, 0, 0, 
							1, 0, 1, 0, 
							1, 0, 0, 1, 
							0, 0, 1, 1, 
							0, 1, 0, 1, 
							0, 1, 1, 0}; 
	// look up tables of size 8 and 6 blocks in a row
	int		lut8[36][8];
	int		lut6[12][6];

	// create the lookup tables for watermark creation
	// lookup tables of size 6 and 8 are made from 
	// lookup tables fo size 2 and four
	for (i = 0; i < 6; i++)
		for (j = 0; j < 6; j++) 
			for (k = 0; k < 8; k++) 
				if (k < 4) lut8[i*6+j][k] = lut4[i][k];
				else lut8[i*6+j][k] = lut4[j][k-4];
	for (i = 0; i < 6; i++)
		for (j = 0; j < 2; j++) 
			for (k = 0; k < 6; k++) 
				if (k < 4) lut6[i*2+j][k] = lut4[i][k];
				else lut6[i*2+j][k] = lut2[j][k-4];

	// depending on period the appropriate lookup table is selected
	// first we select its dimension (number of different rows)
	if (framesperbit == 6) rows = 12;
	else if (framesperbit == 8) rows = 36;
	else if (framesperbit == 4) rows = 6;
	// else exit(-1); // this case is an error

	// create the watermarks
	for (i = 0; i < NWATERMARKS; i++) {
		// all possible watermarks are created. there are 
		// NWATERMARKS=2^cci different watermarks.
		INITIALIZE_SEED(seed, i);
		for (bit = 0; bit < CHIPSPERBLOCK; bit++) {
			for (frame = 0; frame < FRAMESPERWINDOW; frame++) {
				// point to different row in the lookup table
				// after each period.
				if (frame % framesperbit == 0) {
					p_lut = (int) ((rand()*rows)/RAND_MAX);
				}
				// copy the bits from the selected row of the lookup table
				// into the watermark structure.
				if (framesperbit == 6) watermark[bit][frame][i] = lut6[p_lut][frame%6];
				else if (framesperbit == 8) watermark[bit][frame][i] = lut8[p_lut][frame%8];
				else if (framesperbit == 4) watermark[bit][frame][i] = lut4[p_lut][frame%4];
			}
		}
	}
#ifdef PRINT_CHESS_CHIPS
    {
		int i,j,k;
		FILE *f;
		f = fopen("all_chess_chips.m", "w");
		fprintf(f, "acc=[\n");
        for (i = 0; i < NFREQ; i++) {
		    for (j = 0; j < FRAMESPERWINDOW; j++) {
	            for (k = 0; k < NWATERMARKS; k++) {
				    fprintf(f, "%d ", (int)watermark[i][j][k]);
			    }
			    fprintf(f, "\n");
            }
		}
		fprintf(f, "];\n");
		fclose(f);
	}
#endif
}

// creating the secret permutation
// each window has a bitsperblock groups of
// blocks each XORed with a single bit of the message.
// the bitsperblock in each subband are permuted for
// i) security and ii) equal distribution of noise.
void create_permutation (int seed) {
	int bit, i, j, count, temp;
	srand(seed);
	for (bit = 0; bit < CHIPSPERBLOCK; bit++) {
		for (i = 0; i < BITSPERWINDOW; i++) 
			permutation[bit][i] = -1;
		for (i = 0; i < BITSPERWINDOW; i++) {
			temp = rand() % (BITSPERWINDOW - i);
			count = 0;
			for (j = 0; j < BITSPERWINDOW; j++) {
				if (permutation[bit][j] == -1) {
					if (count == temp) {
						permutation[bit][j] = i;
						break;
					} else count++;
				}
			}
		}
	}
#ifdef PRINT_PERMUTATIONS
    {
		int i,j;
		FILE *f;
		f = fopen("permutations.m", "w");
		fprintf(f, "p=[\n");
		for (i = 0; i < CHIPSPERBLOCK; i++) {
			for (j = 0; j < BITSPERWINDOW; j++) {
				fprintf(f, "%d ", (int)chip[i][j]);
			}
			fprintf(f, "\n");
		}
		fprintf(f, "];\n");
		fclose(f);
	}
#endif
}

// creating a lookup table for the fletcher munson limits
// this is done because the old code used to compute this table
// for each call of hearing_threshold. very inefficient because
// thrabs has exp computation etc.
void create_fletcher_munson (long fs) {
	int i;
	for(i = 0; i < NFREQ; i++) 
		fm_lookup[i] = thrabs((0.5 + i) * (float) fs * 0.5 / (float) NFREQ);
}

// initializing the result buffer

void init_results() {
	int i,j;
	pbuff = 0;
	bestcase.dtime = -1000.;
	bestcase.nc = -1000.;		
	bestcase.cci = NONE;
	bestcase.load = NONE;
	for (i =  0; i < BITSPERWINDOW; i++) {
		bestcase.payload[i] = 0.;
	}
	for (i = 0; i < 3; i++) {
		buffer[i].dtime = -1000.;	
		buffer[i].nc = -1000.;		
		buffer[i].cci = NONE;	
		buffer[i].load = NONE;
		for (j =  0; j < BITSPERWINDOW; j++) {
			buffer[i].payload[j] = 0;
		}
	}
	for (i = 0; i < NFREQ; i++) {
		bufA[i] = 0.;
		htA[i] = 0;
	}
}

// loading the circular buffer
void load_buffer
(
	CIRCULAR_BUFFER *cb, 
	long blocks2load, 
	int Nch, 
	long storageblock, 
	long fs
) 
{
	long		i, j, k, w, ch, index;

	// preload data to circular buffer
	for (i = cb->pointer; i < cb->pointer + blocks2load; i++) {
		// define the location of the circular buffer start
		index = storageblock * (i % cb->length);
		// accept the previous subsum and reset the future one;
		for (j = startfreq; j <= endfreq; j++) {
			cb->ht[index+j-startfreq] =  htA[j];
			htA[j] = 0;
			cb->buffer[index+j-startfreq] = bufA[j];
			bufA[j] = 0.;
		}
		// load blocks of freq magnitudes
		for (k = 0; k < searchstep; k++) {
			// Read a block of samples from input file
			fn_getblock(xwav, NFREQ * Nch);
			// process only the first two channels
			for (ch = 0; ch < Nch; ch++) {
				// copy and scale input vector
				for (w = 0, j = ch; w < NFREQ; w++, j+=Nch) {
					xx[w] = INVMAXX * xwav[j];
				}
				// Compute direct MCLT, results in vectors Xc and Xs
				fmclt(Xc, Xs, xx, ya, h, NFREQ);
				// Compute freq magnitudes & block energy
#ifndef SPECTRUM_NORMALIZATION
				for (j = 0; j < NFREQ; j++) {
					xx[j] = Xc[j] * Xc[j] + Xs[j] * Xs[j];
				}
#else                
				{
					float	en = 0;
					float	P = 0.1;
					float	a = 0.005;
					float	b = 0.03;
					float	ga, gb, ec, gain;
					for (j = 0; j < NFREQ; j++) {
						xx[j] = Xc[j] * Xc[j] + Xs[j] * Xs[j];
						en += xx[j];
					}
					en /= NFREQ; // en now contains block energy
					gain = 1;
					if (en < P) {
						ga = (P-b)/(P-a);
						gb = P*(b-a)/(P-a);
						if (en > a) {
							ec = ga * en + gb;
						} else {
							ec = (b/a) * en;
						}
						// ec has desired block energy (after dynamic range compression)
						gain = ec/en;
					    // printf("Block energy = %g\n", en);
					    for (j = 0; j < NFREQ; j++) {
						    xx[j] *= gain;
					    }
					}
				}
#endif
				// Mark inaudible freq magnitudes
				// caution: the power of the signal is used 
				// for faster hearing threshold computation.
				hthres(Ht, xx, NFREQ, fs); 
				// perform the comparison of audibility and go to dB domain
				for (j = 0; j < NFREQ; j++) {
					if (xx[j] > Ht[j]*dbcut && xx[j] > fdnoisefloor) Ht[j] = 1.;
					else Ht[j] = 0.;
					xx[j] = 10.*log10(xx[j]);
				}
				// cepstrum filtering of the signal in dB
				cepstrum_filtering(&xx[0], &yb[0]);
				// copying the data into buffers
				for (j = startfreq; j <= endfreq; j++) {
					if (Ht[j] > 0.) {
						cb->buffer[index+j-startfreq] += (float) xx[j];
						cb->ht[index+j-startfreq] += (short int) Ht[j];
						bufA[j] += (float) xx[j];
						htA[j] += (short int) Ht[j];
					}
				}
			} // both channels processed.
		}
	}
}

// computes normalized correlations
// performs the normalized correlation test

int compute_correlations_normalized 
(	
	CIRCULAR_BUFFER *cb, 
	long storageblock, 
	int *timeINDEX, 
	int *freqINDEX
) 
{
	int			frame, bit, timeindex, freqindex, counter;
	long        i,w,k,kk,p1;
	float		blockcorr, temp;
	int			blockcard;
	float 		maximum, wmean, wstd;

	// initializing the data structure that holds the correlation sums
	for (timeindex = 0; timeindex < SRTIME; timeindex++) {
		for (freqindex = 0; freqindex < SRFREQ; freqindex++) {
			for (w = 0; w < NWATERMARKS; w++) {
			 	payload[timeindex][freqindex][w].sumsquares = 0.;
				for (i = 0; i < BITSPERWINDOW; i++) {
					payload[timeindex][freqindex][w].corr[i][0] = 0.;
					payload[timeindex][freqindex][w].corr[i][1] = 0.;
					payload[timeindex][freqindex][w].card[i][0] = 0;
					payload[timeindex][freqindex][w].card[i][1] = 0;
				}
			}
		}
	}

	// computing all correlations
	// the buffer is processed payloadbit by payloadbit
	for (i = 0; i < BITSPERWINDOW; i++) {
		// and then for each frame that is a part of the window
		// where the curent payloadbit is spread.
		for (frame = (int) i*framesperbit; frame < (int) (1+i)*framesperbit; frame++) {
			// for each time scaling we point to a different subset of blocks
			for (timeindex = 0; timeindex < SRTIME; timeindex++) {
				// p1 is equal to the first block of storageblock coeffs
				// for the current window of the audio clip
				p1 = cb->pointer+pointers[frame][timeindex];
				// p1 = p1%cb->length
				if (p1 >= cb->length) p1 -= cb->length;
				// pointing at storageblocks
				p1 = p1*storageblock;
				// for each frequency scaling 
				for (freqindex = 0; freqindex < SRFREQ; freqindex++) {
					// point to different subbands. for each subband (bitperblock)
					for (bit = 0; bit < CHIPSPERBLOCK; bit++) {
						// this is the sum/card for all coeffs that are part of one
						// bit of the watermark
						blockcorr = 0.;
						blockcard = 0;
						// point to the first coeff of the current bit
						k = p1 + ssdecoder.fmiddle[bit][freqindex] - startfreq;
						// prevent overflow of the circular buffer.
						// if (k >= cb->length * storageblock) 
						// 	k -= cb->length * storageblock;
						// for each coeff in the current frame and current subband
						for (kk = ssdecoder.cbs[bit][freqindex]; kk <= ssdecoder.cbe[bit][freqindex]; kk++) {
							// if audible add it up
							if (cb->ht[k+kk] > 0) {
								blockcorr += cb->buffer[k+kk];
								blockcard += cb->ht[k+kk];
							}
						}
						// add the sum of coeffs to corr[bit][1] or corr[bit][0]
						// depending on whether the watermark bit is one or zero.
						for (w = 0; w < NWATERMARKS; w=w+4) {
							// payload[timeindex][freqindex][w].corr[i][watermark[bit][frame][w]] += blockcorr;
							// payload[timeindex][freqindex][w].card[i][watermark[bit][frame][w]] += blockcard;
							// if there are permutations we have to make an extra
							// memory access to put the right sum into the right payload bit.
							payload[timeindex][freqindex][w].corr[permutation[bit][i]][watermark[bit][frame][w]] += blockcorr;
							payload[timeindex][freqindex][w].card[permutation[bit][i]][watermark[bit][frame][w]] += blockcard;
							payload[timeindex][freqindex][w+1].corr[permutation[bit][i]][watermark[bit][frame][w+1]] += blockcorr;
							payload[timeindex][freqindex][w+1].card[permutation[bit][i]][watermark[bit][frame][w+1]] += blockcard;
							payload[timeindex][freqindex][w+2].corr[permutation[bit][i]][watermark[bit][frame][w+2]] += blockcorr;
							payload[timeindex][freqindex][w+2].card[permutation[bit][i]][watermark[bit][frame][w+2]] += blockcard;
							payload[timeindex][freqindex][w+3].corr[permutation[bit][i]][watermark[bit][frame][w+3]] += blockcorr;
							payload[timeindex][freqindex][w+3].card[permutation[bit][i]][watermark[bit][frame][w+3]] += blockcard;
						} 
					}
				}
			}
		}
	}

	// find the largest correlation among all watermarks.
	// memorize the watermark (counter) and the attackers scaling
	// (timeINDEX and freqINDEX)
	counter = -1;
	maximum = 0;
	*timeINDEX = -1;
	*freqINDEX = -1;
	// computing sum of correlation squares
	for (w = 0; w < NWATERMARKS; w++) {
		for (freqindex = 0; freqindex < SRFREQ; freqindex++) {
			for (timeindex = 0; timeindex < SRTIME; timeindex++) {
				for (bit = 0; bit < BITSPERWINDOW; bit++) {
					// correlation test. note that here we compute the
					// ssum(1)/card(1) - sum(0)/card(0) test.
					// it is much faster and as accurate as 
					// the normalized standard corr test.
					// i have a matlab code thatgivesbetter results than
					// the standard corr test (itis in the matlab files that
					// i have sent to you.
					temp = payload[timeindex][freqindex][w].corr[bit][1]/payload[timeindex][freqindex][w].card[bit][1];
					temp -= payload[timeindex][freqindex][w].corr[bit][0]/payload[timeindex][freqindex][w].card[bit][0];
					// compute squares of correlations
					// payload[timeindex][freqindex][w].sumsquares += temp*temp/(4*OFFSET*OFFSET);
					// or compute abs of correlations
					payload[timeindex][freqindex][w].sumsquares += (float) (fabs(temp)/(2*OFFSET));
					// ZEROMEAN computation - subtract the mean AVE line625 insteadof line 623.
					// payload[timeindex][freqindex][w].sumsquares += fabs(temp-ave)/(2*OFFSET);
					// or compute the intact correlations (payload has to be 0x0
					// in order for this option to be used
					// payload[timeindex][freqindex][w].sumsquares += temp/(2*OFFSET);
				}
				payload[timeindex][freqindex][w].sumsquares /= BITSPERWINDOW;
				// search for the maximum correlation
				if (payload[timeindex][freqindex][w].sumsquares > maximum) {
					counter = w;
					maximum = payload[timeindex][freqindex][w].sumsquares;
					*timeINDEX = timeindex;
					*freqINDEX = freqindex;
				}
			}
		}
	}

	// Normalized correlation
	// Computing the mean of all partial correlations
	wmean = 0.;
	for (w = 0; w < NWATERMARKS; w++) 
		for (timeindex = 0; timeindex < SRTIME; timeindex++) 
			for (freqindex = 0; freqindex < SRFREQ; freqindex++) 
				wmean += payload[timeindex][freqindex][w].sumsquares;
	wmean /= NWATERMARKS*SRFREQ*SRTIME;
	// Computing the standard deviation of all partial correlations
	wstd = 0.;
	for (w = 0; w < NWATERMARKS; w++) 
		for (timeindex = 0; timeindex < SRTIME; timeindex++) 
			for (freqindex = 0; freqindex < SRFREQ; freqindex++)
				wstd += (payload[timeindex][freqindex][w].sumsquares - wmean)*(payload[timeindex][freqindex][w].sumsquares - wmean);
	wstd = (float) sqrt(wstd/(NWATERMARKS*SRFREQ*SRTIME));
	// normalizing the maximal correlation value in order to reflect
	// the standard deviation of computed normalizedcorrelations for all tests 
	payload[*timeINDEX][*freqINDEX][counter].sumsquares = (maximum - wmean) / wstd;

	return(counter);
}

// computes normalized correlations
// performs the normalized correlation test

int compute_correlations_partially 
(	
	CIRCULAR_BUFFER *cb, 
	long storageblock
) 
{
	int			frame, bit, timeindex, freqindex, counter;
	long        i,w,k,kk,p1;
	float		blockcorr, temp;
	int			blockcard;
	float 		maximum, wmean, wstd;

	// initializing the data structure that holds the correlation sums
	for (timeindex = 0; timeindex < SRTIME; timeindex++) {
		for (freqindex = 0; freqindex < SRFREQ; freqindex++) {
			for (w = 0; w < NWATERMARKS; w++) {
			 	payload[timeindex][freqindex][w].sumsquares = 0.;
				for (i = 0; i < BITSPERWINDOW; i++) {
					payload[timeindex][freqindex][w].corr[i][0] = 0.;
					payload[timeindex][freqindex][w].corr[i][1] = 0.;
					payload[timeindex][freqindex][w].card[i][0] = 0;
					payload[timeindex][freqindex][w].card[i][1] = 0;
				}
			}
		}
	}

	// computing all correlations
	// the buffer is processed payloadbit by payloadbit
	timeindex = 1 + (long) (SRTIME/2);
	freqindex = 1 + (int) (SRFREQ/2);
	for (i = 0; i < BITSPERWINDOW; i++) {
		// and then for each frame that is a part of the window
		// where the curent payloadbit is spread.
		for (frame = (int) i*framesperbit; frame < (int) (1+i)*framesperbit; frame++) {
			// p1 is equal to the first block of storageblock coeffs
			// for the current window of the audio clip
			p1 = cb->pointer+pointers[frame][timeindex];
			// p1 = p1%cb->length
			if (p1 >= cb->length) p1 -= cb->length;
			// pointing at storageblocks
			p1 = p1*storageblock;
			// point to different subbands. for each subband (bitperblock)
			for (bit = 0; bit < CHIPSPERBLOCK; bit++) {
				// this is the sum/card for all coeffs that are part of one
				// bit of the watermark
				blockcorr = 0.;
				blockcard = 0;
				// point to the first coeff of the current bit
				k = p1 + ssdecoder.fmiddle[bit][freqindex] - startfreq;
				// for each coeff in the current frame and current subband
				for (kk = ssdecoder.cbs[bit][freqindex]; kk <= ssdecoder.cbe[bit][freqindex]; kk++) {
					// if audible add it up
					if (cb->ht[k+kk] > 0) {
						blockcorr += cb->buffer[k+kk];
						blockcard += cb->ht[k+kk];
					}
				}
				// add the sum of coeffs to corr[bit][1] or corr[bit][0]
				// depending on whether the watermark bit is one or zero.
				for (w = 0; w < NWATERMARKS; w=w+4) {
					// payload[timeindex][freqindex][w].corr[i][watermark[bit][frame][w]] += blockcorr;
					// payload[timeindex][freqindex][w].card[i][watermark[bit][frame][w]] += blockcard;
					// if there are permutations we have to make an extra
					// memory access to put the right sum into the right payload bit.
					payload[timeindex][freqindex][w].corr[permutation[bit][i]][watermark[bit][frame][w]] += blockcorr;
					payload[timeindex][freqindex][w].card[permutation[bit][i]][watermark[bit][frame][w]] += blockcard;
					payload[timeindex][freqindex][w+1].corr[permutation[bit][i]][watermark[bit][frame][w+1]] += blockcorr;
					payload[timeindex][freqindex][w+1].card[permutation[bit][i]][watermark[bit][frame][w+1]] += blockcard;
					payload[timeindex][freqindex][w+2].corr[permutation[bit][i]][watermark[bit][frame][w+2]] += blockcorr;
					payload[timeindex][freqindex][w+2].card[permutation[bit][i]][watermark[bit][frame][w+2]] += blockcard;
					payload[timeindex][freqindex][w+3].corr[permutation[bit][i]][watermark[bit][frame][w+3]] += blockcorr;
					payload[timeindex][freqindex][w+3].card[permutation[bit][i]][watermark[bit][frame][w+3]] += blockcard;
				} 
			}
		}
	}

	// find the largest correlation among all watermarks.
	// memorize the watermark (counter) and the attackers scaling
	// (timeINDEX and freqINDEX)
	counter = -1;
	maximum = 0;
	// computing sum of correlation squares
	for (w = 0; w < NWATERMARKS; w++) {
		for (bit = 0; bit < BITSPERWINDOW; bit++) {
			// correlation test. note that here we compute the
			// ssum(1)/card(1) - sum(0)/card(0) test.
			// it is much faster and as accurate as 
			// the normalized standard corr test.
			// i have a matlab code thatgivesbetter results than
			// the standard corr test (itis in the matlab files that
			// i have sent to you.
			temp = payload[timeindex][freqindex][w].corr[bit][1]/payload[timeindex][freqindex][w].card[bit][1];
			temp -= payload[timeindex][freqindex][w].corr[bit][0]/payload[timeindex][freqindex][w].card[bit][0];
			// compute squares of correlations
			// payload[timeindex][freqindex][w].sumsquares += temp*temp/(4*OFFSET*OFFSET);
			// or compute abs of correlations
			payload[timeindex][freqindex][w].sumsquares += (float) (fabs(temp)/(2*OFFSET));
			// ZEROMEAN computation - subtract the mean AVE line625 insteadof line 623.
			// payload[timeindex][freqindex][w].sumsquares += fabs(temp-ave)/(2*OFFSET);
			// or compute the intact correlations (payload has to be 0x0
			// in order for this option to be used
			// payload[timeindex][freqindex][w].sumsquares += temp/(2*OFFSET);
		}
		payload[timeindex][freqindex][w].sumsquares /= BITSPERWINDOW;
		// search for the maximum correlation
		if (payload[timeindex][freqindex][w].sumsquares > maximum) {
			counter = w;
			maximum = payload[timeindex][freqindex][w].sumsquares;
		}
	}

	// Normalized correlation
	// Computing the mean of all partial correlations
	wmean = 0.;
	for (w = 0; w < NWATERMARKS; w++) 
		wmean += payload[timeindex][freqindex][w].sumsquares;
	wmean /= NWATERMARKS;
	// Computing the standard deviation of all partial correlations
	wstd = 0.;
	for (w = 0; w < NWATERMARKS; w++) 
		wstd += (payload[timeindex][freqindex][w].sumsquares - wmean)*(payload[timeindex][freqindex][w].sumsquares - wmean);
	wstd = (float) sqrt(wstd/NWATERMARKS);
	// normalizing the maximal correlation value in order to reflect
	// the standard deviation of computed normalizedcorrelations for all tests 
	payload[timeindex][freqindex][counter].sumsquares = (maximum - wmean) / wstd;

	return(counter);
}
