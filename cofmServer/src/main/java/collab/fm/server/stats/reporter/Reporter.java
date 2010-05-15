package collab.fm.server.stats.reporter;

public interface Reporter {
	public static final String NL = System.getProperty("line.separator");
	public void report();
}
