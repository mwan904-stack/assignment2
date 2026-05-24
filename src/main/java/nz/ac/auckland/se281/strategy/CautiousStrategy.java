package nz.ac.auckland.se281.strategy;

public class CautiousStrategy implements Strategy {

  @Override
  public boolean shouldRoll(int turnTotal, int humanScore, int aiScore) {

    int holdPoint = 15;

    if (turnTotal >= holdPoint) {
      return false;
    }

    return true;
  }
}
