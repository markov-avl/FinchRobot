package com.company;

import com.birdbrain.Finch;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import com.studiohartman.jamepad.tester.ControllerTester;

public class Main {

    public static void main(String[] args) {
//        ControllerTester.run();
        listen();
    }

    public static void listen() {
        ControllerManager controllers = new ControllerManager();
        controllers.initSDLGamepad();

        Finch finch = new Finch("A");

        System.out.println("Ready to listen");

        while (true) {
            controllers.update();
            ControllerState state = controllers.getState(0);

            float D = state.rightTrigger - state.leftTrigger;
            float x = state.leftStickX;

            System.out.println("RT: " + state.rightTrigger + " | LR: " + state.leftTrigger + " | X: " + state.leftStickX);

            if (state.leftTrigger > 0.1 || state.rightTrigger > 0.1) {
                // TODO: при зажатых LT + RT вообще не двигается, даже если крутить стик
                // TODO: виляет, если отпущен стик (погрешность видимо играет)
                finch.setMotors(Math.min(1 + x, 1) * D * 100, Math.min(1 - x, 1) * D * 100);
                System.out.println("L: " + (Math.min(1 + x, 1) * D * 100) + " | R: " + (Math.min(1 - x, 1) * D * 100));
            } else {
                finch.setMotors(0, 0);
            }

            if (state.a) {
                break;
            }

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        finch.disconnect();
    }
}
