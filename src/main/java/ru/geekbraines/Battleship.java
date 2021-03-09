package ru.geekbraines;

import java.util.Random;
import java.util.Scanner;

public class Battleship {

    private static final int EMPTY = 0;
    private static final int SHIP = 1;
    private static final int SHIP_AREA = 2;
    private static final int HIT_SHIP = -1;
    private static final int SHOT = -2;


    public static void main(String[] args) {
        System.out.println("Привет! Добро пожаловать в игру Морской бой!");
        System.out.println("Уничтожьте один четырёхпалубный, два трёхпалубных, три двухпалубных и четыре однопалубных корабля");
        do {
            int size = Integer.parseInt(args[0]);
            //int ships = Integer.parseInt(args[1]);
            play(size);
        } while(isPlayAgain());
    }

    private static boolean isPlayAgain() {
        Scanner sc = new Scanner(System.in);
        int ans;
        do {
            System.out.println("Игра окончена. \nСыграем ещё? (0 - нет, 1 - да)?");
            ans = sc.nextInt();
        } while (ans != 1 && ans != 0);
        //sc.close();
        return (ans == 1);
    }

    private static void play(int size) {
        int[][] board = genBoard(size);

        int hits = 0;
        int ships = 4 + 2*3 + 3*2 + 4;
        while (hits < ships) {
            printBoard(board);
            boolean isHit = makeMove(board);
            if (isHit) {
                System.out.println("\nПопал");
                hits++;
            } else {
                System.out.println("\nМимо");
            }
        }
    }

    private static boolean makeMove(int[][] board) {
        int row, line;
        int size = board[0].length;
        System.out.print("\nВаш ход: ");
        Scanner sc = new Scanner(System.in);
        do {
            String move = sc.nextLine().toUpperCase();
            row = move.charAt(0) - 'A';
            line = Integer.parseInt(move.substring(1)) - 1;
        } while (row < 0 || row >= size || line < 0 || line >= size);
        if (board[line][row] == SHIP) {
            board[line][row] = HIT_SHIP;
            return true;
        }
        board[line][row] = SHOT;
        return false;
    }

    private static void printBoard(int[][] board) {
        System.out.print("    ");
        for (char i = 'A'; i < board[0].length + 'A'; i++) {
            System.out.print(" " + i + " ");
        }
        int i = 0;
        System.out.println();
        for (int[] lines: board) {
            System.out.printf("%3d ", ++i);
            for (int cell: lines) {
                switch (cell) {
                    /*case SHIP:
                        System.out.print("[ ]");
                        break;
                    case EMPTY:
                        System.out.print(" . ");
                        break;*/
                    case SHOT:
                        System.out.print(" X ");
                        break;
                    case HIT_SHIP:
                        System.out.print("[X]");
                        break;
                    /*case SHIP_AREA:
                        System.out.print("[O]");
                        break;*/
                    default:
                        System.out.print(" . ");
                        break;
                }
            }
            System.out.println();
        }
    }

    private static int[][] genBoard(int size) {
        int[][] board = new int[size][size];
        // генерация четырёхпалубного корабля

        board = genShip(board,size,1,4);
        board = genShip(board,size,2,3);
        board = genShip(board,size,3,2);
        board = genShip(board,size,4,1);

        return board;
    }

    private static int[][] genShip(int[][] board, int size, int shipsCount, int shipSize) {
        Random rnd = new Random();

        for (int i = 0; i < shipsCount; i++) {
            boolean orient = rnd.nextBoolean(); // false - горизонтальная ориентация, true - вертикальная
            int i1; // вертикальный вектор
            int i2; // горизонтальный вектор
            boolean emptySpace = false;
            if (orient) {
                do {
                    i1 = rnd.nextInt((size - shipSize + 1));
                    i2 = rnd.nextInt(size);
                    emptySpace = true;
                    for (int j = 0; j < shipSize; j++) {
                        emptySpace = emptySpace && (board[i1+j][i2] == EMPTY);
                    }
                } while (!emptySpace);
            } else {
                do {
                    i1 = rnd.nextInt(size);
                    i2 = rnd.nextInt((size - shipSize + 1));
                    emptySpace = true;
                    for (int j = 0; j < shipSize; j++) {
                        emptySpace = emptySpace && (board[i1][i2+j] == EMPTY);
                    }
                } while (!emptySpace);
            }
            //запись в массив данных о положении коробля и окружающих корабль полях
            if (i1 != 0 && orient) {
                board[i1 - 1][i2] = SHIP_AREA;   // поле над левой верхней точкой коробля
                if (i2 != 0) {
                    board[i1 - 1][i2 - 1] = SHIP_AREA; // поле над левой верхней точкой коробля и левее на шаг
                }
                if (i2 != size - 1) {
                    board[i1 - 1][i2 + 1] = SHIP_AREA; // поле над левой верхней точкой коробля и правее на шаг
                }
            }
            if (i2 != 0 && !orient) {
                board[i1][i2 - 1] = SHIP_AREA;   // поле слева от корабля
                if (i1 != 0) {
                    board[i1 - 1][i2 - 1] = SHIP_AREA; // поле над левой верхней точкой коробля и левее на шаг
                }
                if (i1 != size - 1) {
                    board[i1 + 1][i2 - 1] = SHIP_AREA; // поле под левой верхней точкой коробля и правее на шаг
                }
            }

            for (int j = 0; j < shipSize; j++) {
                if (orient) { // если вертикальный корабль
                    board[i1 + j][i2] = SHIP;
                    if (i2 != 0) {
                        board[i1 + j][i2 - 1] = SHIP_AREA; // слева от корабля
                    }
                    if (i2 != size - 1) {
                        board[i1 + j][i2 + 1] = SHIP_AREA; // справа от корабля
                    }
                } else {  // если горизонтальный корабль
                    board[i1][i2 + j] = SHIP;
                    if (i1 != 0) {
                        board[i1 - 1][i2 + j] = SHIP_AREA; // над кораблём
                    }
                    if (i1 != size - 1) {
                        board[i1 + 1][i2 + j] = SHIP_AREA; // под кораблём
                    }
                }
            }

            if ((i1 + shipSize) < size && orient) {
                board[i1 + shipSize][i2] = SHIP_AREA;   // поле под левой нижней точкой коробля
                if (i2 != 0) {
                    board[i1 + shipSize][i2 - 1] = SHIP_AREA; // поле под левой нижней точкой коробля и левее на шаг
                }
                if (i2 != size - 1) {
                    board[i1 + shipSize][i2 + 1] = SHIP_AREA; // поле под левой нижней точкой коробля и правее на шаг
                }
            }
            if ((i2 + shipSize) < size && !orient) {
                board[i1][i2 + shipSize] = SHIP_AREA;   // поле справа от корабля
                if (i1 != 0) {
                    board[i1 - 1][i2 + shipSize] = SHIP_AREA; // поле под правой нижней точкой коробля и левее на шаг
                }
                if (i1 != size - 1) {
                    board[i1 + 1][i2 + shipSize] = SHIP_AREA; // поле под правой нижней точкой коробля и правее на шаг
                }
            }
        }
        return board;
    }
}
