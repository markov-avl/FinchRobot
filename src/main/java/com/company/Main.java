package com.company;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import com.studiohartman.jamepad.tester.ControllerTester;

public class Main {

    public static void main(String[] args) {
        ControllerTester.run();
    }

    public void listen() {
        ControllerManager controllers = new ControllerManager();
        controllers.initSDLGamepad();

        System.out.println("Ready to listen");

        while (true) {
            controllers.update();
            ControllerState state = controllers.getState(0);
            if (state.a) {
                System.out.println("A pressed");
            }
            if (state.b) {
                System.exit(0);
            }

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
