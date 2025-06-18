
public class Consumer extends Thread {
	private Cuby cubo;

	public Consumer(Cuby cubo) {
		this.cubo = cubo;
	}

	@Override
	public void run() {
		for (int i = 0; i < 10; i++) {
			cubo.get();
		}
	}

}
