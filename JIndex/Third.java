import org.gnu.gnome.About;
import org.gnu.gnome.App;
import org.gnu.gnome.Program;
import org.gnu.gnome.UIInfo;
import org.gnu.gtk.ButtonsType;
import org.gnu.gtk.DialogFlags;
import org.gnu.gtk.Gtk;
import org.gnu.gtk.GtkStockItem;
import org.gnu.gtk.MessageDialog;
import org.gnu.gtk.MessageType;
import org.gnu.gtk.StatusBar;
import org.gnu.gtk.event.ButtonEvent;
import org.gnu.gtk.event.ButtonListener;
import org.gnu.gtk.event.LifeCycleEvent;
import org.gnu.gtk.event.LifeCycleListener;
import org.gnu.gtk.event.MenuItemEvent;
import org.gnu.gtk.event.MenuItemListener;

public class Third implements MenuItemListener, ButtonListener {
	private App app = null;
 	private StatusBar statusbar = null;
 	public static final String appVersion = "0.1";

	public Third() {
		createMainWindow();
		createMenus();
		createToolbar();
		app.showAll();
	}

	private void createMainWindow() {
		app = new App("Third", "Third App");
		app.setDefaultSize(350, 200);
		app.addListener(new LifeCycleListener() {
			public void lifeCycleEvent(LifeCycleEvent event) {}
			public boolean lifeCycleQuery(LifeCycleEvent event) {
				Gtk.mainQuit();
				return false;
			}
		});
	}

	private void createMenus() {

		UIInfo fileMenu[] = {
			UIInfo.newItem("New Window", "Open a new application window", this),
			UIInfo.separator(),
			UIInfo.openItem((MenuItemListener) this),
			UIInfo.saveItem((MenuItemListener) this),
			UIInfo.saveAsItem((MenuItemListener) this),
			UIInfo.separator(),
			UIInfo.closeItem((MenuItemListener) this),
			UIInfo.quitItem(new MenuItemListener() { 
				public void menuItemEvent(MenuItemEvent event) { 
					fileExit();
				}
			}), 
			UIInfo.end()
		};

		UIInfo editMenu[] = {
			UIInfo.undoItem((MenuItemListener) this),
			UIInfo.redoItem((MenuItemListener) this),
			UIInfo.separator(),
			UIInfo.cutItem((MenuItemListener) this),
			UIInfo.copyItem((MenuItemListener) this),
			UIInfo.pasteItem((MenuItemListener) this),
			UIInfo.separator(),
			UIInfo.findItem((MenuItemListener) this),
			UIInfo.findAgainItem((MenuItemListener) this),
			UIInfo.replaceItem((MenuItemListener) this),
			UIInfo.propertiesItem((MenuItemListener) this),
			UIInfo.end()
		};

		UIInfo moveMenu[] = {
			UIInfo.item("_Up", "Move selection up", (MenuItemListener) this),
			UIInfo.item("D_own", "Move selection down", (MenuItemListener) this),
			UIInfo.end()
		};

		UIInfo helpMenu[] = {
			UIInfo.help("second"),
			UIInfo.aboutItem(new MenuItemListener() { 
				public void menuItemEvent(MenuItemEvent event) { 
					helpAbout();
				}
			}), 
			UIInfo.end()
		};

		UIInfo mainMenu[] = {
			UIInfo.subtree("_File", fileMenu),
			UIInfo.subtree("_Edit", editMenu),
			UIInfo.subtree("_Move", moveMenu),
			UIInfo.subtree("_Help", helpMenu),
			UIInfo.end()
		};
		app.createMenus(mainMenu);
	}

	private void createToolbar() {

		UIInfo toolbar[] = {
			UIInfo.itemStock("New", "Create a new file", 
					(ButtonListener) this, GtkStockItem.NEW),
			UIInfo.itemStock("Open", "Open a file", 
					(ButtonListener) this, GtkStockItem.OPEN),
			UIInfo.separator(),
			UIInfo.itemStock("Save", "Save this file", 
					(ButtonListener) this, GtkStockItem.SAVE),
			UIInfo.itemStock("Save As", "Save this file as", 
					(ButtonListener) this, GtkStockItem.SAVE_AS),
			UIInfo.separator(),
			UIInfo.itemStock("Close", "Close this file", 
					(ButtonListener) this, GtkStockItem.CLOSE),
			UIInfo.end()
		};

		app.createToolBar(toolbar);
	}

	public void helpAbout() {
	    String appname = "Java-GNOME Tutorial";
	    String version = "0.1";
	    String license = "GPL";
	    String description = "Java-GNOME Tutorial.";
	    String authors[] = { 
	        "http://java-gnome.sf.net", 
	        "http://www.gtk.org" 
	    };
	    String documentors[] = { 
	        "java-gnome-developer@lists.sf.net", 
	        "http://www.gnome.org"
	    };
	    String translators = "Language Guys Inc.";
	    String website = "http://java-gnome.sf.net";

//	    AboutDialog about = new AboutDialog();
//        about.setName(appname);
//        about.setVersion(version);
//        about.setLicense(license);
//        about.setComments(description);
//        about.setAuthors(authors);
//        about.setDocumenters(documentors);
//        about.setTranslatorCredits(translators);
//        about.setWebsite(website);
//		about.show();
	}

	public void fileExit() {
		Gtk.mainQuit();
	}

	public void menuItemEvent(MenuItemEvent event) {
		displayMessage();
	}

	public void buttonEvent(ButtonEvent event) {
		displayMessage();
	}
	
	private void displayMessage() {
		MessageDialog dialog = new MessageDialog(app, DialogFlags.MODAL, 
				MessageType.INFO, ButtonsType.OK,
				"Not implemented", false);
		dialog.run();
		dialog.destroy();
	}

	public static void main(String[] args) {
		Program.initGnomeUI("Third", Third.appVersion, args);
		new Third();
		Gtk.main();
	}
}

