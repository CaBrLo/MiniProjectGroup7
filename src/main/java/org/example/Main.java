package org.example;


import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextImage;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.List;


import java.util.Random;

public class Main {
    public static void main(String[] args) throws Exception {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminal();

        TextGraphics tg = terminal.newTextGraphics();


        terminal.setCursorVisible(false);
        // test comment
        int x = 5;
        int y = 5;
        final char player = '\u263a';
        final char block = '\u2588';
        final char bomb = 'O';

        terminal.setCursorPosition(x, y);
        terminal.putCharacter(player);
        //Points start (Caroline)
        //
        List<Position> points = new ArrayList<>();
        points.add(new Position(4,4));
        points.add(new Position(25, 10));
        points.add(new Position(10,20));
        points.add(new Position(5,10));
        points.add(new Position(19,4));

        int score = 0;

        for (Position point : points)
        {
            terminal.setCursorPosition(point.x, point.y);
            terminal.putCharacter('$');

        }
        //Points end

        //monsters
        List<Position> monsters = new ArrayList<>();
        monsters.add(new Position(23, 23));



        // Create obstacles array
        Position[] obstacles = new Position[10];
        for(int i = 0;i<10;i++){
            obstacles[i] = new Position(10+i, 10);
        }

        // Use obstacles array to print to lanterna
        for (Position p : obstacles) {
            terminal.setCursorPosition(p.x, p.y);
            terminal.putCharacter(block);
        }

        //random position för bomben
        Random r = new Random();
        Position bombPosition = new Position(r.nextInt(80), r.nextInt(24));

        terminal.setCursorPosition(bombPosition.x, bombPosition.y);

        terminal.putCharacter(bomb);

        terminal.flush();


        boolean continueReadingInput = true;

        while (continueReadingInput) {

            KeyStroke keyStroke = null;
            do {
                Thread.sleep(5); // might throw InterruptedException
                keyStroke = terminal.pollInput();
            } while (keyStroke == null);


            KeyType type = keyStroke.getKeyType();
            Character c = keyStroke.getCharacter(); // used Character instead of char because it might be null

            System.out.println("keyStroke.getKeyType(): " + type
                    + " keyStroke.getCharacter(): " + c);

            if (c == Character.valueOf('q')) {
                continueReadingInput = false;
                terminal.close();
                System.out.println("quit");
            }

            int oldX = x; // save old position x
            int oldY = y; // save old position y

            switch (keyStroke.getCharacter()) {//if arrows "keyStroke.getKeyType()"
                case 's'://ArrowDown
                    y += 1;
                    break;
                case 'w'://ArrowUp
                    y -= 1;
                    break;
                case 'd'://ArrowRight
                    x += 1;
                    break;
                case 'a'://ArrowLeft
                    x -= 1;
                    break;
            }
            boolean crashIntoObstacle = false;
            for (Position p : obstacles) {
                if (p.x == x && p.y == y) {
                    crashIntoObstacle = true;
                }
            }
// Score start (Caroline)
            for (Position p : points){
                if (p.x == x && p.y == y)
                {
                    score++;
                }
            }
            //System.out.println(score);
            String scoreString = Integer.toString(score);


                terminal.setCursorPosition(45,2);
                terminal.putCharacter(scoreString.charAt(0));

            terminal.flush();
            // Score end

            if (crashIntoObstacle) {
                x = oldX;
                y = oldY;
            } else {
                terminal.setCursorPosition(oldX, oldY); // move cursor to old position
                terminal.putCharacter(' '); // clean up by printing space on old position
                terminal.setCursorPosition(x, y);
                terminal.putCharacter(player);
            }
            // check if player runs into the bomb
            if (bombPosition.x == x && bombPosition.y == y) {
                Random l = new Random();
                Position bombPosition1 = new Position(l.nextInt(20), l.nextInt(24));

                terminal.setCursorPosition(bombPosition.x, bombPosition.y);

                terminal.putCharacter(bomb);
                terminal.bell();
                terminal.close();
                continueReadingInput = false;
            }

            //handling monsters
            for (Position monster : monsters) {
                terminal.setCursorPosition(monster.x, monster.y);
                terminal.putCharacter(' ');

                if (x > monster.x) {
                    monster.x++;
                }
                else if (x < monster.x) {
                    monster.x--;
                }
                if (y > monster.y) {
                    monster.y++;
                }
                else if (y < monster.y) {
                    monster.y--;
                }

                terminal.setCursorPosition(monster.x, monster.y);
                terminal.putCharacter('X');
            }

            // Is the player alive?
            for (Position monster : monsters) {
                if (monster.x == x && monster.y == y) {
                    continueReadingInput = false;
                    terminal.bell();
                    System.out.println("Game Over!");
                }
            }

            terminal.flush();
        }

    }
}