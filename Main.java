import src.gui.SheetGUI;
import src.config.Database;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        // SheetGUI.showMenu();
        Database.connect();
    }
}
