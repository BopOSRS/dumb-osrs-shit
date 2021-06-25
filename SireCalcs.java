import java.util.*;

public class SireCalcs {
  // 0 = arc, 1 = scythe
  int weapon;
  // 0 = claws, 1 = dwh;
  int spec;

  // If inq, manually set MAR_crush.
  boolean inq = false;

  int numSpecs = 4;
  int arclightMax = 62;
  int scytheMax = 53;
  int dwhMax = 87;
  int clawMax = 49; // 48 spec ++++

  int arcAcc = 77;
  int clawAcc = 94;
  int dwhAcc = 160;
  int scytheAcc = 147;

  // Tickspeed each weapon
  int arcSpeed = 4; // 4 * 0.6
  int scytheSpeed = 5; // 5 * 0.6
  int dwhSpeed = 6; // 6 * 0.6
  int clawSpeed = 4; // 4 * 0.6

  // Defensive stats sire
  int defLvl = 250;
  int stabDef = 40;
  int slashDef = 60;
  int crushDef = 50;
  public int hp = 400;
  // If sub100hp, all defBonuses divide by 2

  double MAR_stab, MAR_slash, MAR_crush;
  double MDR_stab, MDR_slash, MDR_crush;

  double accuracy;
  double killTime = 0.0;

  Random rand = new Random(System.nanoTime());

  public SireCalcs() {
    //this.spec = spec;
    //this.weapon = weapon;
  }

  public double findMaxAttackRoll(boolean special) {
    if (special == false) {
      if (weapon == 0) {
        // Weapon arclight, style = controlled
        MAR_stab = Math.floor(Math.floor(((Math.floor(118 * 1.20) + 1 + 8) * (arcAcc + 64)) * 7/6) * 1.7);
        return MAR_stab;
      } else if (weapon == 1) {
        // Weapon scythe, style = aggressive
        MAR_slash = Math.floor(Math.floor((Math.floor(118 * 1.20) + 0 + 8) * (scytheAcc + 64)) * 7/6);
        return MAR_slash;
      }
    } else if (special == true) {
      if (spec == 0) {
        MAR_slash = Math.floor(Math.floor((Math.floor(118 * 1.20) + 0 + 8) * (clawAcc + 64)) * 7/6);
        return MAR_slash;
      } else if (spec == 1) {
        MAR_crush = Math.floor(Math.floor((Math.floor(118 * 1.20) + 3 + 8) * (dwhAcc + 64)) * 7/6);
        if (inq) {
          MAR_crush = 44417;
          //If inq + tyr, uncomment next line
          //MAR_crush = 45850;
        }
        return MAR_crush;
      }
    }
    return -1;
  }

  public double findMaxDefRoll(int attackStyle) {
    // 0 = stab, 1 = slash, 2 = crush
    boolean sub139 = false;
    if (hp < 139) {
      sub139 = true;
    }
    if (!sub139) {
      if (attackStyle == 0) {
        MDR_stab = Math.floor((defLvl + 9) * (stabDef + 64));
        return MDR_stab;
      } else if (attackStyle == 1) {
        MDR_slash = Math.floor((defLvl + 9) * (slashDef + 64));
        return MDR_slash;
      } else if (attackStyle == 2) {
        MDR_crush = Math.floor((defLvl + 9) * (crushDef + 64));
        return MDR_crush;
      }
    } else if (sub139) {
      if (attackStyle == 0) {
        MDR_stab = Math.floor((defLvl + 9) * ((stabDef / 2) + 64));
        return MDR_stab;
      } else if (attackStyle == 1) {
        MDR_slash = Math.floor((defLvl + 9) * ((slashDef / 2) + 64));
        return MDR_slash;
      } else if (attackStyle == 2) {
        MDR_crush = Math.floor((defLvl + 9) * ((crushDef / 2) + 64));
        return MDR_crush;
        // This should never run.
      }
    }
    return -1;
  }

  public int randomHit(double lower, double upper) {
    int hit = (int)(rand.nextInt((int)upper - (int)lower) + lower);
    //System.out.println("Random number between " + lower + " and " + upper + " is " + hit);
    return hit;
  }

  public void clawSpec() {
    this.spec = 0;
    findMaxAttackRoll(true);
    findMaxDefRoll(1);
    double accuracy = calcAccuracy(1);
    double randomNumber;
    int totalHit = 0;
    this.killTime += clawSpeed;
    for (int i = 0; i < 4; i++) {
      randomNumber = Math.random();
      if (accuracy > randomNumber) {
        if (i == 0) {
          int hit = randomHit(Math.floor(clawMax / 2), clawMax);
          totalHit = (int) (hit + Math.floor(hit / 2) + (2 * Math.floor(hit / 4)) + 1);
          if (((hp >= 200) && ((hp - (hit + Math.floor(hit / 2) + Math.floor(hit / 4))) < 200)) ||
              ((hp > 139) && ((hp - (hit + Math.floor(hit / 2) + Math.floor(hit / 4))) <= 139))) {
            killTime --;
          }
          hp -= totalHit;
          return;
        } else if (i == 1) {
          int hit = randomHit(Math.floor((3 * clawMax) / 8), Math.floor((7 * clawMax) / 8) + 1);
          totalHit = (int) (hit + 2 * Math.floor(hit / 2) + 1);
          if (((hp >= 200) && ((hp - (hit + Math.floor(hit / 2))) < 200)) ||
              ((hp > 139) && ((hp - (hit + Math.floor(hit / 2))) <= 139))) {
            killTime --;
          }
          hp -= totalHit;
          return;
        } else if (i == 2) {
          int hit = randomHit(Math.floor(clawMax / 4), Math.floor((3 * clawMax) / 4));
          totalHit = 2 * hit + 1;
          if (((hp >= 200) && ((hp - hit) < 200)) ||
              ((hp > 139) && ((hp - hit) <= 139))) {
            killTime --;
          }
          hp -= totalHit;
          return;
        } else if (i == 3) {
          int hit = randomHit(Math.floor(clawMax / 4), Math.floor((5 * clawMax) / 4)) + 1;
          hp -= hit;
          return;
        }
      }
    }
    if (Math.random() > 0.5) {
      hp -= 2;
      return;
    }
    return;
  }

  public void DWHSpec() {
    this.spec = 1;
    findMaxAttackRoll(true);
    findMaxDefRoll(2);
    double accuracy = calcAccuracy(2);
    double randomNumber;
    int hit = 0;
    this.killTime += dwhSpeed;
    randomNumber = Math.random();
    if (accuracy > randomNumber) {
      hit = rand.nextInt(dwhMax + 1);
      if (hit > 0) {
        defLvl = (int)Math.ceil(defLvl * 0.7);
        hp -= hit;
        this.spec = 0;
      }
    }
  }

  public double calcAccuracy(int atkStyle) {
    double MAR = 0;
    double MDR = 0;
    // 0 = stab, 1 = slash, 2 = crush
    if (atkStyle == 0) {
      MAR = MAR_stab;
      MDR = MDR_stab;
    } else if (atkStyle == 1) {
      MAR = MAR_slash;
      MDR = MDR_slash;
    } else if (atkStyle == 2) {
      MAR = MAR_crush;
      MDR = MDR_crush;
    }

    if (MAR > MDR) {
      accuracy = (1 - ((MDR + 2) / (2 * (MAR + 1))));
    } else {
      accuracy = (MAR / (2 * (MDR + 1)));
    }
    return accuracy;
  }

  public void scytheSwing(double accuracy) {
    int hit = 0;
    if (accuracy > Math.random()) {
      hit += rand.nextInt((int)scytheMax + 1);
    }
    if (accuracy > Math.random()) {
      hit += rand.nextInt((int)Math.floor(scytheMax / 2) + 1);
    }
    if (((hp >= 200) && ((hp - hit) < 200)) ||
        ((hp > 139) && ((hp - hit) <= 139)) ||
        ((hp > 0) && ((hp - hit) <= 0))) {
      // :stoleatick:
      killTime --;
    }
    if (accuracy > Math.random()) {
      if ((Math.random() > (1/5)) || (hp > 200)) { // Add chance of hitting minion in place of 0 in this line .. Ex (1/8)
        hit += rand.nextInt((int)Math.floor(scytheMax / 4) + 1);
      }
    }
    hp -= hit;
  }

  public void simulateKill(int caseID) {
    if (caseID == 0) { // Arclight claws no scythe
      this.weapon = 0;
      this.spec = 0;
      while (numSpecs > 0) {
        clawSpec();
        numSpecs --;
      }
      findMaxAttackRoll(false);
      findMaxDefRoll(0);
      double accuracy = calcAccuracy(0);
      //System.out.println("Case 0 acc +139 hp " + accuracy);
      boolean subtwohundred = false;
      if (hp < 200) {
        subtwohundred = true;
        killTime -= clawSpeed;
        killTime += 19;
        if (hp <= 139) {
          killTime += arcSpeed;
        }
      }
      while (hp > 139) {
        killTime += arcSpeed;
        if (accuracy > Math.random()) {
          int hit = rand.nextInt(arclightMax + 1);
          hp -= hit;
        }
        if ((hp < 200) && (subtwohundred == false)) {
          killTime -= arcSpeed;
          subtwohundred = true;
          killTime += 19;
          if (hp <= 139) {
            // Just in case, happens VERY rarely.
            killTime += arcSpeed;
          }
        }
      }
      killTime -= arcSpeed;
      findMaxAttackRoll(false);
      findMaxDefRoll(0);
      accuracy = calcAccuracy(0);
      //System.out.println("Case 0 acc -139 hp " + accuracy);
      killTime += 9;
      while (hp > 0) {
        killTime += arcSpeed;
        if (accuracy > Math.random()) {
          int hit = rand.nextInt(arclightMax + 1);
          hp -= hit;
        }
      }
    }

    else if (caseID == 1) { // Scythe with inquisitor
      this.weapon = 1;
      this.spec = 1;
      this.inq = true;
      while (numSpecs > 0) {
        if (defLvl == 250) {
          DWHSpec();
          numSpecs --;
        } else if (defLvl < 250) {
          clawSpec();
          numSpecs --;
        }
      }
      findMaxAttackRoll(false);
      findMaxDefRoll(1);
      double accuracy = calcAccuracy(1);
      //System.out.println("Case 1 acc +139 hp " + accuracy);
      boolean subtwohundred = false;
      if (hp < 200) {
        subtwohundred = true;
        killTime -= clawSpeed;
        killTime += 19;
        if (hp <= 139) {
          //Just because I manually add -= scytheSpeed after the while-loop is done.
          killTime += scytheSpeed;
        }
      }
      while (hp > 139) {
        killTime += scytheSpeed;
        scytheSwing(accuracy);
        if ((hp < 200) && (subtwohundred == false)) {
          killTime -= scytheSpeed;
          subtwohundred = true;
          killTime += 19;
          if (hp <= 139) {
            //Just because I manually add -= scytheSpeed after the while-loop is done.
            killTime += scytheSpeed;
          }
        }
      }
      killTime -= scytheSpeed;
      findMaxAttackRoll(false);
      findMaxDefRoll(1);
      accuracy = calcAccuracy(1);
      //System.out.println("Case 1 acc -139 hp " + accuracy);
      killTime += 9;
      while (hp > 0) {
        killTime += scytheSpeed;
        scytheSwing(accuracy);
      }
    }

    else if (caseID == 2) { // Arclight claws + scythe sub139 hp
      this.weapon = 0;
      this.spec = 0;

      while (numSpecs > 0) {
        clawSpec();
        numSpecs --;
      }
      findMaxAttackRoll(false);
      findMaxDefRoll(0);
      double accuracy = calcAccuracy(0);
      //System.out.println("Case 2 acc +139 hp " + accuracy);
      boolean subtwohundred = false;
      if (hp < 200) {
        subtwohundred = true;
        killTime -= clawSpeed;
        killTime += 19;
        if (hp <= 139) {
          //Just because I manually add -= arcSpeed after the while-loop is done.
          killTime += arcSpeed;
        }
      }
      while (hp > 139) {
        killTime += arcSpeed;
        if (accuracy > Math.random()) {
          int hit = rand.nextInt(arclightMax + 1);
          hp -= hit;
        }
        if ((hp < 200) && (subtwohundred == false)) {
          killTime -= arcSpeed;
          subtwohundred = true;
          killTime += 19;
          if (hp <= 139) {
            // This happens VERY rarely.
            killTime += arcSpeed;
          }
        }
      }
      killTime -= arcSpeed;
      this.weapon = 1;
      findMaxAttackRoll(false);
      findMaxDefRoll(1);
      accuracy = calcAccuracy(1);
      //System.out.println("Case 2 acc -139 hp " + accuracy);
      killTime += 9;
      while (hp > 0) {
        killTime += scytheSpeed;
        scytheSwing(accuracy);
      }
    }

    else if (caseID == 3) { // scythe without inquisitor
      this.weapon = 1;
      this.spec = 1;
      this.inq = false;
      while (numSpecs > 0) {
        if (defLvl == 250) {
          DWHSpec();
          numSpecs --;
        } else if (defLvl < 250) {
          clawSpec();
          numSpecs --;
        }
      }
      findMaxAttackRoll(false);
      findMaxDefRoll(1);
      double accuracy = calcAccuracy(1);
      //System.out.println("Case 1 acc +139 hp " + accuracy);
      boolean subtwohundred = false;
      if (hp < 200) {
        subtwohundred = true;
        killTime -= clawSpeed;
        killTime += 19;
        if (hp <= 139) {
          //Just because I manually add -= scytheSpeed after the while-loop is done.
          killTime += scytheSpeed;
        }
      }
      while (hp > 139) {
        killTime += scytheSpeed;
        scytheSwing(accuracy);
        if ((hp < 200) && (subtwohundred == false)) {
          killTime -= scytheSpeed;
          subtwohundred = true;
          killTime += 19;
          if (hp <= 139) {
            //Just because I manually add -= scytheSpeed after the while-loop is done.
            killTime += scytheSpeed;
          }
        }
      }
      killTime -= scytheSpeed;
      findMaxAttackRoll(false);
      findMaxDefRoll(1);
      accuracy = calcAccuracy(1);
      //System.out.println("Case 1 acc -139 hp " + accuracy);
      killTime += 9;
      while (hp > 0) {
        killTime += scytheSpeed;
        scytheSwing(accuracy);
      }
    }
  }


  public static void main(String[] args) {
    SireCalcs sc = new SireCalcs();
    ArrayList<Double> killTimes = new ArrayList<Double>();
    int runs = 1;
    try {
      runs = Integer.parseInt(args[0]);
      if (runs < 0) {
        throw new Exception();
      }
    } catch (Exception e) {
      System.out.println("Need parameter for amount of runs program should do.");
      System.out.println("'java SireCalcs <n>' where n is the amount of kills the program should sim");
      return;
    }
    //long timeTaken = System.currentTimeMillis();
    for (int i = 0; i < runs; i++) {
      sc = new SireCalcs();
      sc.simulateKill(0);
      killTimes.add(sc.killTime);
    }
    double totalTime = 0;
    for (double d: killTimes) {
      totalTime += d;
    }
    double average = totalTime / killTimes.size();
    System.out.println("Mean avg. arclight kills: \t\t" + average * 0.6 + " seconds.");
    Collections.sort(killTimes);
    double median = (killTimes.get((int)Math.floor(killTimes.size() / 2))
                    + killTimes.get((int)Math.ceil(killTimes.size() / 2))) / 2;
    System.out.println("Median avg. arclight kills: \t\t" + median * 0.6 + " seconds.");
    System.out.println("------");

    ArrayList<Double> killTimesScythe = new ArrayList<Double>();
    for (int i = 0; i < runs; i++) {
      sc = new SireCalcs();
      sc.simulateKill(1);
      killTimesScythe.add(sc.killTime);
    }
    totalTime = 0;
    for (double d: killTimesScythe) {
      totalTime += d;
    }
    average = totalTime / killTimesScythe.size();
    System.out.println("Mean avg. scythe kills: \t\t" + average * 0.6 + " seconds.");
    Collections.sort(killTimesScythe);
    median = (killTimesScythe.get((int)Math.floor(killTimesScythe.size() / 2))
            + killTimesScythe.get((int)Math.ceil(killTimesScythe.size() / 2))) / 2;
    System.out.println("Median avg. scythe kills: \t\t" + median * 0.6 + " seconds.");
    System.out.println("------");

    ArrayList<Double> killTimesScytheNoInq = new ArrayList<Double>();
        for (int i = 0; i < runs; i++) {
          sc = new SireCalcs();
          sc.simulateKill(3);
          killTimesScytheNoInq.add(sc.killTime);
        }
        totalTime = 0;
        for (double d: killTimesScytheNoInq) {
          totalTime += d;
        }
        average = totalTime / killTimesScytheNoInq.size();
        System.out.println("Mean avg. scythe (no inq) kills: \t" + average * 0.6 + " seconds.");
        Collections.sort(killTimesScytheNoInq);
        median = (killTimesScytheNoInq.get((int)Math.floor(killTimesScytheNoInq.size() / 2))
                + killTimesScytheNoInq.get((int)Math.ceil(killTimesScytheNoInq.size() / 2))) / 2;
        System.out.println("Median avg. scythe (no inq) kills: \t" + median * 0.6 + " seconds.");
        System.out.println("------");

    ArrayList<Double> killTimesArcScythe = new ArrayList<Double>();
    for (int i = 0; i < runs; i++) {
      sc = new SireCalcs();
      sc.simulateKill(2);
      killTimesArcScythe.add(sc.killTime);
    }
    totalTime = 0;
    for (double d: killTimesArcScythe) {
      totalTime += d;
    }
    average = totalTime / killTimesArcScythe.size();
    System.out.println("Mean avg. arc+scythe kills: \t\t" + average * 0.6 + " seconds.");
    Collections.sort(killTimesArcScythe);
    median = (killTimesArcScythe.get((int)Math.floor(killTimesArcScythe.size() / 2))
            + killTimesArcScythe.get((int)Math.ceil(killTimesArcScythe.size() / 2))) / 2;
    System.out.println("Median avg. arc+scythe kills: \t\t" + median * 0.6 + " seconds.");
  }

}
