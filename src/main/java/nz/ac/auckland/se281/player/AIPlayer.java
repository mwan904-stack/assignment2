package nz.ac.auckland.se281.player;

import nz.ac.auckland.se281.strategy.Strategy;

public class AIPlayer extends Player {

  private Strategy currentStrategy;
  private int roundCount;

  public AIPlayer(String name, Strategy strategy) {
    super(name);

    currentStrategy = strategy;
  }

  public void setStrategy(Strategy strategy) {
    currentStrategy = strategy;
  }

  public boolean shouldRoll(int turnTotal, int humanScore) {

    boolean decision;

    decision = currentStrategy.shouldRoll(turnTotal, humanScore, getScore());

    return decision;
  }

  public void nextRound() {
    roundCount += 1;
  }

  public int getRoundsPlayed() {
    return roundCount;
  }
}
