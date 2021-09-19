/*
 * Should calculate somewhat accurately the average kill time of the different mobs in the fight caves 
 * where you would typically have to craws bow a first hit then bp the rest.
 * Meant for comarison between Craws Bow and Bowfa for first hit, every subsequent hit is blowpipe max range ddarts. 
 * These are all on-task numbers..
 *
 * by Bop
 */

import java.util.*;

class BowfaVsCraws {
  // my stats..
  int BowfaMAR = 61032;
  int BowfaMax = 51;
  int BowfaSpeed = 4;

  int CrawsMAR = 40825;
  int CrawsMax = 34;
  int CrawsSpeed = 3;

  // i forgot to implement the -4 range acc on bp when using crystal, but whatever
  int BlowpipeMAR = 33476;
  int BlowpipeMax = 33;
  int BlowpipeSpeed = 3;

  // npc stats.
  int BatMDR = 1536; int BatHP = 10;
  int BlobMDR = 2496; int BlobHP = 20;
  int RangeMDR = 4416; int RangeHP = 40;
  int MeleeMDR = 8256; int MeleeHP = 80;



  public int hp = 5000;
  public int killTime = 0;

  Random rand = new Random(System.nanoTime());
  public BowfaVsCraws(){

  }

  public double calcAccuracy(int MAR, int MDR) {
    double accuracy;
    if (MAR > MDR) {
      accuracy = (1 - ((MDR + 2) / (2 * (MAR + 1))));
    } else {
      accuracy = (MAR / (2 * (MDR + 1)));
    }
    return accuracy;
  }

  public void BowfaHit(int mob) {
    // 0 = bat, 1 = blob, 2 = ranger, 3 = meleer
    double accuracy;
    double randomNumber;
    if (mob == 0) {
      hp = BatHP;
      accuracy = calcAccuracy(BowfaMAR, BatMDR);
      randomNumber = Math.random();
      if (accuracy > randomNumber) {
        hp -= rand.nextInt(BowfaMax + 1);
      }
    } else if (mob == 1) {
      hp = BlobHP;
      accuracy = calcAccuracy(BowfaMAR, BlobMDR);
      randomNumber = Math.random();
      if (accuracy > randomNumber) {
        hp -= rand.nextInt(BowfaMax + 1);
      }
    } else if (mob == 2) {
      hp = RangeHP;
      accuracy = calcAccuracy(BowfaMAR, RangeMDR);
      randomNumber = Math.random();
      if (accuracy > randomNumber) {
        hp -= rand.nextInt(BowfaMax + 1);
      }
    } else if (mob == 3 ) {
      hp = MeleeHP;
      accuracy = calcAccuracy(BowfaMAR, MeleeMDR);
      randomNumber = Math.random();
      if (accuracy > randomNumber) {
        hp -= rand.nextInt(BowfaMax + 1);
      }
    }
    killTime += BowfaSpeed;
  }

  public void CrawsHit(int mob) {
    // 0 = bat, 1 = blob, 2 = ranger, 3 = meleer
    double accuracy;
    double randomNumber;
    if (mob == 0) {
      hp = BatHP;
      accuracy = calcAccuracy(CrawsMAR, BatMDR);
      randomNumber = Math.random();
      if (accuracy > randomNumber) {
        hp -= rand.nextInt(CrawsMax + 1);
      }
    } else if (mob == 1) {
      hp = BlobHP;
      accuracy = calcAccuracy(CrawsMAR, BlobMDR);
      randomNumber = Math.random();
      if (accuracy > randomNumber) {
        hp -= rand.nextInt(CrawsMax + 1);
      }
    } else if (mob == 2) {
      hp = RangeHP;
      accuracy = calcAccuracy(CrawsMAR, RangeMDR);
      randomNumber = Math.random();
      if (accuracy > randomNumber) {
        hp -= rand.nextInt(CrawsMax + 1);
      }
    } else if (mob == 3 ) {
      hp = MeleeHP;
      accuracy = calcAccuracy(CrawsMAR, MeleeMDR);
      randomNumber = Math.random();
      if (accuracy > randomNumber) {
        hp -= rand.nextInt(CrawsMax + 1);
      }
    }
    killTime += CrawsSpeed;
  }

  public void BlowpipeHit(int mob) {
    // 0 = bat, 1 = blob, 2 = ranger, 3 = meleer
    double accuracy;
    double randomNumber;
    if (mob == 0) {
      accuracy = calcAccuracy(BlowpipeMAR, BatMDR);
      randomNumber = Math.random();
      if (accuracy > randomNumber) {
        hp -= rand.nextInt(BlowpipeMax + 1);
      }
    } else if (mob == 1) {
      accuracy = calcAccuracy(BlowpipeMAR, BlobMDR);
      randomNumber = Math.random();
      if (accuracy > randomNumber) {
        hp -= rand.nextInt(BlowpipeMax + 1);
      }
    } else if (mob == 2) {
      accuracy = calcAccuracy(BlowpipeMAR, RangeMDR);
      randomNumber = Math.random();
      if (accuracy > randomNumber) {
        hp -= rand.nextInt(BlowpipeMax + 1);
      }
    } else if (mob == 3 ) {
      accuracy = calcAccuracy(BlowpipeMAR, MeleeMDR);
      randomNumber = Math.random();
      if (accuracy > randomNumber) {
        hp -= rand.nextInt(BlowpipeMax + 1);
      }
    }
    killTime += BlowpipeSpeed;
  }

  public int simulateKill(int weapon, int mob) {
    //Bowfa = 0, Craws = 1
    if (weapon == 0) {
      BowfaHit(mob);
      while (hp > 0) {
        BlowpipeHit(mob);
      }
      return killTime;
    }
    if (weapon == 1) {
      CrawsHit(mob);
      while (hp > 0) {
        BlowpipeHit(mob);
      }
      return killTime;
    }
    return -1;
  }

  public static void main(String[] args) {
    int runs = 1000001;
    BowfaVsCraws BC = new BowfaVsCraws();

    ArrayList<Integer> BowfaBat = new ArrayList<Integer>();
    ArrayList<Integer> CrawsBat = new ArrayList<Integer>();

    ArrayList<Integer> BowfaBlob = new ArrayList<Integer>();
    ArrayList<Integer> CrawsBlob = new ArrayList<Integer>();

    ArrayList<Integer> BowfaRange = new ArrayList<Integer>();
    ArrayList<Integer> CrawsRange = new ArrayList<Integer>();

    ArrayList<Integer> BowfaMelee = new ArrayList<Integer>();
    ArrayList<Integer> CrawsMelee = new ArrayList<Integer>();

    for (int i = 0; i < runs; i++) {
      BC.simulateKill(0,0);
      BowfaBat.add(BC.killTime);
      BC.killTime = 0;

      BC.simulateKill(1,0);
      CrawsBat.add(BC.killTime);
      BC.killTime = 0;

      BC.simulateKill(0,1);
      BowfaBlob.add(BC.killTime);
      BC.killTime = 0;

      BC.simulateKill(1,1);
      CrawsBlob.add(BC.killTime);
      BC.killTime = 0;

      BC.simulateKill(0,2);
      BowfaRange.add(BC.killTime);
      BC.killTime = 0;

      BC.simulateKill(1,2);
      CrawsRange.add(BC.killTime);
      BC.killTime = 0;

      BC.simulateKill(0,3);
      BowfaMelee.add(BC.killTime);
      BC.killTime = 0;

      BC.simulateKill(1,3);
      CrawsMelee.add(BC.killTime);
      BC.killTime = 0;
    }

    double totalTime = 0;
    for (double d: BowfaBat) {
      totalTime += d;
    }
    double average = totalTime / runs;

    System.out.println("Mean average bowfabat: \t\t\t" + average + " ticks.");
    Collections.sort(BowfaBat);
    double median = (BowfaBat.get((int)Math.floor(runs / 2))
                    + BowfaBat.get((int)Math.ceil(runs / 2))) / 2;
    System.out.println("Median average bowfabat: \t\t" + median + " ticks.");

    totalTime = 0;
    for (double d: CrawsBat) {
      totalTime += d;
    }
    average = totalTime / runs;

    System.out.println("Mean average crawsbat: \t\t\t" + average + " ticks.");
    Collections.sort(CrawsBat);
    median = (CrawsBat.get((int)Math.floor(runs / 2))
                    + CrawsBat.get((int)Math.ceil(runs / 2))) / 2;
    System.out.println("Median average crawsbat: \t\t" + median + " ticks.");

    totalTime = 0;
    for (double d: BowfaBlob) {
      totalTime += d;
    }
    average = totalTime / runs;
    System.out.println("Mean average bowfablob: \t\t" + average + " ticks.");
    Collections.sort(BowfaBlob);
    median = (BowfaBlob.get((int)Math.floor(runs / 2))
                    + BowfaBlob.get((int)Math.ceil(runs / 2))) / 2;
    System.out.println("Median average bowfablob: \t\t" + median + " ticks.");

    totalTime = 0;
    for (double d: CrawsBlob) {
      totalTime += d;
    }
    average = totalTime / runs;

    System.out.println("Mean average crawsblob: \t\t" + average + " ticks.");
    Collections.sort(CrawsBlob);
    median = (CrawsBlob.get((int)Math.floor(runs / 2))
                    + CrawsBlob.get((int)Math.ceil(runs / 2))) / 2;
    System.out.println("Median average crawsblob: \t\t" + median + " ticks.");

    totalTime = 0;
    for (double d: BowfaRange) {
      totalTime += d;
    }
    average = totalTime / runs;

    System.out.println("Mean average bowfarange: \t\t" + average + " ticks.");
    Collections.sort(BowfaRange);
    median = (BowfaRange.get((int)Math.floor(runs / 2))
                    + BowfaRange.get((int)Math.ceil(runs / 2))) / 2;
    System.out.println("Median average bowfarange: \t\t" + median + " ticks.");

    totalTime = 0;
    for (double d: CrawsRange) {
      totalTime += d;
    }
    average = totalTime / runs;

    System.out.println("Mean average crawsrange: \t\t" + average + " ticks.");
    Collections.sort(CrawsRange);
    median = (CrawsRange.get((int)Math.floor(runs / 2))
                    + CrawsRange.get((int)Math.ceil(runs / 2))) / 2;
    System.out.println("Median average crawsrange: \t\t" + median + " ticks.");

    totalTime = 0;
    for (double d: BowfaMelee) {
      totalTime += d;
    }
    average = totalTime / runs;

    System.out.println("Mean average bowfamelee: \t\t" + average + " ticks.");
    Collections.sort(BowfaMelee);
    median = (BowfaMelee.get((int)Math.floor(runs / 2))
                    + BowfaMelee.get((int)Math.ceil(runs / 2))) / 2;
    System.out.println("Median average bowfamelee: \t\t" + median + " ticks.");

    totalTime = 0;
    for (double d: CrawsMelee) {
      totalTime += d;
    }
    average = totalTime / runs;

    System.out.println("Mean average crawsmelee: \t\t" + average + " ticks.");
    Collections.sort(CrawsMelee);
    median = (CrawsMelee.get((int)Math.floor(runs / 2))
                    + CrawsMelee.get((int)Math.ceil(runs / 2))) / 2;
    System.out.println("Median average crawsmelee: \t\t" + median + " ticks.");
  }
}
