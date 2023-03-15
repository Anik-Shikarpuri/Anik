
package java2ddrawingapplication;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;



/**
 *
 * @author anik
 */
public class DrawingApplicationFrame extends JFrame implements ActionListener
{
   
    private JCheckBox filled = new JCheckBox("Filled");
    private JCheckBox gradient = new JCheckBox("Use Gradient");
    private JCheckBox line = new JCheckBox("Dashed");
    private JLabel lineWidth = new JLabel("Line Width:");
    private JSpinner width = new JSpinner(new SpinnerNumberModel(5, 1, 99, 1));
    private JLabel dashLength = new JLabel("Dash Length:");
    private JSpinner dash = new JSpinner(new SpinnerNumberModel(5, 1, 99, 1));
    private ArrayList<MyShapes> shapesList = new ArrayList<MyShapes>();
    private DrawPanel drawPanel = new DrawPanel();
    private Color first, second = Color.black;
    private JLabel statusLabel = new JLabel("(,)");
    private JPanel line1 = new JPanel();
    private JPanel line2 = new JPanel();
    private JPanel topPanel = new JPanel(new GridLayout(2, 1));
    private JLabel shape = new JLabel("Shape:");
    private JComboBox shapes = new JComboBox(new String[] {"Line", "Oval", "Rectangle"});
    private JButton color1 = new JButton("1st Color...");
    private JButton color2 = new JButton("2nd Color...");
    private JButton undo = new JButton("Undo");
    private JButton clear = new JButton("Clear");
    private JLabel option = new JLabel("Options:");
  
    
    
    public DrawingApplicationFrame()
    {
        super("Java 2D Drawing");
        setLayout(new BorderLayout());
        
        
        line1.add(shape);
        line1.add(shapes);
        line1.add(color1);
        line1.add(color2);
        line1.add(undo);
        line1.add(clear);
        
        line2.add(option);
        line2.add(filled);
        line2.add(gradient);
        line2.add(line);
        line2.add(lineWidth);
        line2.add(width);
        line2.add(dashLength);
        line2.add(dash);
        topPanel.add(line1);
        topPanel.add(line2);
        add(topPanel, BorderLayout.NORTH);
        add(drawPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        statusLabel.setBackground((Color.GRAY));

        line1.setBackground(Color.cyan);
        line2.setBackground(Color.cyan);
      
       
        
        undo.addActionListener(this);
        clear.addActionListener(this);
        color1.addActionListener(this);
        color2.addActionListener(this);
       
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    
    public void actionPerformed(ActionEvent event){
        if(event.getSource() == color1){
            first = JColorChooser.showDialog(this, "Select a Color", first);
        }
        
        else if(event.getSource() == color2){
            second = JColorChooser.showDialog(this, "Select a Color", second);
        }
        
        else if(event.getSource() == undo && shapesList.size() != 0){
            shapesList.remove(shapesList.size() - 1);
            drawPanel.repaint();
        }
        
        else if(event.getSource() == clear){
            shapesList.clear();
            drawPanel.repaint();
        }

        
    }

   
    private class DrawPanel extends JPanel
    {
        private Point start;
        private Paint color = first;
        private float lineWidth = ((Integer)width.getValue()).floatValue();
        private Stroke stroke = new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        private float[] dashValue = new float[1];
        private boolean fill, check = false;
        
        public DrawPanel()
        {
            super();
            addMouseListener(new MouseHandler());
            addMouseMotionListener(new MouseHandler());
            setBackground((Color.WHITE));
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D)g;
            
            for(int n = 0; n < shapesList.size(); n++){
                shapesList.get(n).draw(g2d);
            }
        }


        private class MouseHandler extends MouseAdapter implements MouseMotionListener
        {

            public void mousePressed(MouseEvent event)
            {
                start = event.getPoint();
                lineWidth = ((Integer)width.getValue()).floatValue();
                dashValue = new float[1];
                dashValue[0] = ((Integer)dash.getValue()).floatValue();
                
                fill = filled.isSelected();
                
                if(gradient.isSelected())
                {  color = new GradientPaint(0, 0, first, 50, 50, second, true);
                }
                else
                { color = first;
                }
                
                if (line.isSelected())
                {stroke = new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, dashValue, 0);
                } 
                else
                {stroke = new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                }}

            public void mouseReleased(MouseEvent event)
            {
                shapesList.remove(shapesList.size() - 1);
                check = false;
                
                if(shapes.getSelectedItem()=="Line")
                {
                    shapesList.add(new MyLine(start, event.getPoint(), color,stroke));
                }
                else if(shapes.getSelectedItem()=="Oval")
                {
                    shapesList.add(new MyOval(start, event.getPoint(), color,stroke,fill));
                }
                else
                {
                    shapesList.add(new MyRectangle(start, event.getPoint(), color,stroke,fill));
                }
                
                
                drawPanel.repaint();
  }

            @Override
            public void mouseDragged(MouseEvent event)
            {
                statusLabel.setText("(" + (int)event.getPoint().getX() + ", " + (int)event.getPoint().getY() + ")");
                if (shapesList.size() != 0 && check){
                    shapesList.remove(shapesList.size()-1);
                }
                
                if(shapes.getSelectedItem()=="Line")
                {
                    shapesList.add(new MyLine(start, event.getPoint(), color,stroke));
                }
                else if(shapes.getSelectedItem()=="Oval")
                {
                    shapesList.add(new MyOval(start, event.getPoint(), color,stroke,fill));
                }
                else
                {
                    shapesList.add(new MyRectangle(start, event.getPoint(), color,stroke,fill));
                }
                check = true;
                drawPanel.repaint();
            }

            
            
            
            
        @Override
            public void mouseMoved(MouseEvent event)
            {
                statusLabel.setText("(" + (int)event.getPoint().getX() + ", " + (int)event.getPoint().getY() + ")");
            }}}}