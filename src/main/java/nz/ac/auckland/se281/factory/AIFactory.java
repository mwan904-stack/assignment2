package nz.ac.auckland.se281.factory;

import nz.ac.auckland.se281.Main.Difficulty;
import nz.ac.auckland.se281.player.AIPlayer;
import nz.ac.auckland.se281.strategy.RandomStrategy;
import nz.ac.auckland.se281.strategy.Strategy;

public class AIFactory {

  public static AIPlayer createAI(Difficulty difficulty) {

    Strategy strategy;

    switch (difficulty) {
      case EASY:
        strategy = new RandomStrategy();
        break;

      case MEDIUM:
        strategy = new RandomStrategy();
        break;

      default:
        strategy = new RandomStrategy();
        break;
    }

    AIPlayer ai = new AIPlayer("PIG-9000", strategy);

    return ai;
  }
}
