package nz.ac.auckland.se281.strategy;

public interface Strategy {

  boolean shouldRoll(int turnTotal, int humanScore, int aiScore);
}
