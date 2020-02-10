import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

public class Main {
  static Tile[][] factory;
  static boolean[][] detectedTiles;

  public static void main(String[] args) {
    Scanner reader = new Scanner(System.in);
    String[] dimensions = reader.nextLine().split(" ", 2);
    int height = Integer.parseInt(dimensions[0]);
    int width = Integer.parseInt(dimensions[1]);
    int hStart = -1;
    int wStart = -1;

    factory = new Tile[height][width];
    detectedTiles = new boolean[height][width];

    for(int i = 0; i < height; i++) {
      char[] cells = reader.nextLine().toCharArray();
      for(int j = 0; j < width; j++) {
        if(cells[j] == 'S') {
          factory[i][j] = new Tile(i, j, 'S', 0);
          hStart = i;
          wStart = j;
        } else
          factory[i][j] = new Tile(i, j, cells[j], -1);
      }
    }

    for(int i = 0; i < height; i++)
      for(int j = 0; j < width; j++) {
        if(factory[i][j].type == 'C') {
          int i0 = i;
          int j0 = j;
          // Camera Position
          detectedTiles[i0][j0] = true;
          // Up of Camera
          while(i0 > 0 && factory[i0 - 1][j0].type != 'W') {
            i0--;
            detectedTiles[i0][j0] = factory[i0][j0].type == '.' || factory[i0][j0].type == 'C' || factory[i0][j0].type == 'S';
          }
          i0 = i;
          // Down of Camera
          while(i0 < height - 1 && factory[i0 + 1][j0].type != 'W') {
            i0++;
            detectedTiles[i0][j0] = factory[i0][j0].type == '.' || factory[i0][j0].type == 'C' || factory[i0][j0].type == 'S';
          }
          i0 = i;
          // Left of Camera
          while(j0 > 0 && factory[i0][j0 - 1].type != 'W') {
            j0--;
            detectedTiles[i0][j0] = factory[i0][j0].type == '.' || factory[i0][j0].type == 'C' || factory[i0][j0].type == 'S';
          }
          j0 = j;
          // Right of Camera
          while(j0 < width - 1 && factory[i0][j0 + 1].type != 'W') {
            j0++;
            detectedTiles[i0][j0] = factory[i0][j0].type == '.' || factory[i0][j0].type == 'C' || factory[i0][j0].type == 'S';
          }
        }
      }

    // BFS Start
    Queue<Tile> queue = new PriorityQueue<>((Comparator.comparingInt(o -> o.d)));
    queue.add(factory[hStart][wStart]);

    int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    while(!queue.isEmpty()) {
      Tile current = queue.remove();
//      System.out.println("At: " + current);
      if(detectedTiles[current.y][current.x] || factory[current.y][current.x].type == 'W') {
//        System.out.println(" -Skipped (Invalid Position): " + current);
        continue;
      } else {
        for(int[] direction : directions) {
          int dy = direction[0];
          int dx = direction[1];
          Tile destTile = factory[current.y + dy][current.x + dx];
//          System.out.println("  -Checking: " + destTile);
          if(destTile.d != -1 || destTile.type == 'X' || detectedTiles[destTile.y][destTile.x]) {
//            System.out.println("   -Skipped: " + destTile);
            continue;
          } else if(destTile.type == '.') {
            destTile.d = current.d + 1;
            if(destTile.type != 'W') {
              queue.add(destTile);
//              System.out.println("   +Added (.): " + destTile);
            }
          } else {
            destTile.d = current.d + 1;
            // Move along conveyors
            while(destTile.type == 'U' || destTile.type == 'D' || destTile.type == 'L' || destTile.type == 'R') {
              switch(destTile.type) {
                case 'U':
                  factory[destTile.y - 1][destTile.x].d = current.d + 1;
                  destTile = factory[destTile.y - 1][destTile.x];
                  break;
                case 'D':
                  factory[destTile.y + 1][destTile.x].d = current.d + 1;
                  destTile = factory[destTile.y + 1][destTile.x];
                  break;
                case 'L':
                  factory[destTile.y][destTile.x - 1].d = current.d + 1;
                  destTile = factory[destTile.y][destTile.x - 1];
                  break;
                case 'R':
                  factory[destTile.y][destTile.x + 1].d = current.d + 1;
                  destTile = factory[destTile.y][destTile.x + 1];
                  break;
              }
              // RIP Didn't give more points
              if(destTile.type == '.' && detectedTiles[destTile.y][destTile.x]) {
                destTile.d = -1;
                break;
              }
//                System.out.println("    Conveyed to: " + destTile);
            }
            if(destTile.type != 'W') {
              queue.add(destTile);
//                System.out.println("   +Added (Conveyor): " + destTile);
            }
          }
        }
      }
    }

//    System.out.println("Done");
    for(int i = 0; i < height; i++)
      for(int j = 0; j < width; j++)
        if(factory[i][j].type == '.')
          System.out.println(factory[i][j].d);
  }
}

//class Tile {
//  int y;
//  int x;
//  char type;
//  int d;
//  public Tile(int y, int x, char type, int d) {
//    this.y = y;
//    this.x = x;
//    this.type = type;
//    this.d = d;
//  }
//}