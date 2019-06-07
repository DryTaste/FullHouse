package nl.Groep5.FullHouse.UI;

public class ComboItem {
    private String key;
    private String value;

    public ComboItem(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString()
    {
        return this.key;
    }

    public String getKey()
    {
        return this.key;
    }

    public String getValue()
    {
        return this.value;
    }
}
