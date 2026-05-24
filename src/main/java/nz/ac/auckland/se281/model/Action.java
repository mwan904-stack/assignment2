package nz.ac.auckland.se281.model;

/** You cannot modify this class! */
public enum Action {
  ROLL,
  HOLD;

  public static Action fromInput(String input) {
    if (input == null) {
      return null;
    }
    // do this to be case insensitive
    input = input.trim().toUpperCase();

    switch (input) {
      case "R":
      case "ROLL":
        return ROLL;
      case "H":
      case "HOLD":
        return HOLD;
      default:
        return null; // invalid input
    }
  }
}
