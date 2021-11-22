public class MyClass {
    public static long M = 0x5DEECE66DL, P = 53L, mask = (1L << 48) - 1L;
    public static int[] append(int[] ori, int ne) {
        int len = ori.length;
        int[] toRet = java.util.Arrays.copyOf(ori, len+1);
        toRet[len] = ne;
        return toRet;
    }
    public static String sbin(long l, int n) { return String.format("%" + n + "s", Long.toBinaryString(l)).replace(' ', '0'); }
    public static void main(String args[]) {
      int[] data = {35,13,51,42,38,20,15,10,33,13,49,8,11,41,52,35,23,11,40,17,33,6,3,22,10,15,2,35,49,22,38,34,24,25,7,43,25,12,42,17,3,4,7,30,6,31,4,38,5,10,3,34,51,39,41,49,15,2,39,10,23,26,16,13,38,34,17,42,9,4,16,26,14,36,1,49,2,27,42,36,6,35,27,19,44,49,7,26,17,10,13,38,0,15,11,90,169,31,184,29}; // from js
      long ma = 0L;
      int[] empty = {}, rem = empty;
      System.out.println("Predict-Phase");
      for(int i = 0; i <= 24; i++) {
          long Mrem = (((M << i) & ((1L << 48) - 1L)) >> 17);
          int Mi = ((int) (M >> i)) & 1, poss = 0;
          for(int j = 0; j <= 1; j++) {
              // is the ith bit j?
              long diff = (data[i*2+j] - data[99]) * (1 - Mi * 2) * (1 - j * 2);
              long inacc = i < 17? 1L : 0L, shift = i >= 10? (1L << 31) : 0L;
              if((diff - Mrem) % P == 0 || (diff - (Mrem + inacc)) % P == 0 || (diff - (Mrem - shift)) % P == 0 || (diff - (Mrem + inacc - shift)) % P == 0) poss |= (j + 1);
          }
          if(poss == 0) System.out.println("Error! " + i + "th bit has no possibility");
          else if(poss < 3) {
              System.out.println("Prediction: " + i + "th bit is " + (poss-1));
              if(poss == 2) ma |= (1L << i);
          } else rem = append(rem, i);
      }
      System.out.println("Predicted " + (25 - rem.length));
      for(int phase = 'N'; phase >= 'A'; phase /= 1.2) {
          System.out.println("--------------------------\n" + (phase == 'N'? "Narrow" : "Aim") + "-Phase");
          for(int i=25; i<48; i++) rem = append(rem,i);
          int len = rem.length;
          long limit = 1L << len;
          System.out.println(len + " bits to guess");
          for(long bb = 0; bb < limit; bb++) {
              long thi = ma;
              for(int i=0; i<len; i++) {
                  thi |= ((bb >> i) & 1L) << rem[i];
              }
              boolean good = true;
              for(int i=70; i<95; i++) {
                  long seed = 0x7c31L * ((i * 155683L) ^ 6272861L) + 0x7edfL * (i * i + 1) + thi;
                  java.util.Random rng = new java.util.Random(seed);
                  int rr = rng.nextInt((int) P);
                  if(rr != data[i]) {
                      good = false;
                      break;
                  }
              }
              if(good) {
                  if(phase == 'N') {
                      ma = thi & ((1L << 25) - 1);
                      System.out.println("Found some candicate; last 25 bits now fixed: " + sbin(ma, 25));
                      break;
                  }
                  long BS = (thi - 0x4878857fL) * 117921788813345L; // 1179... = c^-1 mod 2^48 (c = 0x289223e1)
                  boolean realGood = true;
                  for(int i = 95; i < 99; i++) {
                      long seed = 0x30b9L * ((i*72123L)^1359851L) + 0x5fcfL * ((i*i*i)^1087415L) + 0x3857dc53L * BS + 0x6e3bd3ddL;
                      java.util.Random rng = new java.util.Random(seed);
                      int rr = rng.nextInt(193);
                      if(rr != data[i]) {
                          realGood = false;
                          break;
                      }
                  }
                  if(realGood) {
                      System.out.println("Found (!) Random seed: " + sbin(thi, 48));
                      System.out.println("             baseseed: " + sbin(BS & mask, 48));
                      long[] seeds = {0x3857dc53L * BS + 0x6e3bd3ddL, 0x18b0a50bL * BS + 0x27d58de9L, 0x1b7a1d5bL * BS + 0x33f4f1a7L, 0x70517261L * BS + 0x3ac463fbL, 0x5b07e3b5L * BS + 0x6b85fa33L};
                      int[] Ps = {193, 1543, 24593, 393241, 25165843};
                      for(int j = 0; j < 5; j++) {
                          java.util.Random rng = new java.util.Random(seeds[j]);
                          int x = rng.nextInt(Ps[j]);
                          int y = rng.nextInt(Ps[j]);
                          System.out.println((j+3)+":"+x+","+y);
                      }
                  }
              }
          }
          rem = empty;
      }
    }
}
