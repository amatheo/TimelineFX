package io.github.amatheo.timelinefx.transform;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for hierarchical transform composition.
 * Verifies that Transform.compose() correctly implements parent-child relationships
 * following standard 3D engine patterns (Unity/Unreal).
 */
class TransformHierarchyTest {

  private static final double EPSILON = 0.001;

  @Test
  void composePositionWithoutRotation() {
    // Parent at (10, 5, 3)
    Transform parent = new Transform(
        new Vector3d(10, 5, 3),
        new Quaterniond().identity(),
        new Vector3d(1, 1, 1)
    );

    // Child at (5, 2, 1) in local space
    Transform child = new Transform(
        new Vector3d(5, 2, 1),
        new Quaterniond().identity(),
        new Vector3d(1, 1, 1)
    );

    Transform world = Transform.compose(parent, child);

    // Expected: parent position + child position = (15, 7, 4)
    assertEquals(15.0, world.position().x, EPSILON, "World X position");
    assertEquals(7.0, world.position().y, EPSILON, "World Y position");
    assertEquals(4.0, world.position().z, EPSILON, "World Z position");
  }

  @Test
  void composePositionWithParentRotation() {
    // Parent at (10, 0, 0) with 90° rotation around Y axis
    Transform parent = new Transform(
        new Vector3d(10, 0, 0),
        new Quaterniond().rotateY(Math.PI / 2), // 90° around Y
        new Vector3d(1, 1, 1)
    );

    // Child at (5, 0, 0) in local space (points along +X in local)
    Transform child = new Transform(
        new Vector3d(5, 0, 0),
        new Quaterniond().identity(),
        new Vector3d(1, 1, 1)
    );

    Transform world = Transform.compose(parent, child);

    // With 90° Y rotation, local +X becomes world -Z
    // Expected: (10, 0, 0) + rotated(5, 0, 0) = (10, 0, 0) + (0, 0, -5) = (10, 0, -5)
    assertEquals(10.0, world.position().x, EPSILON, "World X position");
    assertEquals(0.0, world.position().y, EPSILON, "World Y position");
    assertEquals(-5.0, world.position().z, EPSILON, "World Z position");
  }

  @Test
  void composeRotation() {
    // Parent with 45° rotation around Y
    Transform parent = new Transform(
        new Vector3d(0, 0, 0),
        new Quaterniond().rotateY(Math.PI / 4),
        new Vector3d(1, 1, 1)
    );

    // Child with 45° rotation around X
    Transform child = new Transform(
        new Vector3d(0, 0, 0),
        new Quaterniond().rotateX(Math.PI / 4),
        new Vector3d(1, 1, 1)
    );

    Transform world = Transform.compose(parent, child);

    // Expected: parent rotation * child rotation
    Quaterniond expected = new Quaterniond(parent.rotation());
    expected.mul(child.rotation());

    double diffAngle = world.rotation().difference(expected).angle();
    assertTrue(Math.abs(diffAngle) < EPSILON,
        "World rotation should be composition of parent and child rotations, diff=" + diffAngle);
  }

  @Test
  void composeScale() {
    // Parent with scale (2, 3, 4)
    Transform parent = new Transform(
        new Vector3d(0, 0, 0),
        new Quaterniond().identity(),
        new Vector3d(2, 3, 4)
    );

    // Child with scale (0.5, 2, 1.5)
    Transform child = new Transform(
        new Vector3d(0, 0, 0),
        new Quaterniond().identity(),
        new Vector3d(0.5, 2, 1.5)
    );

    Transform world = Transform.compose(parent, child);

    // Expected: component-wise multiplication (2*0.5, 3*2, 4*1.5) = (1, 6, 6)
    assertEquals(1.0, world.scale().x, EPSILON, "World X scale");
    assertEquals(6.0, world.scale().y, EPSILON, "World Y scale");
    assertEquals(6.0, world.scale().z, EPSILON, "World Z scale");
  }

  @Test
  void composePositionWithParentScale() {
    // Parent with scale (2, 2, 2)
    Transform parent = new Transform(
        new Vector3d(10, 0, 0),
        new Quaterniond().identity(),
        new Vector3d(2, 2, 2)
    );

    // Child at (5, 0, 0) in local space
    Transform child = new Transform(
        new Vector3d(5, 0, 0),
        new Quaterniond().identity(),
        new Vector3d(1, 1, 1)
    );

    Transform world = Transform.compose(parent, child);

    // Expected: parent position + (parent scale * child position)
    // = (10, 0, 0) + (2*5, 2*0, 2*0) = (10, 0, 0) + (10, 0, 0) = (20, 0, 0)
    assertEquals(20.0, world.position().x, EPSILON, "World X position with parent scale");
    assertEquals(0.0, world.position().y, EPSILON, "World Y position with parent scale");
    assertEquals(0.0, world.position().z, EPSILON, "World Z position with parent scale");
  }

  @Test
  void composeComplexHierarchy() {
    // Parent: position (10, 5, 0), rotation 90° around Y, scale (2, 1, 2)
    Transform parent = new Transform(
        new Vector3d(10, 5, 0),
        new Quaterniond().rotateY(Math.PI / 2),
        new Vector3d(2, 1, 2)
    );

    // Child: position (3, 2, 0), rotation 90° around X, scale (0.5, 2, 1)
    Transform child = new Transform(
        new Vector3d(3, 2, 0),
        new Quaterniond().rotateX(Math.PI / 2),
        new Vector3d(0.5, 2, 1)
    );

    Transform world = Transform.compose(parent, child);

    // Scale: (2*0.5, 1*2, 2*1) = (1, 2, 2)
    assertEquals(1.0, world.scale().x, EPSILON, "Complex world X scale");
    assertEquals(2.0, world.scale().y, EPSILON, "Complex world Y scale");
    assertEquals(2.0, world.scale().z, EPSILON, "Complex world Z scale");

    // Rotation: should be composed
    Quaterniond expectedRot = new Quaterniond(parent.rotation());
    expectedRot.mul(child.rotation());
    double diffAngle = world.rotation().difference(expectedRot).angle();
    assertTrue(Math.abs(diffAngle) < EPSILON, "Complex world rotation composition");

    // Position: 
    // 1. Scale child position by parent scale: (3*2, 2*1, 0*2) = (6, 2, 0)
    // 2. Rotate by parent rotation (90° Y): (6, 2, 0) -> (0, 2, -6)
    // 3. Add parent position: (10, 5, 0) + (0, 2, -6) = (10, 7, -6)
    assertEquals(10.0, world.position().x, EPSILON, "Complex world X position");
    assertEquals(7.0, world.position().y, EPSILON, "Complex world Y position");
    assertEquals(-6.0, world.position().z, EPSILON, "Complex world Z position");
  }

  @Test
  void identityParentPreservesChild() {
    // Identity parent should not affect child
    Transform parent = Transform.identity();
    Transform child = new Transform(
        new Vector3d(5, 3, 2),
        new Quaterniond().rotateY(Math.PI / 4),
        new Vector3d(2, 1.5, 3)
    );

    Transform world = Transform.compose(parent, child);

    // Should be identical to child
    assertEquals(child.position().x, world.position().x, EPSILON);
    assertEquals(child.position().y, world.position().y, EPSILON);
    assertEquals(child.position().z, world.position().z, EPSILON);
    assertEquals(child.scale().x, world.scale().x, EPSILON);
    assertEquals(child.scale().y, world.scale().y, EPSILON);
    assertEquals(child.scale().z, world.scale().z, EPSILON);
    
    double diffAngle = world.rotation().difference(child.rotation()).angle();
    assertTrue(Math.abs(diffAngle) < EPSILON, "Identity parent preserves child rotation");
  }

  @Test
  void identityChildPreservesParent() {
    // Identity child should result in parent transform
    Transform parent = new Transform(
        new Vector3d(5, 3, 2),
        new Quaterniond().rotateY(Math.PI / 4),
        new Vector3d(2, 1.5, 3)
    );
    Transform child = Transform.identity();

    Transform world = Transform.compose(parent, child);

    // Should be identical to parent
    assertEquals(parent.position().x, world.position().x, EPSILON);
    assertEquals(parent.position().y, world.position().y, EPSILON);
    assertEquals(parent.position().z, world.position().z, EPSILON);
    assertEquals(parent.scale().x, world.scale().x, EPSILON);
    assertEquals(parent.scale().y, world.scale().y, EPSILON);
    assertEquals(parent.scale().z, world.scale().z, EPSILON);
    
    double diffAngle = world.rotation().difference(parent.rotation()).angle();
    assertTrue(Math.abs(diffAngle) < EPSILON, "Identity child preserves parent rotation");
  }
}
