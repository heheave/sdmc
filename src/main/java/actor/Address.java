package actor;

/**
 * Created by xiaoke on 17-5-6.
 */
public class Address {

    private final String ip;

    private final int port;

    private boolean isOn;

    private long lastOn;

    public Address(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public boolean isOn() {
        return isOn;
    }

    public long getLastOn() {
        return lastOn;
    }

    public void update() {
        isOn = true;
        lastOn = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        if (port != address.port) return false;
        return ip != null ? ip.equals(address.ip) : address.ip == null;

    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }
}