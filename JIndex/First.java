import java.io.FileNotFoundException;
import java.io.IOException;

import org.gnu.glade.GladeXMLException;
import org.gnu.glade.LibGlade;
import org.gnu.gtk.Gtk;

public class First
{
    private LibGlade firstApp;
    
    public First() throws FileNotFoundException, GladeXMLException, IOException
    {
        firstApp = new LibGlade("glade/jindex.glade", this);
    }
    
    public static void main(String[] args)
    {
        First g;
        
        try {
            Gtk.init(args);
            g = new First();
            Gtk.main();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

