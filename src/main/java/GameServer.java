import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class GameServer implements Runnable {

    public static GameServer gameServer;
    private DatagramChannel serverDatagramChannel;

    private final ByteBuffer buffer;
    private final Properties properties;

    private List<SocketAddress> addresses;

    public static void init() throws Exception {
        if (gameServer != null) throw new Exception("Сервер уже был создан");
        gameServer = new GameServer();
    }

    private GameServer() throws IOException {
        properties = new Properties();
        properties.load(new FileReader(new File("src/main/resources/Server.properties")));

        serverDatagramChannel = DatagramChannel.open();

        serverDatagramChannel.bind(new InetSocketAddress(
                properties.getProperty("host"),
                Integer.parseInt(properties.getProperty("gamePort"))));
        serverDatagramChannel.configureBlocking(false);

        buffer = ByteBuffer.allocate(Integer.parseInt(properties.getProperty("bufferSize")));
        addresses = new ArrayList<>();
        System.out.println("Game server started...");
    }

    @Override
    public void run() {
        while (true) {
            try {
                SocketAddress socketAddress;
                buffer.clear();
                if ((socketAddress = serverDatagramChannel.receive(buffer)) != null) {

                    buffer.clear();
                    byte[] data = buffer.array();

                    if (data.length <= 0) continue;

                    if (data[0] == GameMessageType.INIT.getCode()) {
                        addresses.add(socketAddress);
                    }
                    if (data[0] == GameMessageType.TANK_POSITION.getCode() && data.length >= 22) {
                        for (SocketAddress address : addresses)
                            writeData(ByteBuffer.wrap(data), address);
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void writeData(ByteBuffer buffer, SocketAddress socketAddress) throws IOException {
        serverDatagramChannel.send(buffer, socketAddress);
    }
}
import java.util.ArrayList;
//import java.util.Random;
//import java.util.Stack;
//
//public class MazeGenerator {
//
//
//    private int width, height;
//
//    public MazeGenerator(int width, int height) {
//        this.width = width;
//        this.height = height;
//    }
//
//    public byte[][] GenerateMaze() {
//        MazeGeneratorCell[][] maze = new MazeGeneratorCell[width][height];
//
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                maze[x][y] = new MazeGeneratorCell(x, y);
//            }
//        }
//
//        for (int x = 0; x < width; x++) maze[x][height - 1].setLeft(false);
//        for (int y = 0; y < height; y++) maze[width - 1][y].setButtom(false);
//
//        RemoveWallsWithBackTrackers(maze);
//
//        byte[][] mazeCell = new byte[width][height];
//        for (int x = 0; x < width; x++) {
//            for (int y = 0; y < height; y++) {
//                if (maze[x][y].isLeft() && maze[x][y].isButtom()) mazeCell[x][y] = 1;
//                else if (!maze[x][y].isLeft() && maze[x][y].isButtom()) mazeCell[x][y] = 2;
//                else if (maze[x][y].isLeft() && !maze[x][y].isButtom()) mazeCell[x][y] = 3;
//                else mazeCell[x][y] = 0;
//            }
//        }
//
//        return mazeCell;
//    }
//
//    private void RemoveWallsWithBackTrackers(MazeGeneratorCell[][] maze) {
//        MazeGeneratorCell curCell = maze[0][0];
//        curCell.setVisited(true);
//
//        Stack<MazeGeneratorCell> stack = new Stack<MazeGeneratorCell>();
//        do {
//            ArrayList<MazeGeneratorCell> unvisitedNeightboors = new ArrayList<>();
//
//            if (curCell.getX() > 0 && !maze[curCell.getX() - 1][curCell.getY()].isVisited())
//                unvisitedNeightboors.add(maze[curCell.getX() - 1][curCell.getY()]);
//
//            if (curCell.getY() > 0 && !maze[curCell.getX()][curCell.getY() - 1].isVisited())
//                unvisitedNeightboors.add(maze[curCell.getX()][curCell.getY() - 1]);
//
//            if (curCell.getX() < width - 2 && !maze[curCell.getX() + 1][curCell.getY()].isVisited())
//                unvisitedNeightboors.add(maze[curCell.getX() + 1][curCell.getY()]);
//
//            if (curCell.getY() < height - 2 && !maze[curCell.getX()][curCell.getY() + 1].isVisited())
//                unvisitedNeightboors.add(maze[curCell.getX()][curCell.getY() + 1]);
//
//            if (unvisitedNeightboors.size() > 0) {
//                Random random = new Random();
//
//                MazeGeneratorCell chosenCell = unvisitedNeightboors.get(random.nextInt(unvisitedNeightboors.size()));
//                RemoveWall(curCell, chosenCell);
//
//                chosenCell.setVisited(true);
//                curCell = chosenCell;
//                stack.push(chosenCell);
//            } else {
//                curCell = stack.pop();
//            }
//
//        } while (stack.size() > 0);
//    }
//
//    private void RemoveWall(MazeGeneratorCell curCell, MazeGeneratorCell chosenCell) {
//        if (curCell.getX() == chosenCell.getX()) {
//            if (curCell.getY() > chosenCell.getY()) curCell.setButtom(false);
//            else chosenCell.setButtom(false);
//        } else {
//            if (curCell.getX() > chosenCell.getX()) curCell.setLeft(false);
//            else chosenCell.setLeft(false);
//        }
//    }
//
//
//    private class MazeGeneratorCell {
//        private int x, y;
//        private boolean left = true, buttom = true;
//        private boolean visited = false;
//
//        public MazeGeneratorCell(int x, int y) {
//            this.x = x;
//            this.y = y;
//        }
//
//        public int getX() {
//            return x;
//        }
//
//        public void setX(int x) {
//            this.x = x;
//        }
//
//        public int getY() {
//            return y;
//        }
//
//        public void setY(int y) {
//            this.y = y;
//        }
//
//        public boolean isLeft() {
//            return left;
//        }
//
//        public void setLeft(boolean left) {
//            this.left = left;
//        }
//
//        public boolean isButtom() {
//            return buttom;
//        }
//
//        public void setButtom(boolean buttom) {
//            this.buttom = buttom;
//        }
//
//        public boolean isVisited() {
//            return visited;
//        }
//
//        public void setVisited(boolean visited) {
//            this.visited = visited;
//        }
//    }
//}

