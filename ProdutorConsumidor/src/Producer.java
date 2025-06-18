import java.util.Iterator;

public class Producer extends Thread {

	private Cuby cubo;

	public Producer(Cuby cubo) {
		this.cubo = cubo;
	}

	@Override
	public void run() {
		for (int i = 0; i < 10; i++) {
			cubo.put((int) (Math.random() * 100));

		}
	}
}
