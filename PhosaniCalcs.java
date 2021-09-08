import java.util.*;

public class PhosaniCalcs {
  int weapon;
  int spec;
  boolean inq = true; // why would this be false lol..
  boolean thralls = true;
  boolean deathCharge = true;
  boolean harm = false;
  boolean dwh = false;
  int mageLvl = 99;
  int timeTillLevelDecay = 150; // 150 ticks.
  int timeTillHeartRegen = 700; // 700 ticks.
  int timeTillDeathChargeSpecRegen = 100; // 100 ticks between casts.
  int timeTillSpecRegen = 50; // 50 ticks between each 10% spec regen.
  int deathChargeRegenTick;
  int heartRegenTick;
  int mageDecayTick;
  int specRegenTick;

  int bfCharges = 0;
  int scytheCharges = 0;
  int mageCasts = 0;

  boolean deathChargeActive = false;

  // Extra times.
  int phaseTransitionTime = 28; // 28 ticks between last cast of spell and first attackable tick on boss.
  int spellTravelTime = 2; // 2 ticks from cast to hit on final totem.
  int deathAnimationTime = 14;

  int specBar = 100; //
  int specsUsed = 0;
  int scytheMax = 48;
  int DWHmax = 76;
  int clawMax = 43;
  int maceMax = 53;

  int scytheAcc = 94;
  int DWHacc = 159;
  int clawAcc = 86;

  int scytheSpeed = 5;
  int DWHspeed = 6;
  int clawSpeed = 4;
  int sangSpeed = 4;
  int delayThrall = 0;

  int defLvl = 150;
  int stabDef; // Ignored.
  int slashDef = 180;
  int crushDef = 40;

  public int hp = 400;

  double MAR_stab, MAR_slash, MAR_crush;
  double MDR_stab, MDR_slash, MDR_crush;

  double accuracy;
  double killTime = 0.0;

  Random rand = new Random(System.nanoTime());

  public PhosaniCalcs() {
    //
  }

  public double findMaxAttackRoll(int weapon) {
    if (weapon == 0) {
      // Weapon scythe, aggressive crush.
      if (inq) {
        MAR_crush = Math.floor(Math.floor((Math.floor(118 * 1.20) + 0 + 8) * (scytheAcc + 64)) * 1.025);
      } else if (!inq) {
        MAR_crush = Math.floor(Math.floor(118 * 1.20) + 8 + 9 ) * (62 + 64);
      }
      return MAR_crush;
    } else if (weapon == 1) {
      // Weapon DWH, accurate crush
      if (inq) {
        MAR_crush = Math.floor(Math.floor((Math.floor(118 * 1.20) + 3 + 8) * (DWHacc + 64)) * 1.025);
      }
      else {
        MAR_crush = Math.floor(Math.floor(118 * 1.20) + 3 + 8) * (155 + 64);
      }
      return MAR_crush;
    } else if (weapon == 2) {
      // Weapon claws, aggressive slash
      MAR_slash = Math.floor((Math.floor(118 * 1.20) + 0 + 8) * (clawAcc + 64));
      return MAR_slash;
    }
    return -1;
  }

  public double findMaxDefRoll(int attackStyle) {
    // 0 = stab, 1 = slash, 2 = crush
    if (attackStyle == 0) {
      MDR_stab = Math.floor((defLvl + 9) * (stabDef + 64));
      return MDR_stab;
      // Skal ikke brukes.
    } else if (attackStyle == 1) {
      MDR_slash = Math.floor((defLvl + 9) * (slashDef + 64));
      return MDR_slash;
    } else if (attackStyle == 2) {
      MDR_crush = Math.floor((defLvl + 9) * (crushDef + 64));
      return MDR_crush;
    }
    return -1;
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

  public void clawSpec() {
    findMaxAttackRoll(2);
    findMaxDefRoll(1);
    specsUsed ++;
    double accuracy = calcAccuracy(1);
    double randomNumber;
    int totalHit = 0;
    for (int i = 0; i < 4; i++) {
      randomNumber = Math.random();
      if (accuracy > randomNumber) {
        if (i == 0) {
          int hit = randomHit(Math.floor(clawMax / 2), clawMax);
          totalHit = (int) (hit + Math.floor(hit / 2) + (2 * Math.floor(hit / 4)) + 1);
          bfCharges += 4;
          hp -= totalHit;
          return;
        } else if (i == 1) {
          int hit = randomHit(Math.floor((3 * clawMax) / 8), Math.floor((7 * clawMax) / 8) + 1);
          totalHit = (int) (hit + 2 * Math.floor(hit / 2) + 1);
          bfCharges += 3;
          hp -= totalHit;
          return;
        } else if (i == 2) {
          int hit = randomHit(Math.floor(clawMax / 4), Math.floor((3 * clawMax) / 4));
          totalHit = 2 * hit + 1;
          bfCharges += 2;
          hp -= totalHit;
          return;
        } else if (i == 3) {
          int hit = randomHit(Math.floor(clawMax / 4), Math.floor((5 * clawMax) / 4)) + 1;
          hp -= hit;
          bfCharges += 1;
          return;
        }
      }
    }
    if (Math.random() > 0.5) {
      hp -= 2;
      bfCharges += 2;
      return;
    }
    return;
  }

  public int randomHit(double lower, double upper) {
    int hit = (int)(rand.nextInt((int)upper - (int)lower) + lower);
    //System.out.println("Random number between " + lower + " and " + upper + " is " + hit);
    return hit;
  }

  public boolean regenDef() {
    if (defLvl < 150) {
      defLvl++;
      return true;
    }
    return false;
  }

  public void DWHSpec() {
    findMaxAttackRoll(1);
    findMaxDefRoll(2);
    double accuracy = calcAccuracy(2);
    double randomNumber;
    int hit = 0;
    //this.killTime += dwhSpeed;
    randomNumber = Math.random();
    if (accuracy > randomNumber) {
      hit = rand.nextInt(DWHmax + 1);
      bfCharges ++;
      if (hit > 0) {
        defLvl = 120;
        hp -= hit;
        regenDef();
      }
    }
  }

  public void scytheSwing(double accuracy) {
    int hit = 0;
    scytheCharges ++;
    if (accuracy > Math.random()) {
      hit += rand.nextInt((int)scytheMax + 1);
      bfCharges++;
    }
    if (accuracy > Math.random()) {
      hit += rand.nextInt((int)Math.floor(scytheMax / 2) + 1);
      bfCharges++;
    }
    if (accuracy > Math.random()) {
      hit += rand.nextInt((int)Math.floor(scytheMax / 4) + 1);
      bfCharges++;
    }
    hp -= hit;
  }

  public void thrallHit(boolean mage) {
    if (thralls) {
      if (delayThrall == 0) {
        int hit = rand.nextInt(3 + 1);
        if (mage) {
          hit = hit * 2;
          if (hit == 0) {
            hit = 1;
          }
        }
        hp -= hit;
      } else {
        delayThrall --;
      }
    }
  }

  public void sangHit() {
    int maxHit = 78;
    int hit = 0;
    mageCasts ++;
    if (!harm) {
      if (mageLvl >= 108) {
        maxHit = 86;
      } else if (mageLvl >= 105) {
        maxHit = 82;
      } else if (mageLvl >= 102) {
        maxHit = 80;
      } else {
        maxHit = 78;
      }
      hit = rand.nextInt((maxHit / 2) + 1);
      hit = 2 * hit;
      if (hit == 0) {
        hit = 1;
      }
    } else if (harm) {
      maxHit = 98;
      hit = rand.nextInt((maxHit / 2) + 1);
      hit = 2 * hit;
      if (hit == 0) {
        hit = 1;
      }
    }
    hp -= hit;
  }

  public void heart() {
    mageLvl = 109;
  }

  public void simulateKill() {
    int currentTick = 0;
    boolean alive = true;
    int totalSpecials = 0;
    int deadTotems;
    // First phase special happens once every 14 + 2 * 9 + 12 * 6 ticks.
    // First special either after 6 * 6 ticks or after 6 + 14 + 1 * 9 + 6 * 6 ticks.
    int aliveSpecials;
    int nextSpecialTick;
    int timeBetweenSpecials = 100;
    for (int i = 0; i < 4; i++) {
      hp = 400;
      if (dwh) {
        while ((defLvl == 150) && (specBar > 0)) {
          DWHSpec();
          killTime += DWHspeed;
          specBar -= 50;
          if (specBar == 50) {
            specRegenTick = 50;
          } else if (specBar == 0) {
            specRegenTick = 44;
          }
        }
      } else if (!dwh) {
        while (specBar > 0) {
          clawSpec();
          killTime += clawSpeed;
          specBar -= 50;
          specRegenTick = 46;
        }
      }
      timeBetweenSpecials = 100;
      if (Math.random() >= 0) {
        nextSpecialTick = (6 * 6) - (specsUsed * 4);
      } else {
        nextSpecialTick = (6 * 6 + 14 + 1 * 9 + 6 * 6) - (specsUsed * 4);
      } if (dwh) {
        nextSpecialTick -= specsUsed * 2;
      }
      if (alive) {
        findMaxAttackRoll(0);
        findMaxDefRoll(2);
        accuracy = calcAccuracy(2);
        scytheSwing(accuracy);
        thrallHit(false);
        while (hp > 0) {
          currentTick ++;
          if (currentTick == nextSpecialTick) {
            nextSpecialTick += timeBetweenSpecials;
            currentTick += 5;
            totalSpecials ++;
          } if (currentTick >= deathChargeRegenTick) {
            deathChargeRegenTick = currentTick + timeTillDeathChargeSpecRegen;
            if (deathCharge) {
              specBar += 15;
            } if (specBar > 100) {
              specBar = 100;
            }
          }
          if (currentTick % 5 == 0) {
          if (regenDef()) {
            findMaxDefRoll(2);
            accuracy = calcAccuracy(2);
            scytheSwing(accuracy);
          } else if ((specBar >= 50)) {
            clawSpec();
            killTime --;
          } else {
            scytheSwing(accuracy);
          }
        } if (currentTick % 4 == 0) {
            thrallHit(false);
          }
        } if (currentTick >= specRegenTick) {
          specBar += 10;
          specRegenTick = currentTick + timeTillSpecRegen;
          if (specBar > 100) {
            specBar = 100;
          }
        } if ((i != 0) && (mageLvl > 99) && (currentTick >= mageDecayTick)) {
          mageLvl --;
          mageDecayTick = currentTick + timeTillLevelDecay;
        }
      }
      deadTotems = 0;
      hp = 200;
      sangHit();
      thrallHit(true);
      while (deadTotems < 4) {
        currentTick ++;
        if (currentTick == nextSpecialTick) {
          nextSpecialTick += timeBetweenSpecials;
          currentTick += 6;
        } if (currentTick >= deathChargeRegenTick) {
          deathChargeRegenTick = currentTick + timeTillDeathChargeSpecRegen;
          if (deathCharge) {
            specBar += 15;
          } if (specBar > 100) {
            specBar = 100;
          }
        } if (currentTick % 4 == 0) {
          sangHit();
        } if (currentTick >= mageDecayTick) {
          mageDecayTick = currentTick + timeTillLevelDecay;
          mageLvl --;
        } if (currentTick >= specRegenTick) {
          specBar += 10;
          specRegenTick = currentTick + timeTillSpecRegen;
          if (specBar > 100) {
            specBar = 100;
          }
        } if (currentTick % 4 == 0) {
          thrallHit(true);
        } if (hp <= 0) {
          hp = 200;
          deadTotems ++;
          delayThrall = 1;
        }
      }
      specRegenTick -= phaseTransitionTime;
      if (currentTick >= specRegenTick) {
        specRegenTick = currentTick + (currentTick - specRegenTick);
        specBar += 10;
        if (specBar > 100) {
          specBar = 100;
        }
      }
      mageDecayTick -= phaseTransitionTime;
      if (currentTick >= mageDecayTick) {
        if (mageLvl > 99) {
          mageLvl --;
        }
        mageDecayTick = currentTick + (currentTick - mageDecayTick);
      }
    }
    hp = 150;
    while (hp > 0) {
      while (specBar >= 50) {
        clawSpec();
        specBar -= 50;
        killTime += 4;
      }
      currentTick ++;
      if (currentTick % 5 == 0) {
        scytheSwing(accuracy);
      } if (currentTick % 4 == 0) {
        thrallHit(false);
      }
    }
    killTime += totalSpecials;
    killTime += currentTick;
    killTime += 4 * phaseTransitionTime; // Including sleepwalker time.
    killTime += deathAnimationTime;
    killTime += 4 * spellTravelTime; // Assuming 2 ticks from cast xp drop to dead totem, per phase.
    //System.out.println("Kill done after " + killTime + " ticks.");
  }
  public static void main(String[] args) {
    System.out.println("Sanguinesti staff with thralls:");
    PhosaniCalcs NM = new PhosaniCalcs();
    ArrayList<Double> times = new ArrayList<Double>();
    int totalBfCharges = 0;
    int totalScytheCharges = 0;
    int totalSangCharges = 0;
    double totalSpecsUsed = 0;
    int runs = 1000001;
    for (int i = 0; i < runs; i++) {
      NM = new PhosaniCalcs();
      NM.simulateKill();
      times.add(NM.killTime);
      totalBfCharges += NM.bfCharges;
      totalScytheCharges += NM.scytheCharges;
      totalSangCharges += NM.mageCasts;
      totalSpecsUsed += NM.specsUsed;
    }
    double totalTime = 0;
    int sub630 = 0;
    for (double d: times) {
      totalTime += d;
      if (d < 600) {
        sub630 ++;
      }
    }
    double average = totalTime / times.size();
    //String time = (int)Math.floor((average * 0.6) / 60) + ":" + (int)((((average * 0.6)/60) - Math.floor((average * 0.6) / 60))*100*0.6);
    System.out.println("Average specs used per kill: " + totalSpecsUsed / (double)runs + ".");
    System.out.println("Average blood fury charges per kill: " + totalBfCharges / runs + ".");
    System.out.println("Average scythe charges per kill: " + totalScytheCharges / runs + ".");
    System.out.println("Average sanguinesti staff charges per kill: " + totalSangCharges / runs + ".");
    String minutes = Integer.toString((int)Math.floor((average * 0.6) / 60));
    String seconds = Integer.toString((int)((((average * 0.6)/60) - Math.floor((average * 0.6) / 60))*100*0.6));
    if (seconds.length() == 1) {
      seconds = "0" + seconds;
    }
    //System.out.println("Mean average: \t\t\t" + average * 0.6 + " seconds.");
    System.out.println("Mean average: \t\t\t" + minutes + ":" + seconds);
    Collections.sort(times);

    double median = (times.get((int)Math.floor(times.size() / 2))
                    + times.get((int)Math.ceil(times.size() / 2))) / 2;
    //time = (int)Math.floor((median * 0.6) / 60) + ":" + (int)((((median * 0.6)/60) - Math.floor((median * 0.6) / 60))*100*0.6);
    minutes = Integer.toString((int)Math.floor((median * 0.6) / 60));
    seconds = Integer.toString((int)((((median * 0.6)/60) - Math.floor((median * 0.6) / 60))*100*0.6));
    if (seconds.length() == 1) {
      seconds = "0" + seconds;
    }
    //System.out.println("Median average: \t\t" + median * 0.6 + " seconds.");
    System.out.println("Median average: \t\t" + minutes + ":" + seconds);

    //time = (int)Math.floor((times.get(0) * 0.6) / 60) + ":" + (int)((((times.get(0) * 0.6)/60) - Math.floor((times.get(0) * 0.6) / 60))*100*0.6);
    minutes = Integer.toString((int)Math.floor((times.get(0) * 0.6) / 60));
    seconds = Integer.toString((int)((((times.get(0) * 0.6)/60) - Math.floor((times.get(0) * 0.6) / 60))*100*0.6));
    if (seconds.length() == 1) {
      seconds = "0" + seconds;
    }
    //System.out.println("Fastest kill in " + runs + " kills: \t" + times.get(0) * 0.6 + " seconds.");
    System.out.println("Fastest kill in " + runs + " kills: \t" + minutes + ":" + seconds);

    //time = (int)Math.floor((times.get(1000000) * 0.6) / 60) + ":" + (int)((((times.get(1000000) * 0.6)/60) - Math.floor((times.get(1000000) * 0.6) / 60))*100*0.6);
    minutes = Integer.toString((int)Math.floor((times.get(runs - 1) * 0.6) / 60));
    seconds = Integer.toString((int)((((times.get(runs - 1) * 0.6)/60) - Math.floor((times.get(runs - 1) * 0.6) / 60))*100*0.6));
    if (seconds.length() == 1) {
      seconds = "0" + seconds;
    }
    //System.out.println("Slowest kill in " + runs + " kills: \t" + times.get(1000000) * 0.6 + " seconds.");
    System.out.println("Slowest kill in " + runs + " kills: \t" + minutes + ":" + seconds);
    System.out.println();
    System.out.println("Total sub6: " + sub630 + ", % chance: " + sub630/runs*100 + ".");
    System.out.println("------");

    System.out.println("Harmonised Nightmare staff without thralls:");
    totalBfCharges = 0;
    totalScytheCharges = 0;
    int totalHarmCasts = 0;
    totalSpecsUsed = 0;
    times = new ArrayList<Double>();
    for (int i = 0; i < runs; i++) {
      NM = new PhosaniCalcs();
      NM.thralls = false;
      NM.harm = true;
      NM.deathCharge = false;
      NM.simulateKill();
      times.add(NM.killTime);
      totalBfCharges += NM.bfCharges;
      totalScytheCharges += NM.scytheCharges;
      totalHarmCasts += NM.mageCasts;
      totalSpecsUsed += NM.specsUsed;
    }
    totalTime = 0;
    sub630 = 0;
    for (double d: times) {
      totalTime += d;
      if (d < 600) {
        sub630 ++;
      }
    }
    average = totalTime / times.size();
    System.out.println("Average specs per kill: " + (double)totalSpecsUsed / (double)runs + ".");
    System.out.println("Average blood fury charges per kill: " + totalBfCharges / runs + ".");
    System.out.println("Average scythe charges per kill: " + totalScytheCharges / runs + ".");
    System.out.println("Average harmonised staff casts per kill: " + totalHarmCasts / runs + ".");
    //String time = (int)Math.floor((average * 0.6) / 60) + ":" + (int)((((average * 0.6)/60) - Math.floor((average * 0.6) / 60))*100*0.6);
    minutes = Integer.toString((int)Math.floor((average * 0.6) / 60));
    seconds = Integer.toString((int)((((average * 0.6)/60) - Math.floor((average * 0.6) / 60))*100*0.6));
    if (seconds.length() == 1) {
      seconds = "0" + seconds;
    }
    //System.out.println("Mean average: \t\t\t" + average * 0.6 + " seconds.");
    System.out.println("Mean average: \t\t\t" + minutes + ":" + seconds);
    Collections.sort(times);

    median = (times.get((int)Math.floor(times.size() / 2))
                    + times.get((int)Math.ceil(times.size() / 2))) / 2;
    //time = (int)Math.floor((median * 0.6) / 60) + ":" + (int)((((median * 0.6)/60) - Math.floor((median * 0.6) / 60))*100*0.6);
    minutes = Integer.toString((int)Math.floor((median * 0.6) / 60));
    seconds = Integer.toString((int)((((median * 0.6)/60) - Math.floor((median * 0.6) / 60))*100*0.6));
    if (seconds.length() == 1) {
      seconds = "0" + seconds;
    }
    //System.out.println("Median average: \t\t" + median * 0.6 + " seconds.");
    System.out.println("Median average: \t\t" + minutes + ":" + seconds);

    //time = (int)Math.floor((times.get(0) * 0.6) / 60) + ":" + (int)((((times.get(0) * 0.6)/60) - Math.floor((times.get(0) * 0.6) / 60))*100*0.6);
    minutes = Integer.toString((int)Math.floor((times.get(0) * 0.6) / 60));
    seconds = Integer.toString((int)((((times.get(0) * 0.6)/60) - Math.floor((times.get(0) * 0.6) / 60))*100*0.6));
    if (seconds.length() == 1) {
      seconds = "0" + seconds;
    }
    //System.out.println("Fastest kill in " + runs + " kills: \t" + times.get(0) * 0.6 + " seconds.");
    System.out.println("Fastest kill in " + runs + " kills: \t" + minutes + ":" + seconds);

    //time = (int)Math.floor((times.get(1000000) * 0.6) / 60) + ":" + (int)((((times.get(1000000) * 0.6)/60) - Math.floor((times.get(1000000) * 0.6) / 60))*100*0.6);
    minutes = Integer.toString((int)Math.floor((times.get(runs - 1) * 0.6) / 60));
    seconds = Integer.toString((int)((((times.get(runs - 1) * 0.6)/60) - Math.floor((times.get(runs - 1) * 0.6) / 60))*100*0.6));
    if (seconds.length() == 1) {
      seconds = "0" + seconds;
    }
    //System.out.println("Slowest kill in " + runs + " kills: \t" + times.get(1000000) * 0.6 + " seconds.");
    System.out.println("Slowest kill in " + runs + " kills: \t" + minutes + ":" + seconds);
    System.out.println();
    System.out.println("Total sub6: " + sub630 + ", % chance: " + (double)(sub630/runs*100) + ".");
    System.out.println("------");
  }
}
