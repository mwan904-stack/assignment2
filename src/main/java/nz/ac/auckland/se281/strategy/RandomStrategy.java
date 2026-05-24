package nz.ac.auckland.se281.strategy;

import nz.ac.auckland.se281.cli.Utils;

public class RandomStrategy implements Strategy {

  @Override
  public boolean shouldRoll(int turnTotal, int humanScore, int aiScore) {

    boolean keepRolling;

    keepRolling = Utils.randomAi.nextBoolean();

    return keepRolling;
  }
}
