package nz.ac.auckland.se281.player;

public abstract class Player {

  protected String name;
  protected int score;

  public Player(String name) {

    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public int getScore() {

    return this.score;
  }

  public void addScore(int points) {

    this.score = this.score + points;
  }
}
