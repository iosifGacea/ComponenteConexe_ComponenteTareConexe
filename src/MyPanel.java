import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.*;
import javax.swing.*;

public class MyPanel extends JPanel {
    private int nodeNr = 1;
    private int node_diam = 30;
    private final Vector<Node> listaNoduri = new Vector<>();
    private final Vector<Arc> listaArce = new Vector<>();
    public Vector<Color> listaCuloriNoduri = new Vector<>();
    public Vector<Color> listaCuloriArce = new Vector<>();
    Point pointStart = null;
    Point pointEnd = null;
    public Vector<Vector<Integer>> listaDeAdiacenta = new Vector<>();
    public Queue<Integer> coada = new LinkedList<>();
    boolean isDragging = false;
    Random rand = new Random();
    Random randColor = new Random();
    boolean spatiuValabil = true;
    int numarNoduri = 0;
    float probabilitate = 0;
    Stack<Integer> stiva = new Stack<>();
    int id = 0;

    public Color generateRandColor() {
        float R = randColor.nextFloat();
        float G = randColor.nextFloat();
        float B = randColor.nextFloat();
        return new Color(R, G, B);
    }

    public Color batch;

    public void drawRandomNode() {
        boolean isClose;
        boolean stop = false;
        int count = 0;
        while (!stop && count < 500) {
            isClose = false;
            Point newNode = new Point(rand.nextInt(1850) + 10, rand.nextInt(940) + 10);
            if (listaNoduri.size() > 0) {
                for (Node it : listaNoduri) {
                    if (it.getDistance(newNode) <= node_diam) {
                        isClose = true;
                        count++;
                    }
                }
                if (!isClose) {
                    addNode(newNode.x, newNode.y);
                    stop = true;
                    Vector<Integer> row = new Vector<>();
                    listaDeAdiacenta.add(row);
                }
            } else {
                Vector<Integer> row = new Vector<>();
                listaDeAdiacenta.add(row);
                addNode(newNode.x, newNode.y);
                stop = true;
            }
        }
        if (count == 500) {
            spatiuValabil = false;
        }
    }

    public void componenteConexe() {
        boolean stop = false;
        while (!stop) {
            stop = true;
            batch = generateRandColor();
            for (int i = 0; i < listaNoduri.size() && stop; i++) {
                if (!listaNoduri.elementAt(i).isVisited) {
                    stop = false;
                    coada.add(i);
                    listaCuloriNoduri.set(i, batch);
                    listaNoduri.elementAt(i).isVisited = true;
                }
            }
            while (!coada.isEmpty()) {
                for (int i = 0; i < listaDeAdiacenta.elementAt(coada.element()).size(); i++) {
                    if (!listaNoduri.elementAt(listaDeAdiacenta.elementAt(coada.element()).elementAt(i)).isVisited) {
                        coada.add(listaDeAdiacenta.elementAt(coada.element()).elementAt(i));
                        listaCuloriNoduri.set(listaDeAdiacenta.elementAt(coada.element()).elementAt(i), batch);
                        listaNoduri.elementAt(listaDeAdiacenta.elementAt(coada.element()).elementAt(i)).isVisited = true;
                    }
                }
                coada.remove();
            }
        }
    }

    void buttonX() {
        componenteConexe();
        coloreazaArce();
        repaint();
        for (Node it : listaNoduri) {
            it.isVisited = false;
        }
    }

    void buttonHX() {
        for (Node it : listaNoduri) {
            it.isVisited = false;
        }
        componenteTariConexe();
        coloreazaNoduriOrientate();
        coloreazaArce();
        repaint();
        System.out.println();
        for (Node it : listaNoduri) {
            System.out.println(it.low);
        }
        System.out.println();
        for (Node it : listaNoduri) {
            System.out.println(it.id);
        }
    }

    void coloreazaArce() {
        if (ButtonFrame.isRandom) {
            for (Arc ignored : listaArce) {
                listaCuloriArce.add(Color.RED);
            }
        }
        boolean stop;
        for (int i = 0; i < listaArce.size(); i++) {
            stop = false;
            for (int j = 0; j < listaNoduri.size() && !stop; j++) {
                if (listaArce.elementAt(i).start.x - node_diam / 2 == listaNoduri.elementAt(j).getCoordX() && listaArce.elementAt(i).start.y - node_diam / 2 == listaNoduri.elementAt(j).getCoordY()) {
                    listaCuloriArce.set(i, listaCuloriNoduri.elementAt(j));
                    stop = true;
                }
            }
        }
    }

    void coloreazaNoduriOrientate() {
        for (Node it : listaNoduri) {
            it.isVisited = false;
            it.isOnStack = false;
        }
        for (int i = 0; i < listaNoduri.size(); i++) {
            if (!listaNoduri.elementAt(i).isVisited) {
                listaNoduri.elementAt(i).isVisited = true;
                batch = generateRandColor();
                listaCuloriNoduri.set(i, batch);
                for (int j = 0; j < listaNoduri.size(); j++) {
                    if (!listaNoduri.elementAt(j).isVisited && listaNoduri.elementAt(j).low == listaNoduri.elementAt(i).low) {
                        listaNoduri.elementAt(j).isVisited = true;
                        listaCuloriNoduri.set(j, batch);
                    }
                }
            }
        }
    }

    public void dfs(int at) {
        stiva.add(at);
        listaNoduri.elementAt(at).isOnStack = true;
        listaNoduri.elementAt(at).isVisited = true;
        listaNoduri.elementAt(at).id = id;
        listaNoduri.elementAt(at).low = id++;
        if (listaDeAdiacenta.elementAt(at).size() == 0) {
            listaNoduri.elementAt(stiva.peek()).isOnStack = false;
            stiva.pop();
        } else {
            for (Integer to : listaDeAdiacenta.elementAt(at)) {
                if (!listaNoduri.elementAt(to).isVisited) {
                    dfs(to);
                }
                if (listaNoduri.elementAt(to).isOnStack) {
                    listaNoduri.elementAt(at).low = Math.min(listaNoduri.elementAt(at).low, listaNoduri.elementAt(to).low);
                }
                if (listaNoduri.elementAt(at).isOnStack) {
                    if (listaNoduri.elementAt(at).id == listaNoduri.elementAt(at).low) {
                        if (stiva.peek() == at) {
                            listaNoduri.elementAt(stiva.peek()).isOnStack = false;
                            stiva.pop();
                        } else {
                            while (true) {
                                listaNoduri.elementAt(stiva.peek()).isOnStack = false;
                                listaNoduri.elementAt(stiva.peek()).low = listaNoduri.elementAt(at).id;
                                stiva.pop();
                                if (stiva.isEmpty()) {
                                    break;
                                }
                                if (stiva.peek() == at) {
                                    listaNoduri.elementAt(stiva.peek()).isOnStack = false;
                                    stiva.pop();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void componenteTariConexe() {
        id = 0;
        stiva.clear();
        for (Node it : listaNoduri) {
            it.isVisited = false;
            it.low = -1;
        }
        for (int i = 0; i < listaNoduri.size(); i++) {
            if (!listaNoduri.elementAt(i).isVisited) {
                dfs(i);
            }
        }

    }

    public MyPanel() {
        //citireListaDeAdiacenta("Input.txt");
        // borderul panel-ului
        if (Objects.equals(ButtonFrame.type, "Neorientat") && !ButtonFrame.isRandom) {
            JButton cc = new JButton("Componente conexe");
            cc.setBounds(75, 100, 100, 50);
            cc.addActionListener(e -> buttonX());
            add(cc);
        }
        if (Objects.equals(ButtonFrame.type, "Orientat") && !ButtonFrame.isRandom) {
            JButton ctc = new JButton("Componente tari conexe");
            ctc.setBounds(705, 100, 100, 50);
            ctc.addActionListener(e -> buttonHX());
            add(ctc);
        }

        setBorder(BorderFactory.createLineBorder(Color.black));
        if (!ButtonFrame.isRandom) {
            addMouseListener(new MouseAdapter() {
                // evenimentul care se produce la apasarea mouse-ului
                public void mousePressed(MouseEvent e) {
                    pointStart = e.getPoint();
                }

                // evenimentul care se produce la eliberarea mouse-ului
                public void mouseReleased(MouseEvent e) {
                    if (!isDragging) {// adaugare nod, fara sa se suprapuna
                        boolean isClose = false;
                        Point newNodeCenter = e.getPoint();
                        newNodeCenter.x = newNodeCenter.x - node_diam / 2;
                        newNodeCenter.y = newNodeCenter.y - node_diam / 2;
                        if (listaNoduri.size() > 0) {
                            for (Node it : listaNoduri) {
                                if (it.getDistance(newNodeCenter) <= node_diam) {
                                    isClose = true;
                                }
                            }
                            if (!isClose) {
                                listaDeAdiacenta.add(new Vector<>());
                                addNode(e.getX() - node_diam / 2, e.getY() - node_diam / 2);
                            }
                        } else {
                            listaDeAdiacenta.add(new Vector<>());
                            addNode(e.getX() - node_diam / 2, e.getY() - node_diam / 2);
                        }
                    } // adaugare arce care pleaca din noduri sau ajung in noduri
                    else {
                        boolean pointStartIsOnNode = false;
                        int pointStartNodeNumber = 0;

                        boolean pointEndIsOnNode = false;
                        int pointEndNodeNumber = 0;

                        // pointStart
                        for (int it = 0; it < listaNoduri.size() && !pointStartIsOnNode; it++) {
                            if (listaNoduri.get(it).getDistance(pointStart) < node_diam) {
                                pointStartIsOnNode = true;
                                pointStartNodeNumber = listaNoduri.get(it).getNumber();

                            }
                        }

                        // pointEnd
                        for (int it = 0; it < listaNoduri.size() && !pointEndIsOnNode; it++) {
                            if (listaNoduri.get(it).getDistance(pointEnd) < node_diam) {
                                pointEndIsOnNode = true;
                                pointEndNodeNumber = listaNoduri.get(it).getNumber();
                            }
                        }

                        if (pointStartIsOnNode && pointEndIsOnNode && pointStartNodeNumber != pointEndNodeNumber) {
                            // aici updatam graful de adiacenta
                            if (Objects.equals(Graf.type, "Componente conexe")) {
                                boolean gasit = false;
                                for (Integer it : listaDeAdiacenta.elementAt(pointStartNodeNumber - 1)) {
                                    if (it == pointEndNodeNumber - 1) {
                                        gasit = true;
                                        break;
                                    }
                                }
                                if (!gasit) {
                                    listaDeAdiacenta.elementAt(pointStartNodeNumber - 1).add(pointEndNodeNumber - 1);
                                }
                                for (Integer it : listaDeAdiacenta.elementAt(pointEndNodeNumber - 1)) {
                                    if (it == pointStartNodeNumber - 1) {
                                        gasit = true;
                                        break;
                                    }
                                }
                                if (!gasit) {
                                    listaDeAdiacenta.elementAt(pointEndNodeNumber - 1).add(pointStartNodeNumber - 1);
                                    Arc aux = new Arc(new Point(listaNoduri.elementAt(pointStartNodeNumber - 1).getCoordX() + node_diam / 2, listaNoduri.elementAt(pointStartNodeNumber - 1).getCoordY() + node_diam / 2), new Point(listaNoduri.elementAt(pointEndNodeNumber - 1).getCoordX() + node_diam / 2, listaNoduri.elementAt(pointEndNodeNumber - 1).getCoordY() + node_diam / 2));
                                    listaArce.add(aux);
                                    listaCuloriArce.add(Color.red);
                                }
                            }
                            if (Objects.equals(Graf.type, "Componente tari conexe")) {
                                boolean gasit = false;
                                for (Integer it : listaDeAdiacenta.elementAt(pointStartNodeNumber - 1)) {
                                    if (it == pointEndNodeNumber - 1) {
                                        gasit = true;
                                        break;
                                    }
                                }
                                if (!gasit) {
                                    listaDeAdiacenta.elementAt(pointStartNodeNumber - 1).add(pointEndNodeNumber - 1);
                                    Arc aux = new Arc(new Point(listaNoduri.elementAt(pointStartNodeNumber - 1).getCoordX() + node_diam / 2, listaNoduri.elementAt(pointStartNodeNumber - 1).getCoordY() + node_diam / 2), new Point(listaNoduri.elementAt(pointEndNodeNumber - 1).getCoordX() + node_diam / 2, listaNoduri.elementAt(pointEndNodeNumber - 1).getCoordY() + node_diam / 2));
                                    listaArce.add(aux);
                                    listaCuloriArce.add(Color.red);
                                }
                            }
                        }
                    }
                    pointStart = null;
                    isDragging = false;
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                // evenimentul care se produce la drag&drop pe mousse
                public void mouseDragged(MouseEvent e) {
                    pointEnd = e.getPoint();
                    isDragging = true;
                    repaint();
                }
            });
        } else {
            node_diam = 20;
            Graf.width = 1900;
            numarNoduri = Integer.parseInt(JOptionPane.showInputDialog("Introduceti numarul de noduri!"));
            probabilitate = Float.parseFloat(JOptionPane.showInputDialog("Introduceti probabilitatea p! (0-100)"));

            for (int i = 0; i < numarNoduri && spatiuValabil; i++) {
                drawRandomNode();
                if (!spatiuValabil) {
                    JOptionPane.showMessageDialog(this, "S-au putut afisa " + nodeNr + " noduri!");
                } else {
                    for (int j = 0; j < listaNoduri.size() - 1; j++) {
                        if (rand.nextFloat() <= probabilitate / 100) {
                            Point nodCurent = new Point();
                            nodCurent.x = listaNoduri.elementAt(j).getCoordX() + node_diam / 2;
                            nodCurent.y = listaNoduri.elementAt(j).getCoordY() + node_diam / 2;
                            Point nodStocat = new Point();
                            nodStocat.x = listaNoduri.elementAt(listaNoduri.size() - 1).getCoordX() + node_diam / 2;
                            nodStocat.y = listaNoduri.elementAt(listaNoduri.size() - 1).getCoordY() + node_diam / 2;
                            Arc arc = new Arc(nodCurent, nodStocat);
                            if (ButtonFrame.isRandom && Objects.equals(ButtonFrame.type, "Neorientat")) {
                                listaArce.add(arc);
                                listaCuloriArce.add(Color.red);
                                listaDeAdiacenta.elementAt(j).add(listaNoduri.size() - 1);
                                listaDeAdiacenta.elementAt(listaNoduri.size() - 1).add(j);
                            } else {
                                listaArce.add(arc);
                                listaCuloriArce.add(Color.red);
                                listaDeAdiacenta.elementAt(j).add(listaNoduri.size() - 1);
                                if (rand.nextFloat() <= probabilitate / 100) {
                                    listaDeAdiacenta.elementAt(listaNoduri.size() - 1).add(j);
                                    listaArce.add(new Arc(nodStocat, nodCurent));
                                    listaCuloriArce.add(Color.red);
                                }
                            }
                        }
                    }
                }
            }
            if (Objects.equals(ButtonFrame.type, "Neorientat")) {
                componenteConexe();
                coloreazaArce();
            }
            if (Objects.equals(ButtonFrame.type, "Orientat")) {
                componenteTariConexe();
                coloreazaNoduriOrientate();
                coloreazaArce();
//                for (int i = 0; i < listaDeAdiacenta.size(); i++) {
//                    System.out.print(i + 1);
//                    System.out.print(" ");
//                    for (int j = 0; j < listaDeAdiacenta.elementAt(i).size(); j++) {
//                        System.out.print(listaDeAdiacenta.elementAt(i).elementAt(j) + 1);
//                        System.out.print(" ");
//                    }
//                    System.out.println();
//                }
//                System.out.println();
//                for (Node it : listaNoduri) {
//                    System.out.println(it.low);
//                }
            }
        }

    }


    private void addNode(int x, int y) {
        Node node = new Node(x, y, this.nodeNr);
        listaCuloriNoduri.add(Color.red);
        this.listaNoduri.add(node);
        ++this.nodeNr;
        this.repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);// apelez metoda paintComponent din clasa de baza
        // deseneaza arcele existente in lista
        for (int i = 0; i < listaArce.size(); i++) {
            listaArce.elementAt(i).drawArc(g, listaCuloriArce.elementAt(i));
        }
        // deseneaza arcul curent; cel care e in curs de desenare
        if (pointStart != null && !ButtonFrame.isRandom) {
            g.setColor(Color.RED);
            g.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y);
            if (Objects.equals(Graf.type, "Componente tari conexe")) {
                double angle = Math.atan2(pointEnd.y - pointStart.y, pointEnd.x - pointStart.x);
                int arrowHeight = 15;
                int halfArrowWidth = 7;
                Point aroBase = new Point(0, 0);
                aroBase.x = (int) (pointEnd.x - arrowHeight * Math.cos(angle));
                aroBase.y = (int) (pointEnd.y - arrowHeight * Math.sin(angle));
                Point varf1 = new Point(0, 0), varf2 = new Point(0, 0);
                varf1.x = (int) (aroBase.x - halfArrowWidth * Math.cos(angle - Math.PI / 2));
                varf1.y = (int) (aroBase.y - halfArrowWidth * Math.sin(angle - Math.PI / 2));
                varf2.x = (int) (aroBase.x + halfArrowWidth * Math.cos(angle - Math.PI / 2));
                varf2.y = (int) (aroBase.y + halfArrowWidth * Math.sin(angle - Math.PI / 2));
                int[] x = {pointEnd.x, varf1.x, varf2.x};
                int[] y = {pointEnd.y, varf1.y, varf2.y};
                int npoints = x.length;// or y.length
                g.drawPolygon(x, y, npoints);// draws polygon outline
                g.fillPolygon(x, y, npoints);// paints a polygon
            }
        }
        // deseneaza lista de noduri
        for (int i = 0; i < listaNoduri.size(); i++) {
            listaNoduri.elementAt(i).drawNode(g, node_diam, listaCuloriNoduri.elementAt(i));
        }
//        for (int i = 0; i < listaDeAdiacenta.size(); i++) {
//            System.out.print(i+1);
//            System.out.print(" ");
//            for (int j = 0; j < listaDeAdiacenta.elementAt(i).size(); j++) {
//                System.out.print(listaDeAdiacenta.elementAt(i).elementAt(j)+1);
//                System.out.print(' ');
//            }
//            System.out.println();
//        }
//        System.out.println();
//        if (ButtonFrame.type == "Neorientat") {
//            componenteConexe();
//        }
    }
}
