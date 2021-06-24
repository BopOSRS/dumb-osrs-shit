import java.util.*;

class RaidsCompletion {
  // Weighting 20 (total 40)
  int dex; int arc;

  // Weighting 4 (total 8)
  int buckler; int dhcb;

  // Weighting 3 (total 15)
  int claws; int ancHat; int ancTop; int ancLegs; int bulwark;

  // Weighting 2 (total 6)
  int kodai; int maul; int tbow;

  int totalWeight = 69;
  double pointRate = 867600;
  double pointsPerRaid = 32000;
  int c; // wanted completions, i am lazy and made it just c.
  int totalRaids = 0;

  Random rand = new Random(System.nanoTime());

  public RaidsCompletion(int completions) {
    c = completions;
  }

  public boolean isComplete() {
    if ((dex >= c) && (arc >= c) && (buckler >= c) && (dhcb >= c) &&
        (claws >= c) && (ancHat >= c) && (ancTop >= c) && (ancLegs >= c) && (bulwark >= c) &&
        (kodai >= c) && (maul >= c) && (tbow >= c)) {
      return true;
    }
    return false;
  }

  public void completeRaids() {
    while (!isComplete()) {
      totalRaids ++;
      if (Math.random() <= (pointsPerRaid / pointRate)) {
        int roll = rand.nextInt(totalWeight + 1);
        if (roll <= 20) {
          dex ++;
        } else if (roll <= 40) {
          arc ++;
        } else if (roll <= 44) {
          buckler ++;
        } else if (roll <= 48) {
          dhcb ++;
        } else if (roll <= 51) {
          claws ++;
        } else if (roll <= 54) {
          bulwark ++;
        } else if (roll <= 57) {
          ancHat ++;
        } else if (roll <= 60) {
          ancTop ++;
        } else if (roll <= 63) {
          ancLegs ++;
        } else if (roll <= 65) {
          kodai ++;
        } else if (roll <= 67) {
          maul ++;
        } else if (roll <= 69) {
          tbow ++;
        }
      }
    }
  }

  public static void main(String[] args) {
    int completions = 0;
    int runs = 0;
    try {
      completions = Integer.parseInt(args[0]);
      runs = Integer.parseInt(args[1]);
      if ((completions <= 0) || (runs <= 0)) {
        throw new Exception();
      }
    } catch (Exception e) {
      System.out.println("nonon use 'java RaidsCompletion <c> <n>' where <c> is " +
                          "the number of completions you want, and <n> is the total" +
                          " number of simulations !!!");
      return;
    }
    ArrayList<Integer> completionTime = new ArrayList<Integer>();
    for (int i = 0; i < runs; i++) {
      RaidsCompletion rc = new RaidsCompletion(completions);
      rc.completeRaids();
      completionTime.add(rc.totalRaids);
    }
    Collections.sort(completionTime);
    long totalRaidCompletions = 0;
    for (int i = 0; i < runs; i++) {
      totalRaidCompletions += completionTime.get(i);
    }
    double average = totalRaidCompletions / runs;
    System.out.println("Average: " + average);
    System.out.println("Median: " + completionTime.get((int)(runs / 2)));
    System.out.println("Best: " + completionTime.get(0));
    System.out.println("Worst: " + completionTime.get(runs - 1));
  }
}
