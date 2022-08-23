package org.example;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

public class Main {
    public static void main(String[] args) throws Exception {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Terminal terminal = terminalFactory.createTerminal();
        terminal.setCursorVisible(false);

        int x = 5;
        int y = 5;
        final char player = 'X';
        final char block = '\u2588';
        terminal.setCursorPosition(x, y);
        terminal.putCharacter(player);

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

            if (crashIntoObstacle) {
                x = oldX;
                y = oldY;
            } else {
                terminal.setCursorPosition(oldX, oldY); // move cursor to old position
                terminal.putCharacter(' '); // clean up by printing space on old position
                terminal.setCursorPosition(x, y);
                terminal.putCharacter(player);
            }
            terminal.flush();
        }
    }
}