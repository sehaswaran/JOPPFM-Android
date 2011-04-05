package fm.jopp.android.background.obtainer;

public interface Obtainer {

    public void registerObserver();

    public void unregisterObserver();

    public boolean fetch(final int limit);
}
