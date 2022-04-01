package com.company;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class RobotKeyListener implements KeyListener {
    static public final int LEFT = -2;
    static public final int BACKWARD = -1;
    static public final int STAYING = 0;
    static public final int FORWARD = 1;
    static public final int RIGHT = 2;
    static private final int speed = 50;

    private final Finch finch = new Finch("A");
    private int moveState = RobotKeyListener.STAYING;
    private int turnState = RobotKeyListener.STAYING;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public void moveForwardAndLeft() {
        finch.setMotors(speed, 2 * speed);
    }

    public void moveForwardAndRight() {
        finch.setMotors(2 * speed, speed);
    }

    public void moveBackwardAndLeft() {
        finch.setMotors(-speed, -2 * speed);
    }

    public void moveBackwardAndRight() {
        finch.setMotors(-2 * speed, -speed);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_G) {
            System.exit(0);
        }

        if (e.getKeyCode() == KeyEvent.VK_W) {
            moveState = FORWARD;
            if (turnState == STAYING) {
                finch.setMotors(speed, speed);
            } else if (turnState == LEFT) {
                moveForwardAndLeft();
            } else {
                moveForwardAndRight();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            moveState = BACKWARD;
            if (turnState == STAYING) {
                finch.setMotors(-speed, -speed);
            } else if (turnState == LEFT) {
                moveBackwardAndLeft();
            } else {
                moveBackwardAndRight();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_A) {
            turnState = LEFT;
            if (moveState == FORWARD) {
                moveForwardAndLeft();
            } else if (moveState == BACKWARD) {
                moveBackwardAndLeft();
            } else {
                finch.setMotors(-speed, speed);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            turnState = RIGHT;
            if (moveState == FORWARD) {
                moveForwardAndRight();
            } else if (moveState == BACKWARD) {
                moveBackwardAndRight();
            } else {
                finch.setMotors(speed, -speed);
            }
        }

        System.out.println("The key Pressed was: " + e.getKeyChar());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_S) {
            moveState = STAYING;
        } else if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_D) {
            turnState = STAYING;
        }
        System.out.println("The key Released was: " + e.getKeyChar());
        finch.setMotors(0, 0);
    }

    public static void main(String[] args) {
        //Setting the Frame and Labels
        Frame f = new Frame("Demo");
        f.setLayout(new FlowLayout());
        f.setSize(500, 500);
        Label l = new Label();
        l.setText("This is a demonstration");
        f.add(l);
        f.setVisible(true);
        //Creating and adding the key listener
        RobotKeyListener k = new RobotKeyListener();
        f.addKeyListener(k);
    }
}