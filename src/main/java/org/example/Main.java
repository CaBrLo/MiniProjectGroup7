package org.example;


import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.util.ArrayList;
import java.util.List;


import java.util.Random;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminal();
        TextGraphics tg = terminal.newTextGraphics();
        terminal.setCursorVisible(false);
        // test comment
        int x = 2;
        int y = 2;
        final char player = '\u263a';
        final char block = '\u2588';
        final char bomb = 'O';

        // färg och design?


        terminal.setCursorPosition(x, y);
        terminal.putCharacter(player);

        //Score and points start (Caroline)
        int score = 0;
        String scoreHeader = "Score: ";
        for (int i = 0; i < scoreHeader.length(); i++) {
            terminal.setCursorPosition(68 + i, 0);
            terminal.putCharacter(scoreHeader.charAt(i));
        }

        List<Position> fivePoints = new ArrayList<>();
        fivePoints.add(new Position(4, 4));
        fivePoints.add(new Position(75, 10));
        fivePoints.add(new Position(70, 20));
        fivePoints.add(new Position(60, 10));
        fivePoints.add(new Position(19, 4));
        for (Position point : fivePoints) {
            terminal.setCursorPosition(point.x, point.y);
            terminal.putCharacter('$');
        }

        List<Position> tenPoints = new ArrayList<>();
        tenPoints.add(new Position(8, 4));
        tenPoints.add(new Position(25, 10));
        tenPoints.add(new Position(10, 20));
        tenPoints.add(new Position(5, 10));
        tenPoints.add(new Position(72, 8));
        for (Position point : tenPoints) {
            terminal.setCursorPosition(point.x, point.y);
            terminal.putCharacter('£');
        }
        //Score and points end

        //monsters
        List<Position> monsters = new ArrayList<>();
        monsters.add(new Position(23, 21));


        //Tobbe jobbar start
        List<Position> obstacles = new ArrayList<>();
        //Position[] obstacles = new Position[10];
        HorizontalObstacle(0, 1, 80, terminal, block, obstacles);//top frame
        HorizontalObstacle(0, 23, 80, terminal, block, obstacles);//bottom frame
        HorizontalObstacle(5, 6, 20, terminal, block, obstacles);
        HorizontalObstacle(5, 19, 20, terminal, block, obstacles);
        HorizontalObstacle(55, 6, 20, terminal, block, obstacles);
        HorizontalObstacle(55, 19, 20, terminal, block, obstacles);

        VerticalObstacle(0, 1, 23, terminal, block, obstacles);//left frame
        VerticalObstacle(79, 1, 23, terminal, block, obstacles);//right frame
        VerticalObstacle(14, 10, 6, terminal, block, obstacles);
        VerticalObstacle(65, 10, 6, terminal, block, obstacles);
        VerticalObstacle(35, 1, 6, terminal, block, obstacles);//left mid divider
        VerticalObstacle(35, 13, 12, terminal, block, obstacles);
        VerticalObstacle(45, 1, 12, terminal, block, obstacles);//top right mid divider
        VerticalObstacle(45, 19, 15, terminal, block, obstacles);

        //Tobbe jobbar end

        //random position för bomben
        Position bombPosition = new Position(45, 15);
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


// Score & points start (Caroline)
            for (Position p : fivePoints) {
                if (p.x == x && p.y == y) {
                    score += 5;
                    p.x = 0;    //moves the point's location to 0,0
                    p.y = 0;
                }
            }
            for (Position p : tenPoints) {
                if (p.x == x && p.y == y) {
                    score += 10;
                    p.x = 0;    //moves the point's location to 0,0
                    p.y = 0;
                }
            }

            String scoreString = Integer.toString(score);
            for (int i = 0; i < scoreString.length(); i++) {
                terminal.setCursorPosition(75 + i, 0);
                terminal.putCharacter(scoreString.charAt(i));
            }
            // Score & points end

            if (crashIntoObstacle) {
                x = oldX;
                y = oldY;
            } else {
                terminal.setCursorPosition(oldX, oldY); // move cursor to old position
                terminal.putCharacter(' '); // clean up by printing space on old position
                terminal.setCursorPosition(x, y);
                terminal.putCharacter(player);
            }

            //handling monsters
            for (Position monster : monsters) {
                terminal.setCursorPosition(monster.x, monster.y);
                terminal.putCharacter(' ');

                int oldMonterX=monster.x;
                int oldMonterY=monster.y;

                if (x > monster.x) {
                    monster.x++;
                } else if (x < monster.x) {
                    monster.x--;
                }
                if (y > monster.y) {
                    monster.y++;
                } else if (y < monster.y) {
                    monster.y--;
                }
                boolean monsterCrashIntoObstacle = false;
                for (Position p : obstacles) {
                    if (p.x == monster.x && p.y == monster.y) {
                        monsterCrashIntoObstacle = true;
                    }
                }
                if (monsterCrashIntoObstacle) {
                    monster.x = oldMonterX-1;
                    monster.y = oldMonterY;
                } else {
                    terminal.setCursorPosition(oldMonterX, oldMonterY); // move cursor to old position
                    terminal.putCharacter(' '); // clean up by printing space on old position
                    terminal.setCursorPosition(monster.x, monster.y);
                    terminal.putCharacter('X');
                }

                terminal.setCursorPosition(monster.x, monster.y);
                terminal.putCharacter('X');
            }

            // check if player runs into the bomb
            if (bombPosition.x == x && bombPosition.y == y) {
                terminal.setCursorPosition(bombPosition.x, bombPosition.y);

                terminal.putCharacter(bomb);
                terminal.bell();
                GameOver(terminal, tg);
                //terminal.close();
                continueReadingInput = false;
            }

                // Is the player alive?
                for (Position monster : monsters) {
                    if (monster.x == x && monster.y == y) {
                        continueReadingInput = false;
                        terminal.bell();
                        System.out.println("Game Over!");
                        GameOver(terminal, tg);
                    }
                }
                terminal.flush();
            }

        }
        public static void HorizontalObstacle ( int x, int y, int length, Terminal terminal,char block, List<
        Position > obstacles  ) throws IOException {

            // Create obstacles array
            for (int i = 0; i < length; i++) {
                obstacles.add(new Position(x, y));
                x++;
                //obstacles[count] = new Position(x+i, y);

            }

            // Use obstacles array to print to lanterna
            for (Position p : obstacles) {
                terminal.setCursorPosition(p.x, p.y);
                terminal.putCharacter(block);
            }
        }
        public static void VerticalObstacle ( int x, int y, int length, Terminal terminal,char block, List<
        Position > obstacles  ) throws IOException {

            // Create obstacles array
            for (int i = 0; i < length; i++) {
                obstacles.add(new Position(x, y));
                y++;
                //obstacles[count] = new Position(x+i, y);

            }

            // Use obstacles array to print to lanterna
            for (Position p : obstacles) {
                terminal.setCursorPosition(p.x, p.y);
                terminal.putCharacter(block);
            }
        }
        public static void GameOver (Terminal terminal, TextGraphics tg) throws Exception
    {
                for (TextColor.ANSI bgc : TextColor.ANSI.values()) {
                    tg.setBackgroundColor(bgc);
                    tg.fill(' ');
                    terminal.flush();
                    Thread.sleep(100);

                    String gameOver = "GAME OVER!";
                    for (int i = 0; i < gameOver.length(); i++) {
                        terminal.setCursorPosition(i + 35, 10);
                        terminal.putCharacter(gameOver.charAt(i));
                    }
                }
    }
}
