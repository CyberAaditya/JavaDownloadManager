import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

//this class render a JProgressBar in a table cell.

class ProgressRenderer extends JProgressBar implements TableCellRenderer {
 
    public ProgressRenderer(int min, int max) {
        super(min,max);
    }
    //return this JProgressBar as the renderer for the givenn table cell..
    
    
    public Component getTableCellRendererComponent (
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setValue((int) ((Float) value).floatValue());
            return this;
        }
    }
    
