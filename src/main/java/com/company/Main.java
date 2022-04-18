package com.company;


import com.birdbrain.Finch;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;


public class Main {
    // Менеджер геймпадов (обслуживает до 4 подключённых геймпадов)
    private ControllerManager controllers;
    // Робот Финч
    private Finch finch;
    // Стики и триггеры не всегда показывают, что они находятся в начальном положении, даже если это так. Поэтому
    // введена мера погрешности, пресечение которой позволяет считать стик или триггер активным
    private final float accuracy = 0.15f;

    public void main(String[] args) {
        // Позволяет проверить геймпад на корректную работу
        // ControllerTester.run();
        run();
    }

    public void run() {
        initializeGamepad();
        initializeFinch();

        System.out.println("Ready");

        // Вычисленное роботом расстояние до ближайшего объекта в последний раз (255, потому что это максимум)
        // TODO: хотелось бы без таких тупых переменных обойтись
        int previousDistance = 255;

        ControllerState controllerState;

        // TODO: очень грязный код
        do {
            // Получает статус геймпада, с помощью которого можно узнать зажатые кнопки
            controllers.update();
            controllerState = controllers.getState(0);

            // Вычисленное роботом расстояние до ближайшего объекта в данный момент
            int distance = finch.getDistance();

            /*
             * Отображает направление робота (ВЛЕВО-ВПРАВО) в числовом формате:
             * 1 >= x > 0 -> левый стик направлен вправо, робот двигается по направлению y и вправо (сила учитывается)
             * x = 0 -> левый стик находится в начальном состоянии, робот двигается по направлению y
             * -1 <= x < 0 -> левый стик направлен влево, робот двигается по направлению y и влево (сила учитывается)
             */
            float x = Math.abs(controllerState.leftStickX) >= accuracy ? controllerState.leftStickX : 0;

            /*
             * Отображает направление робота (ВПЕРЕД-НАЗАД) в числовом формате:
             * 1 >= y > 0 -> RT зажат сильнее LT, робот двигается вперед
             * y = 0 -> LT и RT зажаты с одинаковой силой, робот не двигается
             * -1 <= y < 0 -> LT зажат сильнее RT, робот двигается назад
             */
            float y = controllerState.rightTrigger - controllerState.leftTrigger;

            // Проверка ситуаций
            checkMovement(controllerState, x, y);
            checkObstacle(y, distance, previousDistance);

            previousDistance = distance;

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Пока кнопка A служит триггером для выхода из программы
        while (!controllerState.a);

        turnFinchOff();
    }

    private void initializeGamepad() {
        controllers = new ControllerManager();
        controllers.initSDLGamepad();
    }

    private void initializeFinch() {
        // Если робот изначально не подключен, эта строчка вызовет исключение
        finch = new Finch();
    }

    private void turnFinchOff() {
        finch.stopAll();
        finch.disconnect();
    }

    private void checkMovement(ControllerState controllerState, float x, float y) {
        float absX = Math.abs(x);
        float absY = Math.abs(y);

        // Срабатывает, если зажат хотя бы один из триггеров
        if (controllerState.leftTrigger >= accuracy || controllerState.rightTrigger >= accuracy) {
            // Срабатывает, если один из триггеров зажат сильнее другого
            if (absY >= accuracy) {
                finch.setMotors(Math.min(1 + x, 1) * y * 100, Math.min(1 - x, 1) * y * 100);
            }
            // Срабатывает, когда одновременно зажаты LT и RT с одинаковой силой (с погрешностью). В этом случае робот
            // должен крутиться вокруг своей оси, если левый стик находится не в начальном положении
            else {
                double rotationStrength = (absX > 0 ? absX + controllerState.leftTrigger : 0) * 100;
                rotationStrength = x > 0 ? rotationStrength : -rotationStrength;
                finch.setMotors(rotationStrength, -rotationStrength);
            }
        } else {
            finch.setMotors(0, 0);
        }
    }

    private void checkObstacle(float y, int distance, int previousDistance) {
        float absY = Math.abs(y);

        // TODO: робот очень криво считает дистанцию, да и вообще условие вибрации кривое
        if (distance <= 5 && previousDistance > distance) {
            controllers.doVibration(0, absY, absY, (int) (absY * 200));
        }
    }
}