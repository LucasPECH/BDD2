package Application;

public class JComboItem {
    private String label;
    private int value;

    public JComboItem(String label, int value) {
        this.value = value;
        this.label = label;
    }

    public int getValue() {
        return this.value;
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public String toString() {
        return label;
    }
}