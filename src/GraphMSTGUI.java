import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GraphMSTGUI extends JFrame {
    private JTextArea output;
    private JComboBox<String> algoSelector;
    private GraphPanel graphPanel;
    private List<Edge> currentMST = new ArrayList<>();

    // Graph data
    private static final String[] vertices = {"A","B","C","D","E","F","G","H"};
    private final Map<String,Integer> nodeMap = new HashMap<>();
    private final List<Edge> edges = new ArrayList<>();
    private final Map<String, Point> nodePos = new HashMap<>();

    // Edge class
    private static class Edge implements Comparable<Edge> {
        int src, dest, weight;
        Edge(int s, int d, int w){ src=s; dest=d; weight=w; }
        public int compareTo(Edge o){ return Integer.compare(weight,o.weight); }
        public String toString(){ return vertices[src] + "-" + vertices[dest] + ":" + weight; }
    }

    public GraphMSTGUI(){
        super("MST Visualizer");
        setSize(900,600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Split pane
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        graphPanel = new GraphPanel();
        graphPanel.setPreferredSize(new Dimension(500, 400));
        split.setLeftComponent(graphPanel);

        output = new JTextArea();
        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        split.setRightComponent(new JScrollPane(output));
        add(split, BorderLayout.CENTER);

        // Controls
        JPanel top = new JPanel();
        algoSelector = new JComboBox<>(new String[]{"Kruskal","Prim"});
        JButton computeBtn = new JButton("Compute MST");
        JButton showBtn = new JButton("Show Graph");

        top.add(new JLabel("Algorithm:"));
        top.add(algoSelector);
        top.add(computeBtn);
        top.add(showBtn);
        add(top, BorderLayout.NORTH);

        computeBtn.addActionListener(e -> computeMST());
        showBtn.addActionListener(e -> showGraph());

        initGraph();
    }

    private void initGraph(){
        // Initialize node mapping and positions
        for(int i=0; i<vertices.length; i++) nodeMap.put(vertices[i], i);
        nodePos.put("A", new Point(250, 50));  nodePos.put("B", new Point(120, 120));
        nodePos.put("C", new Point(40, 220));  nodePos.put("D", new Point(160, 280));
        nodePos.put("E", new Point(250, 260)); nodePos.put("F", new Point(200, 160));
        nodePos.put("G", new Point(320, 220)); nodePos.put("H", new Point(360, 120));

        // Add edges from original graph
        addEdge("A","B",14); addEdge("A","F",21); addEdge("A","H",8);
        addEdge("B","C",15); addEdge("B","F",13); addEdge("B","H",26);
        addEdge("C","D",12); addEdge("D","E",10); addEdge("D","F",14);
        addEdge("E","F",12); addEdge("E","G",14); addEdge("F","G",10);
        addEdge("G","H",7);  addEdge("C","E",33);
    }

    private void addEdge(String u,String v,int w){
        edges.add(new Edge(nodeMap.get(u), nodeMap.get(v), w));
    }

    private void computeMST(){
        output.setText("");
        if("Kruskal".equals(algoSelector.getSelectedItem())) runKruskal();
        else runPrim();
        graphPanel.repaint();
    }

    private void showGraph(){
        currentMST.clear();
        output.setText("Graph Edges:\n");
        for(Edge e : edges) output.append(e + "\n");
        graphPanel.repaint();
    }

    // Kruskal's Algorithm
    private void runKruskal(){
        List<Edge> sorted = new ArrayList<>(edges);
        Collections.sort(sorted);
        int[] parent = new int[vertices.length];
        for(int i=0; i<vertices.length; i++) parent[i]=i;

        output.append("KRUSKAL'S MST:\n");
        currentMST.clear();
        int total = 0;

        for(Edge e : sorted){
            int r1 = find(parent,e.src), r2 = find(parent,e.dest);
            if(r1!=r2){
                parent[r1]=r2;
                currentMST.add(e);
                output.append(e + "\n");
                total += e.weight;
                if(currentMST.size()==vertices.length-1) break;
            }
        }
        output.append("Total: " + total + "\n");
    }

    private int find(int[] parent,int x){
        return parent[x]==x ? x : (parent[x] = find(parent,parent[x]));
    }

    // Prim's Algorithm
    private void runPrim(){
        boolean[] inMST = new boolean[vertices.length];
        PriorityQueue<Edge> pq = new PriorityQueue<>();
        currentMST.clear();

        output.append("PRIM'S MST (from A):\n");
        inMST[0] = true;

        for(Edge e : edges)
            if(e.src==0 || e.dest==0) pq.add(e);

        int total = 0;
        while(!pq.isEmpty() && currentMST.size()<vertices.length-1){
            Edge e = pq.poll();
            if(inMST[e.src] && inMST[e.dest]) continue;

            currentMST.add(e);
            output.append(e + "\n");
            total += e.weight;

            int newNode = inMST[e.src] ? e.dest : e.src;
            inMST[newNode] = true;

            for(Edge edge : edges)
                if((edge.src==newNode && !inMST[edge.dest]) ||
                        (edge.dest==newNode && !inMST[edge.src])) pq.add(edge);
        }
        output.append("Total: " + total + "\n");
    }

    // Graph Panel
    private class GraphPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw all edges
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setStroke(new BasicStroke(2));
            for(Edge e : edges) {
                Point p1 = nodePos.get(vertices[e.src]);
                Point p2 = nodePos.get(vertices[e.dest]);
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);

                // Weight label
                int mx = (p1.x + p2.x) / 2, my = (p1.y + p2.y) / 2;
                g2d.setColor(Color.BLACK);
                g2d.drawString("" + e.weight, mx-5, my-5);
                g2d.setColor(Color.LIGHT_GRAY);
            }

            // Draw MST edges
            if(!currentMST.isEmpty()) {
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(4));
                for(Edge e : currentMST) {
                    Point p1 = nodePos.get(vertices[e.src]);
                    Point p2 = nodePos.get(vertices[e.dest]);
                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }

            // Draw nodes
            g2d.setStroke(new BasicStroke(2));
            for(String v : vertices) {
                Point p = nodePos.get(v);
                g2d.setColor(Color.CYAN);
                g2d.fillOval(p.x-15, p.y-15, 30, 30);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(p.x-15, p.y-15, 30, 30);
                g2d.drawString(v, p.x-4, p.y+4);
            }
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new GraphMSTGUI().setVisible(true));
    }
}