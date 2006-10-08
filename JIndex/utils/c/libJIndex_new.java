package utils.c;

public class libJIndex_new {
    static {
        System.out.println("entred libJIndex_new"
                );
        System.out.println(getMimeType("/etc/passwd"));
    }
  public static native String getMimeType(String filename);
  public static native String getIconFromMimeType(String mimetype);
}
