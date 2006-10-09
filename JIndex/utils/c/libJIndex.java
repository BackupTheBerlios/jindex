package utils.c;

public class libJIndex {
	static {
		System.out.println("loading library");
		System.loadLibrary("JIndex");
	}

	public static native String getMimeType(String filename);

	public static native String getIconFromMimeType(String mimetype);
}
