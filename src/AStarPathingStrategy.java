import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.HashMap;

class AStarPathingStrategy
        implements PathingStrategy {

    public class ListNode {
        public Point point;
        public double g;
        public double h;
        public ListNode parent;

        public ListNode(Point point, double g, double h, ListNode parent) {
            this.point = point;
            this.g = g;
            this.h = h;
            this.parent = parent;
        }
    }

    public List<Point> computePath(Point start, Point end, Predicate<Point> canPassThrough, BiPredicate<Point, Point> withinReach, Function<Point, Stream<Point>> potentialNeighbors) {
        ListNode found = null; // not sure why i nneded this boolean flag?
        List<ListNode> openList = new LinkedList<>(); // init opan and closed list
        List<ListNode> closedList = new LinkedList<>();
        LinkedList<Point> path = new LinkedList<>(); // init path to be returned

        openList.add(new ListNode(start, 0, start.manhattanDistance(end), null)); // add start to open list

        while (openList.size() != 0) { // go through until found // add can no longer search
            int i = quickFind(openList); // find the smalled F on the open-list
            ListNode curr = openList.get(i); // this is the current node (smalled f val node)
//            System.out.println("new curr: " + curr.point);
//            System.out.println("///////////////////////////////////////////////////////////////////////////////////////////");
//            System.out.println("openlist: " + openList.size());
//            System.out.println("///////////////////////////////////////////////////////////////////////////////////////////");
            if (withinReach.test(curr.point, end)) {
//                System.out.println("found its goal");
                found = curr;
                break;
            }
            potentialNeighbors.apply(curr.point)
                    .forEach(neighbor -> {
//                        System.out.println("neighbor: " + neighbor);
                        if (!(canPassThrough.test(neighbor) && notOnClosedList(neighbor, closedList))) { // check valid
//                            System.out.println("invalid neighbor:");
                            return; // TODO might break
                        }
                        double newG = curr.g + curr.point.distance(neighbor);
                        ListNode repeat = onOpenList(neighbor, openList); // if the neighbor is already on the openList
                        if (repeat != null) { // if the neighbor is on open list
                            if (newG <= repeat.g) { // if the new g value for the neighbor is beter than the one on open list
                                repeat.g = newG; // make the smaller G value the new G value
                                openList.remove(repeat); // get rid of it as new will create  anew node to replace it
                            } else {// otherwise go to next neighbor
                                return; //TODO might break
                            }
                        }

                        double newH = end.manhattanDistance(neighbor);
                        double newF = newH + newG;
                        ListNode update = new ListNode(neighbor, newG, newH, curr);

                        openList.add(update);
                    });

            openList.remove(curr);
            closedList.add(curr);
        }

        if (found != null){
            while (found != null) {
                path.add(0,found.point);
                found = found.parent;
            }
            path.remove(0);
        }
        return path;
    }


    public boolean notOnClosedList(Point p, List<ListNode> closdList){
        for (ListNode n: closdList){
            if (n.point.equals(p)){
                return false;
            }
        }
        return true;
    }
    public ListNode onOpenList(Point p, List<ListNode> openList){
        for (ListNode n: openList){
            if (n.point.equals(p)){
                return  n;
            }
        }
        return null;
    }
    public int quickFind(List<ListNode> inList) {
        int outIndex = 0;
        double smallest = 100000000; // arbitrary large value
        for (int i = 0; i < inList.size(); i++){
            double comp = inList.get(i).g + inList.get(i).h;
            if (comp < smallest){
                smallest = comp;
                outIndex = i;
            }
        }
        return outIndex;
    }
    public boolean searchSamePosLowF(List<ListNode> list, ListNode comp){
        double sucF = comp.g + comp.h;
        Point sucP = comp.point;
        for (ListNode n: list){
            if (n.point.equals(sucP) && ((n.h + n.g) < sucF)){
                return true;
            }
        }
        return false;
    }
}
