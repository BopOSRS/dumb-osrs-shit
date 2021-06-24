import java.util.*;

class AvgCompletionCalculator {

  int wantedCompletions;
  int killCount = 0;
  ArrayList<Integer> drops = new ArrayList<Integer>();
  ArrayList<Integer> dropCompletions = new ArrayList<Integer>();
  Random rand = new Random(System.nanoTime());

  AvgCompletionCalculator(int wantedCompletions) {
    this.wantedCompletions = wantedCompletions;
  }

  public void addDrop(int droprate) {
    drops.add(droprate);
    dropCompletions.add(0);
  }

  public boolean isComplete() {
    for (int i: dropCompletions) {
      if (i < wantedCompletions) {
        return false;
      }
    }
    return true;
  }

  public void completeBoss() {
    while (!isComplete()) {
      killCount ++;
      for (int i = 0; i < drops.size(); i++) {
        int roll = rand.nextInt(drops.get(i) + 1);
        if (roll == drops.get(i)) {
          dropCompletions.set(i, dropCompletions.get(i) + 1);
          i = drops.size() + 1;
        }
      }
    }
  }

  public static void main(String[] args) {
    int completions;
    int runs = 100000;
    try {
      runs = Integer.parseInt(args[0]);
    } catch (Exception ignored) {
    }
    ArrayList<Integer> droprates = new ArrayList<Integer>();
    Scanner in = new Scanner(System.in);
    int input = -1;
    while (input != 0) {
      System.out.println("Input droprate of wanted item.");
      System.out.print("Stop by inputting '0': ");
      input = Integer.parseInt(in.nextLine());
      if (input != 0) {
        droprates.add(input);
      }
    }
    System.out.println();
    System.out.print("Input how many completions you want: ");
    completions = Integer.parseInt(in.nextLine());
    ArrayList<Integer> completionTime = new ArrayList<Integer>();
    AvgCompletionCalculator ac;
    for (int i = 0; i < runs; i++) {
      ac = new AvgCompletionCalculator(completions);
      for (int x: droprates) {
        ac.addDrop(x);
      }
      ac.completeBoss();
      completionTime.add(ac.killCount);
    }
    Collections.sort(completionTime);
    long totalKills = 0;
    for (int i = 0; i < runs; i++) {
      totalKills += completionTime.get(i);
    }
    double average = totalKills / runs;
    System.out.println("Average: " + average);
    System.out.println("Median: " + completionTime.get((int)(runs / 2)));
    System.out.println("Best: " + completionTime.get(0));
    System.out.println("Worst: " + completionTime.get(runs - 1));
  }
}
