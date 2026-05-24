package nz.ac.auckland.se281.strategy;

public class AdaptiveStrategy implements Strategy {

  @Override
  public boolean shouldRoll(int turnTotal, int humanScore, int aiScore) {

    int limit;

    if (aiScore < humanScore) {
      limit = 22;
    } else {
      limit = 12;
    }

    return turnTotal < limit;
  }
}
