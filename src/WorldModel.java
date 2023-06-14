import processing.core.PImage;

import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel {
    private int numRows;
    private int numCols;
    private Background[][] background;
    private Entity[][] occupancy;
    private Set<Entity> entities;

    public void parseEntity(String line, ImageStore imageStore) {
        String[] properties = line.split(" ", 4 + 1);
        if (properties.length >= 4) {
            String key = properties[0];
            String id = properties[1];
            Point pt = new Point(Integer.parseInt(properties[2]), Integer.parseInt(properties[3]));

            properties = properties.length == 4 ?
                    new String[0] : properties[4].split(" ");

            switch (key) {
                case "obstacle" -> parseObstacle(properties, pt, id, imageStore);
                case "dude" -> parseDude(properties, pt, id, imageStore);
                case "fairy" -> parseFairy(properties, pt, id, imageStore);
                case "house" -> parseHouse(properties, pt, id, imageStore);
                case "tree" -> parseTree(properties, pt, id, imageStore);
                case "sapling" -> parseSapling(properties, pt, id, imageStore);
                case "stump" -> parseStump(properties, pt, id, imageStore);
                default -> throw new IllegalArgumentException("Entity key is unknown");
            }
        }else{
            throw new IllegalArgumentException("Entity must be formatted as [key] [id] [x] [y] ...");
        }
    }

    public void parseSaveFile(Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        String lastHeader = "";
        int headerLine = 0;
        int lineCounter = 0;
        while(saveFile.hasNextLine()){
            lineCounter++;
            String line = saveFile.nextLine().strip();
            if(line.endsWith(":")){
                headerLine = lineCounter;
                lastHeader = line;
                switch (line){
                    case "Backgrounds:" -> background = ( new Background[numRows][numCols]);
                    case "Entities:" -> {
                        occupancy = (new Entity[numRows][numCols]);
                        entities = ( new HashSet<>());
                    }
                }
            }else{
                switch (lastHeader){
                    case "Rows:" -> numRows = (Integer.parseInt(line));
                    case "Cols:" -> numCols = (Integer.parseInt(line));
                    case "Backgrounds:" -> parseBackgroundRow(line, lineCounter-headerLine-1, imageStore);
                    case "Entities:" -> this.parseEntity(line, imageStore);
                }
            }
        }
    }

    public void load(Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        this.parseSaveFile(saveFile, imageStore, defaultBackground);
        if(background == null){
            background = (new Background[numRows][numCols]);
            for (Background[] row : background)
                Arrays.fill(row, defaultBackground);
        }
        if(occupancy == null){
            occupancy = ( new Entity[numRows][numCols]);
            entities = (new HashSet<>());
        }
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public Set<Entity> getEntities() {
        return entities;
    }

    public WorldModel() {

    }

    /*
           Assumes that there is no entity currently occupying the
           intended destination cell.
        */
    public void addEntity(Entity entity) {
        if (withinBounds(entity.getPosition())) {
            setOccupancyCell(entity.getPosition(), entity);
            this.entities.add(entity);
        }
    }

    public void setOccupancyCell(Point pos, Entity entity) {
        this.occupancy[pos.getY()][pos.getX()] = entity;
    }

    public void removeEntityAt(Point pos) {
        if (withinBounds(pos) && getOccupancyCell(pos) != null) {
            Entity entity = getOccupancyCell(pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.setPosition(new Point(-1, -1));
            this.entities.remove(entity);
            this.setOccupancyCell(pos, null);
        }
    }

    public void removeEntity(EventScheduler scheduler, Entity entity) {
        scheduler.unscheduleAllEvents(entity);
        this.removeEntityAt(entity.getPosition());
    }

    public Entity getOccupancyCell(Point pos) {
        return this.occupancy[pos.getY()][pos.getX()];
    }

    public boolean withinBounds(Point pos) {
        return pos.getY() >= 0 && pos.getY() < this.numRows && pos.getX() >= 0 && pos.getX() < this.numCols;
    }

    public Optional<Entity> findNearest(Point pos, List<Class> kinds) {
        List<Entity> ofType = new LinkedList<>();
        for (Class kind : kinds) {
            for (Entity entity : this.entities) {
                if (entity.getClass() == kind) {
                    ofType.add(entity);
                }
            }
        }

        return pos.nearestEntity(ofType);
    }

    public void moveEntity(EventScheduler scheduler, Entity entity, Point pos) {
        Point oldPos = entity.getPosition();
        if (this.withinBounds(pos) && !pos.equals(oldPos)) {
            this.setOccupancyCell(oldPos, null);
            Optional<Entity> occupant = getOccupant(pos);
            occupant.ifPresent(target -> this.removeEntity(scheduler, target));
            this.setOccupancyCell(pos, entity);
            entity.setPosition(pos);
        }
    }

    public Optional<Entity> getOccupant(Point pos) {
        if (isOccupied(pos)) {
            return Optional.of(this.getOccupancyCell(pos));
        } else {
            return Optional.empty();
        }
    }

    public Background getBackgroundCell(Point pos) {
        return this.background[pos.getY()][pos.getX()];
    }

    public void setBackgroundCell(Point pos, Background background) {
        this.background[pos.getY()][pos.getX()] = background;
    }

    public Optional<PImage> getBackgroundImage(Point pos) {
        if (this.withinBounds(pos)) {
            return Optional.of(this.getBackgroundCell(pos).getCurrentImage());
        } else {
            return Optional.empty();
        }
    }

    public void parseStump(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == 0) {
            Entity entity = new Stump(id, pt, imageStore.getImageList("stump"));
            entity.tryAddEntity(this);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", "stump", 0));
        }
    }

    public void parseHouse(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == 0) {
            Entity entity = new House(id, pt, imageStore.getImageList("house"));
            entity.tryAddEntity(this);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", "house", 0));
        }
    }

    public void parseObstacle(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == 1) {
            Entity entity = new Obstacle(id, pt, imageStore.getImageList("obstacle"), Double.parseDouble(properties[0]));
            entity.tryAddEntity(this);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", "obstacle", 1));
        }
    }

    public void parseTree(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == 3) {
            Entity entity = new Tree(id, pt, imageStore.getImageList("tree"), Functions.getNumFromRange(0.600, 0.050), Functions.getNumFromRange(1.400, 1.000),  Functions.getIntFromRange(3, 1));
            entity.tryAddEntity(this);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", "tree", 3));
        }
    }

    public void parseFairy(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == 2) {
            Entity entity = new Fairy(id, pt, imageStore.getImageList("fairy"),  Double.parseDouble(properties[0]), Double.parseDouble(properties[1]));
            entity.tryAddEntity(this);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", "fairy", 2));
        }
    }

    public void parseDude(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == 3) {
            Entity entity = new DudeNotFull(id, pt,imageStore.getImageList("dude"), Double.parseDouble(properties[1]), Double.parseDouble(properties[0]), Integer.parseInt(properties[2]),0);
            entity.tryAddEntity(this);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", "dude", 3));
        }
    }

    public void parseSapling(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == 1) {
            int health = Integer.parseInt(properties[0]);
            Entity entity = new Sapling(id, pt, imageStore.getImageList("sapling"),1.000, 1.000, health, 5);
            entity.tryAddEntity(this);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", "sapling", 1));
        }
    }

    public void parseBackgroundRow(String line, int row, ImageStore imageStore) {
        String[] cells = line.split(" ");
        if(row < this.numRows){
            int rows = Math.min(cells.length, this.numCols);
            for (int col = 0; col < rows; col++){
                this.background[row][col] = new Background(cells[col], imageStore.getImageList(cells[col]));
            }
        }
    }

    public boolean isOccupied(Point pos) {
        return this.withinBounds(pos) && this.getOccupancyCell(pos) != null;
    }

    /**
     * Helper method for testing. Don't move or modify this method.
     */
    public List<String> log(){
        List<String> list = new ArrayList<>();
        for (Entity entity : entities) {
            String log = entity.log();
            if(log != null) list.add(log);
        }
        return list;
    }

}
