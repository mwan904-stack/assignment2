package nz.ac.auckland.se281.engine;

import nz.ac.auckland.se281.Main.Difficulty;
import nz.ac.auckland.se281.cli.MessageCli;
import nz.ac.auckland.se281.cli.Utils;
import nz.ac.auckland.se281.factory.AIFactory;
import nz.ac.auckland.se281.model.Action;
import nz.ac.auckland.se281.model.Die;
import nz.ac.auckland.se281.player.AIPlayer;
import nz.ac.auckland.se281.strategy.AdaptiveStrategy;
import nz.ac.auckland.se281.strategy.CautiousStrategy;
import nz.ac.auckland.se281.strategy.RandomStrategy;

/** you can add fields, add methods, just don't remove existing methods */
public class Game {

  private static final String AI_NAME = "PIG-9000";
  private static final int MAX_ROUNDS = 30;
  private static final int POWER_BONUS = 3;

  private String humanName;
  private int targetScore;
  private int round;
  private boolean gameStarted;
  private AIPlayer aiPlayer;
  private Difficulty difficulty;
  private int humanScore;
  private int aiLastRoundPoints;
  private int powerNumber;
  private boolean hardUsingCautious;

  public void newGame(Difficulty difficulty, int targetScore, String[] options) {
    this.difficulty = difficulty;
    this.targetScore = targetScore;
    this.humanName = options[0];

    round = 1;
    gameStarted = true;
    humanScore = 0;
    aiLastRoundPoints = 0;
    powerNumber = 0;
    hardUsingCautious = false;

    aiPlayer = AIFactory.createAI(difficulty);

    MessageCli.WELCOME_PLAYER.printMessage(humanName, targetScore);
  }

  public void play() {
    if (!gameStarted) {
      MessageCli.GAME_NOT_STARTED.printMessage();
      return;
    }

    MessageCli.START_ROUND.printMessage(round, targetScore);

    boolean powerRound = isPowerRound();
    if (powerRound) {
      powerNumber = Die.rollPowerNumber();
    }

    RoundScore humanRound = playHumanTurn(powerRound);

    changeAiStrategy();

    RoundScore aiRound = playAiTurn(powerRound);

    if (powerRound) {
      MessageCli.PRINT_POWER_NUMBER.printMessage(powerNumber);
    }

    humanScore += humanRound.points;
    aiPlayer.addScore(aiRound.points);
    aiLastRoundPoints = aiRound.points;

    MessageCli.PRINT_OUTCOME_ROUND.printMessage(humanName, humanRound.points);
    MessageCli.PRINT_OUTCOME_ROUND.printMessage(AI_NAME, aiRound.points);

    if (humanScore >= targetScore || aiPlayer.getScore() >= targetScore) {
      endGame();
      return;
    }

    if (round == MAX_ROUNDS) {
      MessageCli.PRINT_ROUND_CAP_REACHED.printMessage(MAX_ROUNDS);
      endGame();
      return;
    }

    round++;
  }

  public void showStats() {
    if (!gameStarted) {
      MessageCli.GAME_NOT_STARTED.printMessage();
      return;
    }

    printStats();
  }

  private RoundScore playHumanTurn(boolean powerRound) {
    MessageCli.PRINT_TURN_HEADER.printMessage(humanName);

    int turnTotal = 0;
    boolean rolledBefore = false;
    boolean powerTriggered = false;

    while (true) {
      MessageCli.ASK_HUMAN_ACTION.printMessage();

      String input = Utils.scanner.nextLine();
      Action action = Action.fromInput(input);

      if (action == null) {
        MessageCli.INVALID_HUMAN_ACTION.printMessage();
        continue;
      }

      if (action == Action.HOLD) {
        if (!rolledBefore) {
          MessageCli.MUST_ROLL_FIRST.printMessage();
          continue;
        }

        MessageCli.PRINT_HOLD.printMessage(humanName, turnTotal);
        return new RoundScore(calculateRoundPoints(turnTotal, powerRound, powerTriggered));
      }

      int roll = Die.roll();
      rolledBefore = true;

      if (roll == 1) {
        MessageCli.PRINT_BUST.printMessage(humanName);
        return new RoundScore(0);
      }

      if (powerRound && roll == powerNumber) {
        powerTriggered = true;
      }

      turnTotal += roll;
      MessageCli.PRINT_ROLL.printMessage(humanName, roll, turnTotal);
    }
  }

  private RoundScore playAiTurn(boolean powerRound) {
    aiPlayer.nextRound();

    MessageCli.PRINT_TURN_HEADER.printMessage(AI_NAME);

    int turnTotal = 0;
    boolean powerTriggered = false;

    do {
      int roll = Die.roll();

      if (roll == 1) {
        MessageCli.PRINT_BUST.printMessage(AI_NAME);
        return new RoundScore(0);
      }

      if (powerRound && roll == powerNumber) {
        powerTriggered = true;
      }

      turnTotal += roll;
      MessageCli.PRINT_ROLL.printMessage(AI_NAME, roll, turnTotal);

    } while (aiPlayer.shouldRoll(turnTotal, humanScore));

    MessageCli.PRINT_HOLD.printMessage(AI_NAME, turnTotal);

    return new RoundScore(calculateRoundPoints(turnTotal, powerRound, powerTriggered));
  }

  private void changeAiStrategy() {
    if (difficulty == Difficulty.EASY) {
      aiPlayer.setStrategy(new RandomStrategy());
      return;
    }

    if (difficulty == Difficulty.MEDIUM) {
      changeMediumStrategy();
      return;
    }

    changeHardStrategy();
  }

  private void changeMediumStrategy() {
    if (round == 1) {
      aiPlayer.setStrategy(new RandomStrategy());
    } else {
      aiPlayer.setStrategy(new CautiousStrategy());
    }
  }

  private void changeHardStrategy() {
    if (round <= 2) {
      aiPlayer.setStrategy(new RandomStrategy());
      return;
    }

    if (round == 3) {
      aiPlayer.setStrategy(new CautiousStrategy());
      hardUsingCautious = true;
      return;
    }

    if (aiLastRoundPoints == 0) {
      hardUsingCautious = !hardUsingCautious;
    }

    if (hardUsingCautious) {
      aiPlayer.setStrategy(new CautiousStrategy());
    } else {
      aiPlayer.setStrategy(new AdaptiveStrategy());
    }
  }

  private boolean isPowerRound() {
    return round % 3 == 0;
  }

  private int calculateRoundPoints(int basePoints, boolean powerRound, boolean powerTriggered) {
    if (powerRound && powerTriggered) {
      return basePoints + POWER_BONUS;
    }

    return basePoints;
  }

  private void printStats() {
    MessageCli.PRINT_PLAYER_POINTS.printMessage(humanName, humanScore);
    MessageCli.PRINT_PLAYER_POINTS.printMessage(AI_NAME, aiPlayer.getScore());
  }

  private void endGame() {
    printStats();
    MessageCli.PRINT_END_GAME.printMessage();

    if (humanScore > aiPlayer.getScore()) {
      MessageCli.PRINT_WINNER_GAME.printMessage(humanName);
    } else if (humanScore < aiPlayer.getScore()) {
      MessageCli.PRINT_WINNER_GAME.printMessage(AI_NAME);
    } else {
      MessageCli.PRINT_TIE_GAME.printMessage();
    }

    gameStarted = false;
  }

  private class RoundScore {
    private int points;

    private RoundScore(int points) {
      this.points = points;
    }
  }
}
