package game_objects;

import org.junit.Test;

public class CircleTest {

  @Test
  public void testConstruction() {
    Circle newcirc = new Circle();
    assert(true);
  }

  @Test
  public void testSettersGetters() {
    Circle newcirc = new Circle();
    newcirc.setID("hello");
    assert(newcirc.getID().equals("hello"));
    newcirc.setRadius(5);
    assert(newcirc.getRadius() == 5);
    newcirc.setX(23);
    newcirc.setY(54);
    assert(newcirc.getX() == 23);
    assert(newcirc.getY() == 54);
  }

  @Test
  public void collission() {
    Circle circ1 = new Circle();
    circ1.setX(0);
    circ1.setY(0);
    Circle circ2 = new Circle();
    circ2.setX(0);
    circ2.setY(0);
    assert(circ1.isCollision(circ2));
    assert(circ1.distTo(circ2) == 0);
    assert(circ1.getDistanceFromOrigin() == 0);
  }

  @Test
  public void radiusCollission() {
    Circle circ1 = new Circle();
    circ1.setX(0);
    circ1.setY(0);
    Circle circ2 = new Circle();
    circ2.setX(10);
    circ2.setY(10);
    circ1.setRadius(100);
    assert(circ1.isCollision(circ2));
  }


}
