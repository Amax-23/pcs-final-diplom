import java.io.Serializable;
import java.util.Comparator;

public class PageEntry implements Comparable<PageEntry>, Serializable {
    private final String pdfName;
    private final int page;
    private final int count;
    //Comparator<PageEntry> comparator = Comparator.comparing(obj -> obj.getCount());

    public PageEntry(String pdfName, int page, int count) {
        this.pdfName = pdfName;
        this.page = page;
        this.count = count;
    }

    @Override
    public int compareTo(PageEntry o) {
        return Integer.compare(this.getCount(), o.getCount());
        //return 0;
    }

    public String getPdfName() {
        return pdfName;
    }

    public int getPage() {
        return page;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "{" +
                "\"pdfName\":" + '\"' + pdfName + '\"' +
                ", \"page\":" + page +
                ", \"count\":" + count +
                '}';
    }
}
