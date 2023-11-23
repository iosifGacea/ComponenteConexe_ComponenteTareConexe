import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonFrame extends JFrame {
    static public String type="";
    static public boolean isRandom=false;
    JButton orientat, neorientat, randomOrientat, randomNeorientat;
    void orientat00(){
        type="Orientat";
        this.dispose();
        new Graf();
    }
    void orientatRandom(){
        type="Orientat";
        this.dispose();
        isRandom=true;
        new Graf();
    }
    void neorientat(){
        type="Neorientat";
        this.dispose();
        new Graf();
    }
    void neorientatRandom(){
        type="Neorientat";
        this.dispose();
        isRandom=true;
        new Graf();
    }
    public ButtonFrame() {
        orientat=new JButton("Orientat");
        orientat.setBounds(75,100,100,50);
        orientat.addActionListener(e->orientat00());

        neorientat=new JButton("Neorientat");
        neorientat.setBounds(325,100,100,50);
        neorientat.addActionListener(e->neorientat());

        randomOrientat=new JButton("Random orientat");
        randomOrientat.setBounds(50,200,150,50);
        randomOrientat.addActionListener(e->orientatRandom());

        randomNeorientat=new JButton("Random neorientat");
        randomNeorientat.setBounds(300,200,150,50);
        randomNeorientat.addActionListener(e->neorientatRandom());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setSize(500,500);
        setVisible(true);
        add(orientat);
        add(neorientat);
        add(randomOrientat);
        add(randomNeorientat);
    }
}
