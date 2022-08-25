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



        //Tobbe jobbar start
        List<Position>obstacles=new ArrayList<>();
        //Position[] obstacles = new Position[10];
        HorizontalObstacle(0,0,80,terminal,block,obstacles);//top frame
        HorizontalObstacle(0,23,80,terminal,block,obstacles);//bottom frame
        HorizontalObstacle(5,5,20,terminal,block,obstacles);
        HorizontalObstacle(5,18,20,terminal,block,obstacles);
        HorizontalObstacle(55,5,20,terminal,block,obstacles);
        HorizontalObstacle(55,18,20,terminal,block,obstacles);

        VerticalObstacle(0,0,23,terminal,block,obstacles);//left frame
        VerticalObstacle(79,0,23,terminal,block,obstacles);//right frame
        VerticalObstacle(14,9,6,terminal,block,obstacles);
        VerticalObstacle(65,9,6,terminal,block,obstacles);
        VerticalObstacle(35,0,6,terminal,block,obstacles);//left mid divider
        VerticalObstacle(35,12,12,terminal,block,obstacles);
        VerticalObstacle(45,0,12,terminal,block,obstacles);//top right mid divider
        VerticalObstacle(45,18,15,terminal,block,obstacles);

        //Tobbe jobbar end

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
    public static void HorizontalObstacle(int x,int y,int length,Terminal terminal,char block, List<Position> obstacles  ) throws IOException {

        // Create obstacles array
        for(int i = 0;i<length;i++){
            obstacles.add(new Position(x,y));
            x++;
            //obstacles[count] = new Position(x+i, y);

        }

        // Use obstacles array to print to lanterna
        for (Position p : obstacles) {
            terminal.setCursorPosition(p.x, p.y);
            terminal.putCharacter(block);
        }
    }
    public static void VerticalObstacle(int x,int y,int length,Terminal terminal,char block, List<Position> obstacles  ) throws IOException {

        // Create obstacles array
        for(int i = 0;i<length;i++){
            obstacles.add(new Position(x,y));
            y++;
            //obstacles[count] = new Position(x+i, y);

        }

        // Use obstacles array to print to lanterna
        for (Position p : obstacles) {
            terminal.setCursorPosition(p.x, p.y);
            terminal.putCharacter(block);
        }
    }
}