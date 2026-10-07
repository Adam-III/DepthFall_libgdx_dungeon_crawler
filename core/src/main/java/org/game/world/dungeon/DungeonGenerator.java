package org.game.world.dungeon;

import org.game.world.rooms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Main class responsible for the procedural generation of the dungeon map.
 * It utilizes a "Random Walk" algorithm with a backtracking mechanism
 * to create a continuous path from the start to the boss and add side branches.
 */
public class DungeonGenerator {

    private static int GRID_SIZE; // Size of the square grid (e.g., 40x40)

    // Parameters for the main path (from StartRoom to BossRoom)
    private static int MAIN_PATH_MIN_LENGTH;
    private static int MAIN_PATH_MAX_LENGTH;

    // Parameters for side branches (Branch paths)
    private static int MIN_BRANCH_COUNT;
    private static int MAX_BRANCH_COUNT;
    private static int BRANCH_MIN_LENGTH;
    private static int BRANCH_MAX_LENGTH;

    private static final Random RNG = new Random();
    private static final Logger logger = LoggerFactory.getLogger(DungeonGenerator.class);

    /**
     * Main entry point for dungeon generation.
     * @param currentDifficulty Object containing probability settings and lengths for the current level.
     * @return A 2D array of Room objects representing the completed map.
     */
    public static Room[][] generateDungeon(DungeonDifficulty currentDifficulty) {
        DungeonDifficulty diff = currentDifficulty;

        // Initialize limits based on current difficulty
        GRID_SIZE = diff.getGridSize();
        MAIN_PATH_MIN_LENGTH = diff.getMinMainPathLength();
        MAIN_PATH_MAX_LENGTH = diff.getMaxMainPathLength();
        MIN_BRANCH_COUNT = diff.getMinBranchCount();
        MAX_BRANCH_COUNT = diff.getMaxBranchCount();
        BRANCH_MIN_LENGTH = diff.getMinBranchLength();
        BRANCH_MAX_LENGTH = diff.getMaxBranchLength();

        int globalAttempts = 0;
        int maxRetries = 100; // Number of attempts to generate a valid dungeon before giving up

        while (globalAttempts < maxRetries) {
            globalAttempts++;

            // Create an empty grid of types (information about type only, not yet objects)
            RoomType[][] typeGrid = new RoomType[GRID_SIZE][GRID_SIZE];
            for (int y = 0; y < GRID_SIZE; y++) Arrays.fill(typeGrid[y], RoomType.EMPTY);

            List<int[]> mainPath = new ArrayList<>(); // Coordinate list for the main path
            List<List<int[]>> branchList = new ArrayList<>(); // List of coordinate lists for branches

            // STEP 1: Generate the main path. If it fails (gets stuck), retry the whole dungeon.
            if (!generateMainPath(typeGrid, mainPath)) continue;

            // STEP 2: Generate side branches attached to the main path.
            if (!generateBranches(typeGrid, branchList, mainPath)) continue;

            // STEP 3: Label rooms in the grid (START, FINAL_ROOM, BRANCH_PATH)
            labelAllRooms(typeGrid, mainPath, branchList);

            // STEP 4: Map branch IDs. Important for RoomFactory to know
            // if a chest has already been generated in a specific branch (chestPerBranch).
            Map<String, Integer> branchIdMap = new HashMap<>();
            for (int i = 0; i < branchList.size(); i++) {
                for (int[] pos : branchList.get(i)) {
                    branchIdMap.put(pos[0] + "," + pos[1], i);
                }
            }

            // STEP 5: Convert the type grid into actual class instances (BattleRoom, ChestRoom, etc.)
            return finalizeDungeonArray(typeGrid, GRID_SIZE, diff, branchIdMap, branchList.size());
        }

        // If generation fails within the retry limit, return a predefined layout
        logger.warn("Dungeon generation stalled on floor {}. Using the fallback layout.", diff.getDifficulty());
        return generateFallbackLayout(diff);
    }

    /**
     * Converts the typeGrid into an array of Room objects and sets neighbor connections (North, South...).
     */
    private static Room[][] finalizeDungeonArray(RoomType[][] typeGrid, int size, DungeonDifficulty diff, Map<String, Integer> branchIdMap, int branchCount) {
        Room[][] dungeon = new Room[size][size];
        RoomCounter counter = new RoomCounter();
        int[] chestPerBranch = new int[Math.max(branchCount + 1, 20)];

        // First pass: Create instances via RoomFactory
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (typeGrid[y][x] == RoomType.EMPTY) continue;

                String key = x + "," + y;
                Integer branchId = (branchIdMap != null) ? branchIdMap.get(key) : null;

                dungeon[y][x] = RoomFactory.create(
                    typeGrid[y][x], x, y, diff, counter, branchId, chestPerBranch
                );
            }
        }

        // Second pass: Analyze neighbors and set boolean flags (north, south, east, west)
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Room current = dungeon[y][x];
                if (current == null) continue;
                if (y + 1 < size && dungeon[y + 1][x] != null) current.north = true;
                if (y - 1 >= 0 && dungeon[y - 1][x] != null) current.south = true;
                if (x + 1 < size && dungeon[y][x + 1] != null) current.east = true;
                if (x - 1 >= 0 && dungeon[y][x - 1] != null) current.west = true;
            }
        }
        return dungeon;
    }

    /**
     * Checks if adjacent tiles (except the one we came from) are occupied.
     * Prevents corridors from "touching" sideways or diagonally (maintains a clean snake-like shape).
     */
    private static boolean touchesAnyRoomExceptPrevious(RoomType[][] grid, int nextX, int nextY, int prevX, int prevY) {
        for (int[] dir : cardinalDirections()) {
            int adjX = nextX + dir[0];
            int adjY = nextY + dir[1];

            if (!isInsideGrid(adjX, adjY)) continue;
            if (adjX == prevX && adjY == prevY) continue;

            if (grid[adjY][adjX] != RoomType.EMPTY) return true;
        }
        return false;
    }

    /**
     * Hard-coded layout for generator failure cases.
     */
    public static Room[][] generateFallbackLayout(DungeonDifficulty diff) {
        int size = 40;
        RoomType[][] typeGrid = new RoomType[size][size];
        for (int y = 0; y < size; y++) Arrays.fill(typeGrid[y], RoomType.EMPTY);

        int sX = size / 2;
        int sY = size / 2;

        // Manually assembled cross/shape for emergency mode
        typeGrid[sY][sX] = RoomType.START;
        typeGrid[sY][sX-1] = RoomType.MAIN_PATH;
        typeGrid[sY][sX+1] = RoomType.MAIN_PATH;
        typeGrid[sY][sX+2] = RoomType.MAIN_PATH;
        typeGrid[sY+1][sX] = RoomType.BRANCH_PATH;
        typeGrid[sY+2][sX] = RoomType.BRANCH_PATH;
        typeGrid[sY+3][sX] = RoomType.BRANCH_PATH;
        typeGrid[sY+3][sX+1] = RoomType.BRANCH_PATH;
        typeGrid[sY+4][sX+1] = RoomType.BRANCH_PATH;
        typeGrid[sY-1][sX] = RoomType.MAIN_PATH;
        typeGrid[sY-2][sX] = RoomType.MAIN_PATH;
        typeGrid[sY-3][sX] = RoomType.MAIN_PATH;
        typeGrid[sY-4][sX] = RoomType.MAIN_PATH;
        typeGrid[sY-4][sX-1] = RoomType.MAIN_PATH;
        typeGrid[sY-4][sX-2] = RoomType.FINAL_ROOM;

        return finalizeDungeonArray(typeGrid, size, diff, null, 0);
    }

    private static boolean isInsideGrid(int x, int y) {
        return x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE;
    }

    private static int[][] cardinalDirections() {
        return new int[][]{ {1, 0}, {-1, 0}, {0, 1}, {0, -1} };
    }

    // --- MAIN PATH GENERATION ---

    private static boolean generateMainPath(RoomType[][] grid, List<int[]> mainPath) {
        mainPath.clear();

        // Select a random starting point near the center of the grid
        int startX = RNG.nextInt(10) + (GRID_SIZE/2)-5;
        int startY = RNG.nextInt(10) + (GRID_SIZE/2)-5;

        mainPath.add(new int[]{startX, startY});
        grid[startY][startX] = RoomType.MAIN_PATH;

        int targetLength = MAIN_PATH_MIN_LENGTH + RNG.nextInt(MAIN_PATH_MAX_LENGTH - MAIN_PATH_MIN_LENGTH + 1);
        int prevDirX = 1, prevDirY = 0;

        // Random Walk Algorithm with Backtracking
        while (mainPath.size() < targetLength) {
            int[] lastRoom = mainPath.get(mainPath.size() - 1);
            int lastX = lastRoom[0], lastY = lastRoom[1];

            List<int[]> directionOptions = new ArrayList<>();

            // 70% chance to continue in the same direction (creates straighter corridors)
            if (RNG.nextInt(100) < 70)
                directionOptions.add(new int[]{prevDirX, prevDirY});

            // Add other directions (excluding 180-degree turns and the current direction already added above)
            for (int[] dir : cardinalDirections()) {
                if (dir[0] == -prevDirX && dir[1] == -prevDirY) continue;
                if (dir[0] == prevDirX && dir[1] == prevDirY) continue;
                directionOptions.add(dir);
            }

            Collections.shuffle(directionOptions, RNG);
            boolean moved = false;

            for (int[] dir : directionOptions) {
                int nextX = lastX + dir[0];
                int nextY = lastY + dir[1];

                // Boundary, occupancy, and "touching" checks
                if (!isInsideGrid(nextX, nextY)) continue;
                if (grid[nextY][nextX] != RoomType.EMPTY) continue;
                if (touchesAnyRoomExceptPrevious(grid, nextX, nextY, lastX, lastY)) continue;

                // Successful step forward
                mainPath.add(new int[]{nextX, nextY});
                grid[nextY][nextX] = RoomType.MAIN_PATH;
                prevDirX = dir[0];
                prevDirY = dir[1];
                moved = true;
                break;
            }

            // BACKTRACKING: If we cannot move anywhere, remove the last room and return to the previous one
            if (!moved) {
                int[] removed = mainPath.remove(mainPath.size() - 1);
                grid[removed[1]][removed[0]] = RoomType.EMPTY;

                // If we've returned to the start and cannot continue, main path generation has failed
                if (mainPath.size() <= 1) {
                    if (!mainPath.isEmpty()) {
                        int[] start = mainPath.remove(0);
                        grid[start[1]][start[0]] = RoomType.EMPTY;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    // --- SIDE BRANCH GENERATION ---

    private static boolean generateBranches(RoomType[][] grid, List<List<int[]>> branchList, List<int[]> mainPath) {
        branchList.clear();
        int requiredBranches = MIN_BRANCH_COUNT + RNG.nextInt(MAX_BRANCH_COUNT - MIN_BRANCH_COUNT + 1);

        // Branches can attach to any point on the main path except the final room (Boss)
        List<int[]> possibleAttachPoints = new ArrayList<>();
        for (int i = 0; i < mainPath.size() - 1; i++) {
            possibleAttachPoints.add(mainPath.get(i));
        }

        int attempts = 0;
        while (branchList.size() < requiredBranches && attempts < 200) {
            attempts++;

            // Random attachment point
            int[] attachPoint = possibleAttachPoints.get(RNG.nextInt(possibleAttachPoints.size()));
            int attachX = attachPoint[0];
            int attachY = attachPoint[1];

            // Branches must not start at the Start or Final Boss rooms
            if (grid[attachY][attachX] == RoomType.START) continue;
            if (grid[attachY][attachX] == RoomType.FINAL_ROOM) continue;

            List<int[]> validStarts = new ArrayList<>();
            // Find an available direction from the attachment point
            for (int[] dir : cardinalDirections()) {
                int startX = attachX + dir[0];
                int startY = attachY + dir[1];

                if (!isInsideGrid(startX, startY)) continue;
                if (grid[startY][startX] != RoomType.EMPTY) continue;
                if (touchesAnyRoomExceptPrevious(grid, startX, startY, attachX, attachY)) continue;

                validStarts.add(new int[]{startX, startY});
            }

            if (validStarts.isEmpty()) continue;

            // Start of the branch
            int[] branchStart = validStarts.get(RNG.nextInt(validStarts.size()));
            int branchLength = BRANCH_MIN_LENGTH + RNG.nextInt(BRANCH_MAX_LENGTH - BRANCH_MIN_LENGTH + 1);

            List<int[]> branch = new ArrayList<>();
            branch.add(branchStart);
            grid[branchStart[1]][branchStart[0]] = RoomType.BRANCH_PATH;

            int prevDirX = branchStart[0] - attachX;
            int prevDirY = branchStart[1] - attachY;

            // Branch growth (similar logic to the main path)
            while (branch.size() < branchLength) {
                int[] lastRoom = branch.get(branch.size() - 1);
                int lastX = lastRoom[0];
                int lastY = lastRoom[1];

                List<int[]> directionOptions = new ArrayList<>();
                if (RNG.nextInt(100) < 70) directionOptions.add(new int[]{prevDirX, prevDirY});

                for (int[] dir : cardinalDirections()) {
                    if (dir[0] == -prevDirX && dir[1] == -prevDirY) continue;
                    if (dir[0] == prevDirX && dir[1] == prevDirY) continue;
                    directionOptions.add(dir);
                }

                Collections.shuffle(directionOptions, RNG);
                boolean moved = false;

                for (int[] dir : directionOptions) {
                    int nextX = lastX + dir[0];
                    int nextY = lastY + dir[1];

                    if (!isInsideGrid(nextX, nextY)) continue;
                    if (grid[nextY][nextX] != RoomType.EMPTY) continue;
                    if (touchesAnyRoomExceptPrevious(grid, nextX, nextY, lastX, lastY)) continue;

                    branch.add(new int[]{nextX, nextY});
                    grid[nextY][nextX] = RoomType.BRANCH_PATH;
                    prevDirX = dir[0];
                    prevDirY = dir[1];
                    moved = true;
                    break;
                }

                if (!moved) {
                    int[] removed = branch.remove(branch.size() - 1);
                    grid[removed[1]][removed[0]] = RoomType.EMPTY;
                    if (branch.isEmpty()) break;
                }
            }

            // If the branch reached the required length, add it to the list; otherwise, clear it
            if (branch.size() == branchLength) {
                branchList.add(branch);
            } else {
                for (int[] room : branch) grid[room[1]][room[0]] = RoomType.EMPTY;
            }
        }
        return branchList.size() >= MIN_BRANCH_COUNT;
    }

    /**
     * Final assignment of types to the grid after all paths are completed.
     */
    private static void labelAllRooms(RoomType[][] grid, List<int[]> mainPath, List<List<int[]>> branchList) {
        // Label the first and last points of the main path
        int[] start = mainPath.get(0);
        grid[start[1]][start[0]] = RoomType.START;

        for (int i = 1; i < mainPath.size() - 1; i++) {
            int[] room = mainPath.get(i);
            grid[room[1]][room[0]] = RoomType.MAIN_PATH;
        }

        int[] finalRoom = mainPath.get(mainPath.size() - 1);
        grid[finalRoom[1]][finalRoom[0]] = RoomType.FINAL_ROOM;

        // Label all branches
        for (List<int[]> branch : branchList) {
            for (int[] room : branch) {
                grid[room[1]][room[0]] = RoomType.BRANCH_PATH;
            }
        }
    }
}
