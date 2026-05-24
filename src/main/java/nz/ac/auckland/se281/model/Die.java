package nz.ac.auckland.se281.model;

import nz.ac.auckland.se281.cli.Utils;

/** You cannot modify this class! */
public class Die {

  private static final int SIDES = 6;

  public static int roll() {
    return Utils.randomDie.nextInt(SIDES) + 1;
  }

  public static int rollPowerNumber() {
    // Power Number is in [2, 6] (1 is the bust value, never the power).
    return Utils.randomPowerNumber.nextInt(SIDES - 1) + 2;
  }
}
