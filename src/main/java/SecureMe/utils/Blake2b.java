package SecureMe.utils;

import java.util.*;


public class Blake2b{

	private int cbHashLen;
	private Long[] IV;
	private Long[] h; 


	/**
	 * BLAKE2b is a cryptographic hash function which operates on blocks of data of 128 bytes. 
	 * The algorithm uses a predefined message mixing schedule, SIGMA, which is an array of 16-element integer arrays. 
	 * BLAKE2b uses 12 rounds of message mixing, and for each round, a different subset of the SIGMA array is used.
	 */
	private static final int[][] SIGMA = {
	    {0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14, 15},
	    {14, 10,  4,  8,  9, 15, 13,  6,  1, 12,  0,  2, 11,  7,  5,  3},
	    {11,  8, 12,  0,  5,  2, 15, 13, 10, 14,  3,  6,  7,  1,  9,  4},
	    { 7,  9,  3,  1, 13, 12, 11, 14,  2,  6,  5, 10,  4,  0, 15,  8},
	    { 9,  0,  5,  7,  2,  4, 10, 15, 14,  1, 11, 12,  6,  8,  3, 13},
	    { 2, 12,  6, 10,  0, 11,  8,  3,  4, 13,  7,  5, 15, 14,  1,  9},
	    {12,  5,  1, 15, 14, 13,  4, 10,  0,  7,  6,  3,  9,  2,  8, 11},
	    {13, 11,  7, 14, 12,  1,  3,  9,  5,  0, 15,  4,  8,  6,  2, 10},
	    { 6, 15, 14,  9, 11,  3,  0,  8, 12,  2, 13,  7,  1,  4, 10,  5},
	    {10,  2,  8,  4,  7,  6,  1,  5, 15, 11,  9, 14,  3, 12, 13,  0}
	};






	public Blake2b(int cbHashLen)
	{
		//Desired hash length in bytes
		this.cbHashLen=cbHashLen;

		init();
	}

	private void init(){
		//Initialize State vector h with IV

		IV = new Long[] {
		    0x6A09E667F3BCC908L, 0xBB67AE8584CAA73BL, 0x3C6EF372FE94F82BL, 0xA54FF53A5F1D36F1L,
		    0x510E527FADE682D1L, 0x9B05688C2B3E6C1FL, 0x1F83D9ABFB41BD6BL, 0x5BE0CD19137E2179L
		};

		h=new Long[IV.length];
		for(int i=0;i<IV.length;i++)
			h[i]=IV[i];

	}

	/**
	 * Computes the BLAKE2 hash of an input message with an optional key.
	 *
	 * @param key           The optional key to use for the hash computation.
	 *                      Can be null or empty to indicate no key.
	 * @param M      The input message to be hashed.
	 * @return              The computed hash value as a byte array.
	 */

	public byte[] hash(byte[] key,byte[] M)
	{
		//System.out.println("orig "+Arrays.toString(h));
		int cbKeyLen;
		if(key==null)
			cbKeyLen=0;
		else
			cbKeyLen=key.length;

		//Mix key size (cbKeyLen) and desired hash length (cbHashLen) into h0
		long h0 = IV[0];
		h0 ^= 0x01010000L | ((long)cbHashLen << 8) | cbKeyLen;
		h[0] = h0;

		//System.out.println(" after mix "+Arrays.toString(h));

		int cBytesCompressed = 0;
    	int cBytesRemaining = M.length;

    	//If there was a key supplied (i.e. cbKeyLen > 0)
		//then pad with trailing zeros to make it 128-bytes (i.e. 16 words) and prepend it to the message M
    	if (cbKeyLen > 0) {
		    byte[] paddedKey = new byte[128];
		    System.arraycopy(key, 0, paddedKey, 0, Math.min(cbKeyLen, 128));
		    int remaining = Math.max(0, 128 - cbKeyLen);
		    if (remaining > 0) {
		        Arrays.fill(paddedKey, cbKeyLen, 128, (byte) 0);
		    }
		    System.arraycopy(M, 0, paddedKey, cbKeyLen, Math.min(cBytesRemaining, remaining));
		    M = paddedKey;
		    cBytesRemaining += remaining;
		}
	    //System.out.println(new String(M));


	    // Compress whole 128-byte chunks of message, except last chunk
	    while (cBytesRemaining > 128) {
	        byte[] chunk = Arrays.copyOfRange(M, cBytesCompressed, cBytesCompressed + 128);
	        cBytesCompressed += 128;
	        cBytesRemaining -= 128;
	        h = compress(h, chunk, cBytesCompressed, false);
	        //System.out.println("compressiong"+Arrays.toString(h));
	    }



	    // Compress the final bytes from M
		byte[] chunk = Arrays.copyOfRange(M, cBytesCompressed, cBytesCompressed + cBytesRemaining);
		cBytesCompressed += cBytesRemaining;

		// Pad chunk with zeros if necessary
		if (cBytesRemaining < 128) {
		    byte[] paddedChunk = new byte[128];
		    System.arraycopy(chunk, 0, paddedChunk, 0, cBytesRemaining);
		    chunk = paddedChunk;
		}

		// Compress the final chunk of data
		h = compress(h, chunk, cBytesCompressed, true);

	    


		//System.out.println(Arrays.toString(h));


		 // Finalize hash value
	    byte[] result = new byte[cbHashLen];

	    for (int i = 0; i < cbHashLen; i++) {
	        result[cbHashLen - i - 1] = (byte)(h[(i/8)%8] >>> (8 * (i % 8)));
	    }

	    // Encode result in Base64
	    //result= Base64.getEncoder().encode(result);
		return result;
	}



	/**
	 * Computes a single block of the BLAKE2b hash of an input message.
	 *
	 * @param h             The current state vector of the hash computation.
	 * @param chunk         The 128-byte message block to compress.
	 * @param t             The count of bytes that have been fed into the hash computation.
	 * @param isLastBlock   A flag indicating if this is the last block of the input message.
	 * @return              The updated state vector after compressing the input block.
	 */
	private Long[] compress(Long[] h, byte[] chunk, long t, boolean isLastBlock) {
	    
	    // Setup local work vector V
		Long[] V = new Long[16];

		// Copy first eight items from persistent state vector h
		System.arraycopy(h, 0, V, 0, 8);

		// Initialize remaining eight items from IV
		System.arraycopy(IV, 0, V, 8, 8);

		// Mix the 128-bit counter t into V12:V13
		V[12] ^= t & 0xFFFFFFFFFFFFFFFFL;  // Lo 64 bits of t
		V[13] ^= (t >>> 64) & 0xFFFFFFFFFFFFFFFFL;  // Hi 64 bits of t

		// If this is the last block then invert all the bits in V14
		if (isLastBlock) {
		    V[14] ^= 0xFFFFFFFFFFFFFFFFL;
		}

		if (chunk.length != 128) {
		    throw new IllegalArgumentException("Input chunk must be exactly 128 bytes long");
		}


		// Treat each 128-byte message chunk as sixteen 8-byte (64-bit) words m
		long[] m = new long[16];
		for (int i = 0; i < 16; i++) {
		    int offset = i * 8;
		    long word = 0;
		    for (int j = 0; j < 8; j++) {
		        word |= ((long)chunk[offset + j] & 0xFF) << (j * 8);
		    }
		    m[i] = word;
		}


		// Twelve rounds of cryptographic message mixing
		for (int i = 0; i < 12; i++) {
		    // Select message mixing schedule for this round.
		    // BLAKE2b uses 12 rounds, while SIGMA has only 10 entries.
		    int[] S = SIGMA[i % 10];

		    //System.out.println("len M is "+m.length);
		    // Mix the words using the selected schedule
		    mix(V, 0, 4, 8, 12, m[S[0]], m[S[1]]);
		    mix(V, 1, 5, 9, 13, m[S[2]], m[S[3]]);
		    mix(V, 2, 6, 10, 14, m[S[4]], m[S[5]]);
		    mix(V, 3, 7, 11, 15, m[S[6]], m[S[7]]);
		    mix(V, 0, 5, 10, 15, m[S[8]], m[S[9]]);
		    mix(V, 1, 6, 11, 12, m[S[10]], m[S[11]]);
		    //System.out.println(" wat is "+S.length);
		    //System.out.println(" wat is "+S[12]+" vs "+S[13]);
		    mix(V, 2, 7, 8, 13, m[S[12]], m[S[13]]);
		    mix(V, 3, 4, 9, 14, m[S[14]], m[S[15]]);
		}

		// Mix the upper and lower halves of V into ongoing state vector h
		for (int i = 0; i < 8; i++) {
		    h[i] ^= V[i];
		    h[i] ^= V[i + 8];
		}

		// Result is updated persistent state vector h
		return h;

	}




	/**
	 * Mixes four 8-byte word entries from the work vector V with two 8-byte word entries from the message m.
	 *
	 * @param V  The work vector.
	 * @param a  The index of the first word to mix.
	 * @param b  The index of the second word to mix.
	 * @param c  The index of the third word to mix.
	 * @param d  The index of the fourth word to mix.
	 * @param x  The first message word.
	 * @param y  The second message word.
	 */
	private static void mix(Long[] V, int a, int b, int c, int d, long x, long y) {
	    V[a] += V[b] + x;
	    V[d] = Long.rotateRight(V[d] ^ V[a], 32);
	    V[c] += V[d];
	    V[b] = Long.rotateRight(V[b] ^ V[c], 24);
	    V[a] += V[b] + y;
	    V[d] = Long.rotateRight(V[d] ^ V[a], 16);
	    V[c] += V[d];
	    V[b] = Long.rotateRight(V[b] ^ V[c], 63);
	}











}