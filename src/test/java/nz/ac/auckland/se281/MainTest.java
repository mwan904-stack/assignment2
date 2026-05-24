package nz.ac.auckland.se281;

import static nz.ac.auckland.se281.Main.Command.*;
import static nz.ac.auckland.se281.cli.MessageCli.*;

import java.util.Random;
import nz.ac.auckland.se281.cli.Utils;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
  MainTest.Task1.class,
  MainTest.Task2.class,
  MainTest.Task3.class,
  MainTest.Task4.class,
  MainTest.Task5.class
})
public class MainTest {

  private static final String AI_NAME = "PIG-9000";
  private static final String ROLL = "ROLL";
  private static final String HOLD = "HOLD";

  private static void resetSeeds(int seed) {
    Utils.randomDie = new Random(seed);
    Utils.randomAi = new Random(seed);
    Utils.randomPowerNumber = new Random(seed);
  }

  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class Task1 extends SysCliTest {

    public Task1() {
      super(Main.class);
    }

    @Before
    public void resetState() {
      resetSeeds(1);
    }

    @Test
    public void T1_01_welcome_message() throws Exception {
      runCommands(NEW_GAME + " EASY 50", "Valerio");
      assertContains(WELCOME_PLAYER.getMessage("Valerio", "50"));
    }

    @Test
    public void T1_02_play_start_round() throws Exception {
      runCommands(
          NEW_GAME + " EASY 50",
          "Valerio", //
          PLAY,
          ROLL,
          HOLD);
      assertContains(START_ROUND.getMessage("1", "50"));
      assertDoesNotContain(START_ROUND.getMessage("0", "50"));
      assertDoesNotContain(START_ROUND.getMessage("2", "50"));
    }

    @Test
    public void T1_03_play_second_round() throws Exception {
      runCommands(
          NEW_GAME + " EASY 50",
          "Valerio", //
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD);
      assertContains(START_ROUND.getMessage("1", "50"));
      assertContains(START_ROUND.getMessage("2", "50"));
      assertDoesNotContain(START_ROUND.getMessage("3", "50"));
    }

    @Test
    public void T1_04_target_in_start_round_message() throws Exception {
      runCommands(
          NEW_GAME + " EASY 25",
          "Valerio", //
          PLAY,
          ROLL,
          HOLD);
      assertContains(START_ROUND.getMessage("1", "25"));
    }

    @Test
    public void T1_05_asks_input_when_playing() throws Exception {
      runCommands(
          NEW_GAME + " EASY 50",
          "Valerio", //
          PLAY,
          ROLL,
          HOLD);
      assertContains(ASK_HUMAN_ACTION.getMessage());
    }

    @Test
    public void T1_06_invalid_input_then_valid() throws Exception {
      runCommands(
          NEW_GAME + " EASY 50",
          "Valerio", //
          PLAY,
          "blah",
          ROLL,
          HOLD);
      assertContains(INVALID_HUMAN_ACTION.getMessage());
      assertContains(PRINT_HOLD.getMessage("Valerio", "4"));
    }

    @Test
    public void T1_07_hold_before_rolling_re_prompts() throws Exception {
      runCommands(
          NEW_GAME + " EASY 50",
          "Valerio", //
          PLAY,
          HOLD,
          ROLL,
          HOLD);
      assertContains(MUST_ROLL_FIRST.getMessage());
      assertContains(PRINT_ROLL.getMessage("Valerio", "4", "4"));
      assertContains(PRINT_HOLD.getMessage("Valerio", "4"));
    }

    @Test
    public void T1_08_prints_turn_headers_for_both_players() throws Exception {
      runCommands(
          NEW_GAME + " EASY 50",
          "Valerio", //
          PLAY,
          ROLL,
          HOLD);
      assertContains(PRINT_TURN_HEADER.getMessage("Valerio"));
      assertContains(PRINT_TURN_HEADER.getMessage(AI_NAME));
    }

    @Test
    public void T1_09_prints_human_roll() throws Exception {
      runCommands(
          NEW_GAME + " EASY 50",
          "Valerio", //
          PLAY,
          ROLL,
          ROLL,
          HOLD);
      // seed 1 dice: 4, 5, ...
      assertContains(PRINT_ROLL.getMessage("Valerio", "4", "4"));
      assertContains(PRINT_ROLL.getMessage("Valerio", "5", "9"));
    }

    @Test
    public void T1_10_prints_human_hold() throws Exception {
      runCommands(
          NEW_GAME + " EASY 50",
          "Valerio", //
          PLAY,
          ROLL,
          HOLD);
      assertContains(PRINT_HOLD.getMessage("Valerio", "4"));
    }
  }

  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class Task2 extends SysCliTest {

    public Task2() {
      super(Main.class);
    }

    @Before
    public void resetState() {
      resetSeeds(1);
    }

    @Test
    public void T2_01_easy_ai_plays_random_first_roll() throws Exception {
      // seed 1 dice: 4 (human), then 5 (AI's 1st roll)
      runCommands(
          NEW_GAME + " EASY 50",
          "Valerio", //
          PLAY,
          ROLL,
          HOLD);
      assertContains(PRINT_ROLL.getMessage(AI_NAME, "5", "5"));
    }

    @Test
    public void T2_02_easy_ai_holds_random() throws Exception {
      // seed 1 ai bool: true, true, false → ROLL ROLL HOLD; AI banks 7 (5+2)
      runCommands(
          NEW_GAME + " EASY 50",
          "Valerio", //
          PLAY,
          ROLL,
          HOLD);
      assertContains(PRINT_ROLL.getMessage(AI_NAME, "5", "5"));
      assertContains(PRINT_ROLL.getMessage(AI_NAME, "2", "7"));
      assertContains(PRINT_HOLD.getMessage(AI_NAME, "7"));
    }

    @Test
    public void T2_03_human_scoring_basic() throws Exception {
      runCommands(
          NEW_GAME + " EASY 50",
          "Valerio", //
          PLAY,
          ROLL,
          HOLD);
      assertContains(PRINT_OUTCOME_ROUND.getMessage("Valerio", "4"));
    }

    @Test
    public void T2_04_ai_scoring_basic() throws Exception {
      runCommands(
          NEW_GAME + " EASY 50",
          "Valerio", //
          PLAY,
          ROLL,
          HOLD);
      assertContains(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, "7"));
    }

    @Test
    public void T2_05_bust_scores_zero() throws Exception {
      resetSeeds(2);
      runCommands(
          NEW_GAME + " EASY 50",
          "Valerio", //
          PLAY,
          ROLL,
          ROLL);
      assertContains(PRINT_BUST.getMessage("Valerio"));
      assertContains(PRINT_OUTCOME_ROUND.getMessage("Valerio", "0"));
    }

    @Test
    public void T2_06_power_bonus_applied_to_human() throws Exception {
      Utils.randomDie = new Random(19000);
      Utils.randomAi = new Random(1);
      Utils.randomPowerNumber = new Random(1);
      runCommands(
          NEW_GAME + " EASY 100",
          "Valerio",
          PLAY,
          ROLL,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          ROLL,
          HOLD);
      assertContainsAtRound(PRINT_POWER_NUMBER.getMessage("2"), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", "9"), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, "3"), 3);
    }

    @Test
    public void T2_07_no_power_bonus_when_no_match() throws Exception {
      // R3 human banks 5; AI banks 3. Power = 2. No match.
      runCommands(
          NEW_GAME + " EASY 50",
          "Valerio", //
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD);
      assertContainsAtRound(PRINT_POWER_NUMBER.getMessage("2"), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", "5"), 3);
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage(AI_NAME, "3"), 3);
    }

    @Test
    public void T2_08_busted_player_no_bonus() throws Exception {
      resetSeeds(2);
      runCommands(
          NEW_GAME + " EASY 50",
          "Valerio", //
          PLAY,
          ROLL,
          ROLL,
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD);
      assertContains(PRINT_BUST.getMessage("Valerio"));
      assertContainsAtRound(PRINT_OUTCOME_ROUND.getMessage("Valerio", "0"), 1);
    }
  }

  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class Task3 extends SysCliTest {

    public Task3() {
      super(Main.class);
    }

    @Before
    public void resetState() {
      resetSeeds(1);
    }

    @Test
    public void T3_01_medium_round_one_uses_random() throws Exception {
      runCommands(
          NEW_GAME + " MEDIUM 50",
          "Valerio", //
          PLAY,
          ROLL,
          HOLD);
      // seed 1: AI rolls 5 (ROLL), 2 (HOLD), banks 7
      assertContains(PRINT_HOLD.getMessage(AI_NAME, "7"));
    }

    @Test
    public void T3_02_medium_round_two_uses_cautious() throws Exception {
      // R2 with seed 1 dice: AI Cautious holds at 16.
      runCommands(
          NEW_GAME + " MEDIUM 50",
          "Valerio", //
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD);
      assertContainsAtRound(PRINT_HOLD.getMessage(AI_NAME, "7"), 1);
      assertContainsAtRound(PRINT_HOLD.getMessage(AI_NAME, "16"), 2);
    }

    @Test
    public void T3_03_medium_keeps_cautious_round_three() throws Exception {
      runCommands(
          NEW_GAME + " MEDIUM 50", "Valerio", PLAY, ROLL, HOLD, PLAY, ROLL, HOLD, PLAY, ROLL, HOLD);
      assertContainsAtRound(PRINT_HOLD.getMessage(AI_NAME, "7"), 1);
      assertContainsAtRound(PRINT_HOLD.getMessage(AI_NAME, "16"), 2);
      assertContainsAtRound(PRINT_HOLD.getMessage(AI_NAME, "15"), 3);
    }

    @Test
    public void T3_04_medium_round_two_busts_possible() throws Exception {
      Utils.randomDie = new Random(4);
      Utils.randomAi = new Random(4);
      Utils.randomPowerNumber = new Random(1);
      runCommands(
          NEW_GAME + " MEDIUM 50",
          "Valerio", //
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD);
      assertContainsAtRound(PRINT_HOLD.getMessage(AI_NAME, "24"), 1);
      assertContainsAtRound(PRINT_BUST.getMessage(AI_NAME), 2);
    }

    @Test
    public void T3_05_medium_round_one_random_holds_immediately() throws Exception {
      Utils.randomDie = new Random(19000);
      Utils.randomAi = new Random(19000);
      Utils.randomPowerNumber = new Random(1);
      runCommands(
          NEW_GAME + " MEDIUM 50",
          "Valerio", //
          PLAY,
          ROLL,
          HOLD);
      assertContains(PRINT_HOLD.getMessage(AI_NAME, "6"));
    }

    @Test
    public void T3_06_medium_round_one_other_name() throws Exception {
      runCommands(
          NEW_GAME + " MEDIUM 50",
          "Alice", //
          PLAY,
          ROLL,
          HOLD);
      assertContains(PRINT_HOLD.getMessage(AI_NAME, "7"));
    }
  }

  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class Task4 extends SysCliTest {

    public Task4() {
      super(Main.class);
    }

    @Before
    public void resetState() {
      resetSeeds(1);
    }

    @Test
    public void T4_01_hard_round_one_random() throws Exception {
      runCommands(
          NEW_GAME + " HARD 50",
          "Valerio", //
          PLAY,
          ROLL,
          HOLD);
      assertContains(PRINT_HOLD.getMessage(AI_NAME, "7"));
    }

    @Test
    public void T4_02_hard_round_two_still_random() throws Exception {
      runCommands(
          NEW_GAME + " HARD 50",
          "Valerio", //
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD);
      assertContainsAtRound(PRINT_HOLD.getMessage(AI_NAME, "7"), 1);
      assertContainsAtRound(PRINT_HOLD.getMessage(AI_NAME, "3"), 2);
    }

    @Test
    public void T4_03_hard_round_three_uses_cautious() throws Exception {
      runCommands(
          NEW_GAME + " HARD 50", "Valerio", PLAY, ROLL, HOLD, PLAY, ROLL, HOLD, PLAY, ROLL, HOLD);
      assertContainsAtRound(PRINT_HOLD.getMessage(AI_NAME, "18"), 3);
    }

    @Test
    public void T4_04_hard_keeps_cautious_after_scoring() throws Exception {
      runCommands(
          NEW_GAME + " HARD 50",
          "Valerio",
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD);
      assertContainsAtRound(PRINT_HOLD.getMessage(AI_NAME, "18"), 3);
      assertContainsAtRound(PRINT_BUST.getMessage(AI_NAME), 4);
    }

    @Test
    public void T4_05_hard_switches_after_zero() throws Exception {
      runCommands(
          NEW_GAME + " HARD 100",
          "Valerio",
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD);
      assertContainsAtRound(PRINT_BUST.getMessage(AI_NAME), 4);
      assertContainsAtRound(PRINT_BUST.getMessage(AI_NAME), 5);
    }

    @Test
    public void T4_06_hard_resets_on_new_game() throws Exception {
      runCommands(
          NEW_GAME + " HARD 50",
          "Valerio",
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD,
          NEW_GAME + " HARD 50",
          "Valerio",
          PLAY,
          ROLL,
          HOLD);
      assertContains(WELCOME_PLAYER.getMessage("Valerio", "50"));
    }
  }

  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class Task5 extends SysCliTest {

    public Task5() {
      super(Main.class);
    }

    @Before
    public void resetState() {
      resetSeeds(1);
    }

    @Test
    public void T5_01_play_before_new_game_errors() throws Exception {
      runCommands(PLAY);
      assertContains(GAME_NOT_STARTED.getMessage());
    }

    @Test
    public void T5_02_show_stats_before_new_game_errors() throws Exception {
      runCommands(SHOW_STATS);
      assertContains(GAME_NOT_STARTED.getMessage());
    }

    @Test
    public void T5_03_show_stats_during_game() throws Exception {
      runCommands(
          NEW_GAME + " EASY 50",
          "Valerio", //
          PLAY,
          ROLL,
          HOLD,
          SHOW_STATS);
      assertContains(PRINT_PLAYER_POINTS.getMessage("Valerio", "4"));
      assertContains(PRINT_PLAYER_POINTS.getMessage(AI_NAME, "7"));
    }

    @Test
    public void T5_04_show_stats_accumulates() throws Exception {
      runCommands(NEW_GAME + " EASY 50", "Valerio", PLAY, ROLL, HOLD, PLAY, ROLL, HOLD, SHOW_STATS);
      // R1: H=4, AI=7. R2: H=4, AI=3. Cum: H=8, AI=10.
      assertContains(PRINT_PLAYER_POINTS.getMessage("Valerio", "8"));
      assertContains(PRINT_PLAYER_POINTS.getMessage(AI_NAME, "10"));
    }

    @Test
    public void T5_05_ai_wins_when_target_reached() throws Exception {
      // target=10. R1: H=4 AI=7. R2: H=8 AI=10. AI reached 10. AI wins (10 > 8).
      runCommands(NEW_GAME + " EASY 10", "Valerio", PLAY, ROLL, HOLD, PLAY, ROLL, HOLD);
      assertContains(PRINT_END_GAME.getMessage());
      assertContains(PRINT_WINNER_GAME.getMessage(AI_NAME));
      assertDoesNotContain(PRINT_TIE_GAME.getMessage());
    }

    @Test
    public void T5_06_human_wins_when_target_reached() throws Exception {
      // target=20: After R5 H=23 AI=20. Both reached. H wins (23 > 20).
      runCommands(
          NEW_GAME + " EASY 20",
          "Valerio",
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD);
      assertContains(PRINT_END_GAME.getMessage());
      assertContains(PRINT_WINNER_GAME.getMessage("Valerio"));
    }

    @Test
    public void T5_07_tie_when_both_reach_with_equal_score() throws Exception {
      // target=15: After R4 both =18. Tie.
      runCommands(
          NEW_GAME + " EASY 15",
          "Valerio",
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD,
          PLAY,
          ROLL,
          HOLD);
      assertContains(PRINT_END_GAME.getMessage());
      assertContains(PRINT_TIE_GAME.getMessage());
    }
  }
}
