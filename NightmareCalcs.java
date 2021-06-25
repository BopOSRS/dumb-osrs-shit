import java.util.*;

public class NightmareCalcs {
  int weapon;
  int spec;
  boolean inq = true; // why would this be false lol..
  boolean thralls = true;
  boolean deathCharge = true;
  boolean harm = false;
  boolean dwh = true;
  int mageLvl = 99;
  int timeTillLevelDecay = 150; // 150 ticks.
  int timeTillHeartRegen = 700; // 700 ticks.
  int timeTillDeathChargeSpecRegen = 100; // 100 ticks between casts.
  int timeTillSpecRegen = 50; // 50 ticks between each spec regen.
  int deathChargeRegenTick;
  int heartRegenTick;
  int mageDecayTick;
  int specRegenTick;

  boolean deathChargeActive = false;

  // Extra times.
  int phaseTransitionTime = 28; // 28 ticks between last cast of spell and first attackable tick on boss.
  int spellTravelTime = 2; // 2 ticks from cast to hit on final totem.
  int deathAnimationTime = 14;

  int specBar = 100; //
  int specsUsed = 0;
  int scytheMax = 48;
  int DWHmax = 76;
  int clawMax = 42;
  int maceMax = 53;

  int scytheAcc = 94;
  int DWHacc = 159;
  int clawAcc = 81;

  int scytheSpeed = 5;
  int DWHspeed = 6;
  int clawSpeed = 4;
  int sangSpeed = 4;
  int delayThrall = 0;

  int defLvl = 150;
  int stabDef; // Ignored.
  int slashDef = 180;
  int crushDef = 40;

  public int hp = 2000;
  public int huskOneHp = 20; // 20
  public int huskTwoHp = 20; // 20
  public int parasiteHp = 40; // 40

  double MAR_stab, MAR_slash, MAR_crush;
  double MDR_stab, MDR_slash, MDR_crush;

  double accuracy;
  double killTime = 0.0;

  Random rand = new Random(System.nanoTime());

  public NightmareCalcs() {
    // Trenger sikkert ikke fikse noe her.
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

  public boolean regenDef() {
    if (defLvl < 150) {
      defLvl++;
      return true;
    }
    return false;
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
      if (hit > 0) {
        defLvl = 120;
        hp -= hit;
        regenDef();
      }
    }
  }

  public void clawSpec() {
    findMaxAttackRoll(2);
    findMaxDefRoll(1);
    double accuracy = calcAccuracy(1);
    double randomNumber;
    int totalHit = 0;
    for (int i = 0; i < 4; i++) {
      randomNumber = Math.random();
      if (accuracy > randomNumber) {
        if (i == 0) {
          int hit = randomHit(Math.floor(clawMax / 2), clawMax);
          totalHit = (int) (hit + Math.floor(hit / 2) + (2 * Math.floor(hit / 4)) + 1);
          hp -= totalHit;
          return;
        } else if (i == 1) {
          int hit = randomHit(Math.floor((3 * clawMax) / 8), Math.floor((7 * clawMax) / 8) + 1);
          totalHit = (int) (hit + 2 * Math.floor(hit / 2) + 1);
          hp -= totalHit;
          return;
        } else if (i == 2) {
          int hit = randomHit(Math.floor(clawMax / 4), Math.floor((3 * clawMax) / 4));
          totalHit = 2 * hit + 1;
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

  public void clawSpecParasite() {
    int mar = 21605;
    int mdr = 6566;
    double accuracy = (1 - ((mdr + 2) / (2 * (mar + 1))));
    int totalHit = 0;
    for (int i = 0; i < 4; i++) {
      if (accuracy > Math.random()) {
        if (i == 0) {
          int hit = randomHit(Math.floor(clawMax / 2), clawMax);
          totalHit = (int) (hit + Math.floor(hit / 2) + (2 * Math.floor(hit / 4)) + 1);
          parasiteHp -= totalHit;
          return;
        } else if (i == 1) {
          int hit = randomHit(Math.floor((3 * clawMax) / 8), Math.floor((7 * clawMax) / 8) + 1);
          totalHit = (int) (hit + 2 * Math.floor(hit / 2) + 1);
          parasiteHp -= totalHit;
          return;
        } else if (i == 2) {
          int hit = randomHit(Math.floor(clawMax / 4), Math.floor((3 * clawMax) / 4));
          totalHit = 2 * hit + 1;
          parasiteHp -= totalHit;
          return;
        } else if (i == 3) {
          int hit = randomHit(Math.floor(clawMax / 4), Math.floor((5 * clawMax) / 4)) + 1;
          parasiteHp -= hit;
          return;
        }
      }
    }
    if (Math.random() > 0.5) {
      parasiteHp -= 2;
      return;
    }
    return;
  }

  public int randomHit(double lower, double upper) {
    int hit = (int)(rand.nextInt((int)upper - (int)lower) + lower);
    //System.out.println("Random number between " + lower + " and " + upper + " is " + hit);
    return hit;
  }

  public void scytheSwing(double accuracy) {
    int hit = 0;
    if (accuracy > Math.random()) {
      hit += rand.nextInt((int)scytheMax + 1);
    }
    if (accuracy > Math.random()) {
      hit += rand.nextInt((int)Math.floor(scytheMax / 2) + 1);
    }
    if (accuracy > Math.random()) {
      hit += rand.nextInt((int)Math.floor(scytheMax / 4) + 1);
    }
    hp -= hit;
  }

  public void thrallHit(boolean mage) {
    if (thralls) {
      if ((huskOneHp > 0) || (huskTwoHp > 0) || (parasiteHp > 0)) {
        int hit = rand.nextInt(3 + 1);
        if (huskOneHp > 0) {
          huskOneHp -= hit;
        } else if (huskTwoHp > 0) {
          huskTwoHp -= hit;
        } else if (parasiteHp > 0) {
          parasiteHp -= hit;
        }
      } else {
        if (delayThrall > 0) {
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
  }

  public void sangHit() {
    int maxHit = 78;
    int hit = 0;
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

  public void maceHit(int phase) {
    int accuracy = 0;
    int mar;
    int mdr;
    int hit = 0;
    if (phase == 1) {
      mar = 38333;
      mdr = 2436;
      accuracy = (1 - ((mdr + 2) / (2 * (mar + 1))));
      if (accuracy > Math.random()) {
        hit = rand.nextInt(maceMax + 1);
      }
      if (huskOneHp > 0) {
        huskOneHp -= hit;
      } else if (huskTwoHp > 0) {
        huskTwoHp -= hit;
      }
    } else if (phase == 2) {
      mar = 38333;
      mdr = 3626;
      accuracy = (1 - ((mdr + 2) / (2 * (mar + 1))));
      if (accuracy > Math.random()) {
        hit = rand.nextInt(maceMax + 1);
      }
      parasiteHp -= hit;
    }
  }

  public void simulateKill() {
    int currentTick = 0;
    if (dwh) {
      while ((defLvl == 150) && (specBar > 0)) {
        DWHSpec();
        specsUsed ++;
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
        specsUsed ++;
        killTime += clawSpeed;
        specBar -= 50;
        specRegenTick = 46;
      }
    }
    boolean alive = true;
    // First phase special happens once every 14 + 2 * 9 + 12 * 6 ticks.
    // First special either after 6 * 6 ticks or after 6 + 14 + 1 * 9 + 6 * 6 ticks.
    int aliveSpecials;
    int nextSpecialTick;
    int timeBetweenSpecials = 104;
    if (Math.random() >= 0.5) {
      nextSpecialTick = (6 * 6) - (specsUsed * 4);
      if (dwh) {
        nextSpecialTick -= specsUsed * 2;
      }
    } else {
      nextSpecialTick = (6 * 6 + 14 + 1 * 9 + 6 * 6) - (specsUsed * 4);
      if (dwh) {
        nextSpecialTick += specsUsed * 2;
      }
    }
    if (alive) {
      currentTick = 0;
      findMaxAttackRoll(0);
      findMaxDefRoll(2);
      double accuracy = calcAccuracy(2);
      scytheSwing(accuracy);
      thrallHit(false);
      while (hp > 0) {
        currentTick++;
        if (currentTick == nextSpecialTick) {
          huskOneHp = 20;
          huskTwoHp = 20;
          nextSpecialTick += timeBetweenSpecials;
          killTime += 5 - (currentTick % 5);
        }
        if ((huskOneHp > 0) || (huskTwoHp > 0)) {
          if (currentTick % 4 == 0) {
            maceHit(1);
          } if (currentTick % 4 == 0) {
            thrallHit(false);
          }
          if ((huskOneHp <= 0) && (huskTwoHp <= 0)) {
            killTime -= (4 - currentTick % 5);
            if (currentTick >= deathChargeRegenTick) {
              deathChargeRegenTick = currentTick + timeTillDeathChargeSpecRegen;
              if (deathCharge) {
                specBar += 15;
              }
              if (specBar > 100) {
                specBar = 100;
              }
            }
          }
        } else {
          if ((currentTick % 5) == 0) {
            if (regenDef()) {
              findMaxDefRoll(2);
              accuracy = calcAccuracy(2);
              scytheSwing(accuracy);
            } else if ((specBar >= 50) && (hp > 700)) {
              if (dwh) {
                if (hp < 700) {
                  clawSpec();
                  killTime --;
                } else {
                  DWHSpec();
                  killTime ++;
                }
              } else if (!dwh) {
                clawSpec();
                killTime --;
              }
              specBar -= 50;
              specsUsed ++;
              //System.out.println("Specced at " + hp + " hp, spec remaining " + specBar);
            } else {
              scytheSwing(accuracy);
            }
          } // Scythe or DWH.
          if ((currentTick % 4) == 0) {
            thrallHit(false);
          }
        } // Thrall hits.
        if ((currentTick >= specRegenTick)) {
          specBar += 10;
          specRegenTick = currentTick + timeTillSpecRegen;
          if (specBar > 100) {
            specBar = 100;
          }
        } // Regen 10 special energy.
        if ((hp < 800) && (mageLvl == 99)) {
          heart();
          heartRegenTick = currentTick + timeTillHeartRegen;
          mageDecayTick = currentTick + timeTillLevelDecay;
        }
        if ((mageLvl > 99) && (currentTick >= mageDecayTick)) {
          mageLvl --;
          mageDecayTick = currentTick + timeTillLevelDecay;
        }
      }
    }
    //System.out.println("First phase dead after " + (currentTick + killTime) + " ticks");
    // when use imbued heart ?
    //
    //
    // Totem phase phase 1.
    //
    //
    int deadTotems = 0;
    hp = 300;
    sangHit();
    thrallHit(true);
    while (deadTotems < 4) {
      currentTick ++;
      if (currentTick == nextSpecialTick) {
        huskOneHp = 20;
        huskTwoHp = 20;
        nextSpecialTick += timeBetweenSpecials;
      }
      if ((huskOneHp > 0) || (huskTwoHp > 0)) {
        if (currentTick % 4 == 0) {
          maceHit(1);
        } if (currentTick % 4 == 0) {
          thrallHit(false);
        }
        if ((huskOneHp <= 0) && (huskTwoHp <= 0)) {
          if (currentTick >= deathChargeRegenTick) {
            deathChargeRegenTick = currentTick + timeTillDeathChargeSpecRegen;
            if (deathCharge) {
              specBar += 15;
            }
            if (specBar > 100) {
              specBar = 100;
            }
          }
        }
      } else {
        if (currentTick % 4 == 0) {
          sangHit();
        }
        if (currentTick >= mageDecayTick) {
          mageDecayTick = currentTick + timeTillLevelDecay;
          mageLvl --;
        }
        if (currentTick >= specRegenTick) {
          specBar += 10;
          specRegenTick = currentTick + timeTillSpecRegen;
          if (specBar > 100) {
              specBar = 100;
          }
        } if (currentTick % 4 == 0) {
          thrallHit(true);
        }
        if (hp <= 0) {
          hp = 300;
          deadTotems ++;
          delayThrall = 1;
        }
      }
    }

    //
    //
    // START PHASE TWO
    //
    //
    // reset hp to 2k.
    // Every x amount of ticks, spawn a parasite.
    // If 50+ spec, clawspec, else inq mace, make a new inqMace() method and one for parasite hp.
    // Remember to include thrall against parasite.
    // Second phase special happens once every 6 + 2 * 9 + 12 * 6 ticks.
    // First special either after 6 * 6 ticks or after 6 + 6 + 1 * 9 + 6 * 6 ticks.
    specRegenTick -= 30;
    if (currentTick >= specRegenTick) {
      specRegenTick = currentTick + (currentTick - specRegenTick);
      specBar += 10;
      if (specBar > 100) {
        specBar = 100;
      }
    }
    mageDecayTick -= 30;
    if (currentTick >= mageDecayTick) {
      if (mageLvl > 99) {
        mageLvl --;
      }
      mageDecayTick = currentTick + (currentTick - mageDecayTick);
    }
    heartRegenTick -= 30;
    aliveSpecials = 0;
    nextSpecialTick = 0;
    timeBetweenSpecials = 96;
    int timeTillBurst = 26;
    if (Math.random() >= 0.5) {
      nextSpecialTick = (6 * 6) + currentTick + timeTillBurst;
    } else {
      nextSpecialTick = (6 + 6 + 1 * 9 + 6 * 6) + currentTick + timeTillBurst;
    }
    hp = 2000;
    defLvl = 150;
    int ticksAlive = 0;
    if (alive) {
      findMaxAttackRoll(0);
      findMaxDefRoll(2);
      double accuracy = calcAccuracy(2);
      scytheSwing(accuracy);
      thrallHit(false);
      while (hp > 0) {
        currentTick++;
        if (currentTick == nextSpecialTick) {
          parasiteHp = 40;
          aliveSpecials ++;
          nextSpecialTick = currentTick + timeBetweenSpecials;
          ticksAlive = 0;
        }
        if (parasiteHp > 0) {
          ticksAlive ++;
          if (currentTick % 4 == 0) {
            if (specBar >= 50) {
              clawSpecParasite();
              specBar -= 50;
            } else {
              //System.out.println("Couldn't spec parasite, current spec " + specBar + " parasite number " + aliveSpecials);
              maceHit(2);
            }
            thrallHit(false);
            if (ticksAlive > 8) {
              hp += randomHit(31, 50);
            }
          }
          if (parasiteHp <= 0) {
            killTime -= (4 - currentTick % 5);
            if (currentTick >= deathChargeRegenTick) {
              deathChargeRegenTick = currentTick + timeTillDeathChargeSpecRegen;
              if (deathCharge) {
                specBar += 15;
              }
            }
          }
        } else {
          if (currentTick % 5 == 0) {
            scytheSwing(accuracy);
          } // Scythe hits.
          if ((currentTick % 4) == 0) {
            thrallHit(false);
          } // Thrall hits.
        }
        if (currentTick >= specRegenTick) {
          specBar += 10;
          specRegenTick = currentTick + timeTillSpecRegen;
          if (specBar > 100) {
            specBar = 100;
          }
        } // Regen 10 special energy.
        if ((currentTick == mageDecayTick) && (mageLvl > 99)) {
          mageLvl --;
          mageDecayTick += timeTillLevelDecay;
        }
        if (currentTick == heartRegenTick) {
          // timeTillHeartRegen = 0;
          // idk what i meant to do here but it wasn't this
        }
      }
    }
    //System.out.println("Second phase dead after " + (currentTick + killTime) + " ticks.");
    //
    //
    // Second phase mage phase.
    //
    //
    deadTotems = 0;
    hp = 300;
    sangHit();
    thrallHit(true);
    if (currentTick >= heartRegenTick) {
      heart();
      heartRegenTick = currentTick + timeTillHeartRegen;
    }
    while (deadTotems < 4) {
      currentTick ++;
      if (currentTick == nextSpecialTick) {
        parasiteHp = 40;
        nextSpecialTick += timeBetweenSpecials;
        ticksAlive = 0;
      }
      if (parasiteHp > 0) {
        if (currentTick % 4 == 0) {
          if (specBar >= 50) {
            clawSpecParasite();
            specBar -= 50;
          } else {
            //System.out.println("Couldn't spec parasite mage phase, current spec " + specBar);
            maceHit(2);
          }
          thrallHit(false);
          if (ticksAlive >= 0) {
            hp += randomHit(31, 50);
            if (hp > 300) {
              hp = 300;
            }
          }
        }
        if (parasiteHp <= 0) {
          killTime -= (4 - currentTick % 4);
          if (currentTick >= deathChargeRegenTick) {
            deathChargeRegenTick = currentTick + timeTillDeathChargeSpecRegen;
            if (deathCharge) {
              specBar += 15;
            }
          }
        } else {
          ticksAlive++;
        }
      }
      if (currentTick % 4 == 0) {
        sangHit();
      }
      if (currentTick >= mageDecayTick) {
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
      }
      if (hp <= 0) {
        hp = 300;
        deadTotems ++;
        delayThrall = 1;
      }
    }
    //
    //
    // Final phase __very__ simple. Count zero wasted ticks.
    // Ignore specials.
    //
    //
    specRegenTick -= 30;
    if (currentTick > specRegenTick) {
      specRegenTick = currentTick + (currentTick - specRegenTick);
      specBar += 10;
      if (specBar > 100) {
        specBar = 100;
      }
    }
    mageDecayTick -= 30;
    if (currentTick > mageDecayTick) {
      if (mageLvl > 99) {
        mageLvl --;
      }
      mageDecayTick = currentTick + (currentTick - mageDecayTick);
    }
    heartRegenTick -= 30;
    hp = 2000;
    defLvl = 150;
    if ((dwh) && (specBar >= 50)) {
      DWHSpec();
      specBar -= 50;
      killTime ++;
    } else if ((!dwh) && (specBar >= 50)) {
      clawSpec();
      specBar -= 50;
      killTime --;
    } else if (specBar < 50) {
      findMaxAttackRoll(0);
      findMaxDefRoll(2);
      accuracy = calcAccuracy(2);
      scytheSwing(accuracy);
    }
    thrallHit(false);
    while (hp > 0) {
      currentTick++;
      if (currentTick % 5 == 0) {
        if (regenDef()) {
          findMaxAttackRoll(0);
          findMaxDefRoll(2);
          accuracy = calcAccuracy(2);
          scytheSwing(accuracy);
        } else if (specBar >= 50) {
          if ((hp > 800) && (dwh)) {
            DWHSpec();
            killTime ++;
            specBar -= 50;
          } else {
            clawSpec();
            killTime --;
            specBar -= 50;
          }
        } else {
          findMaxAttackRoll(0);
          findMaxDefRoll(2);
          accuracy = calcAccuracy(2);
          scytheSwing(accuracy);
        }
      } // Scythe or spec.
      if ((currentTick % 4) == 0) {
        thrallHit(false);
      } // Thrall hits.
      if ((currentTick >= specRegenTick)) {
        specBar += 10;
        specRegenTick = currentTick + timeTillSpecRegen;
        if (specBar > 100) {
          specBar = 100;
        }
      } // Regen 10 special energy.
      if ((currentTick == mageDecayTick) && (mageLvl > 99)) {
        mageLvl --;
        mageDecayTick += timeTillLevelDecay;
      }
    }
    //System.out.println("Third phase dead after " + (currentTick + killTime) + " ticks");
    //
    //
    // Final mage phase.
    //
    //
    hp = 300;
    deadTotems = 0;
    if (currentTick >= heartRegenTick) {
      heart();
      heartRegenTick = currentTick + timeTillHeartRegen;
    }
    sangHit();
    thrallHit(true);
    while (deadTotems < 4) {
      currentTick ++;
      if (currentTick >= heartRegenTick) {
        heart();
        heartRegenTick = currentTick + timeTillHeartRegen;
      }
      if (currentTick % 4 == 0) {
        sangHit();
      }
      if ((currentTick == mageDecayTick) && (mageLvl > 99)) {
        mageLvl --;
        mageDecayTick += timeTillLevelDecay;
      } if (currentTick == specRegenTick) {
        specBar += 10;
        specRegenTick = currentTick + timeTillSpecRegen;
      } if (currentTick % 4 == 0) {
        thrallHit(true);
      }
      if (hp <= 0) {
        hp = 300;
        deadTotems ++;
        delayThrall = 1;
      }
    }
    killTime += currentTick;
    killTime += 2 * phaseTransitionTime; // Including sleepwalker time.
    killTime += deathAnimationTime;
    killTime += 3 * spellTravelTime; // Assuming 2 ticks from cast xp drop to dead totem, per phase.
    //System.out.println("Kill done after " + killTime + " ticks.");
  }
  public static void main(String[] args) {
    System.out.println("Sanguinesti staff with thralls:");
    NightmareCalcs NM = new NightmareCalcs();
    ArrayList<Double> times = new ArrayList<Double>();
    int runs = 1000001;
    for (int i = 0; i < runs; i++) {
      NM = new NightmareCalcs();
      NM.simulateKill();
      times.add(NM.killTime);
    }
    double totalTime = 0;
    for (double d: times) {
      totalTime += d;
    }
    double average = totalTime / times.size();
    //String time = (int)Math.floor((average * 0.6) / 60) + ":" + (int)((((average * 0.6)/60) - Math.floor((average * 0.6) / 60))*100*0.6);
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
    System.out.println("------");

    System.out.println("Harmonised Nightmare staff without thralls:");
    times = new ArrayList<Double>();
    for (int i = 0; i < runs; i++) {
      NM = new NightmareCalcs();
      NM.thralls = false;
      NM.harm = true;
      NM.deathCharge = false;
      NM.simulateKill();
      times.add(NM.killTime);
    }
    totalTime = 0;
    for (double d: times) {
      totalTime += d;
    }
    average = totalTime / times.size();
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
    System.out.println("------");
  }
}
