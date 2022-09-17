package me.hydos.crabday.hayday;

import me.hydos.crabday.window.Window;

import java.awt.*;
import java.awt.event.InputEvent;

/**
 * Interfaces with the Hayday game.
 */
public class HaydayInterface {

    private final Window handle;
    private final Robot robot;

    public HaydayInterface(Window handle) {
        try {
            this.handle = handle;
            this.robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException("Unable to create AWT Robot. GraphicsEnvironment.isHeadless() is true.");
        }
    }

    public void touchPoint(Point point) {
        this.robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    }

    public void releasePoint() {
        this.robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
    }
}
