import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class BooleanSearchEngine implements SearchEngine {
    protected Map<String, List<PageEntry>> mapEntry = new HashMap<>();
    protected File pdfsDir;

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        this.pdfsDir = pdfsDir;
        System.out.println("Производится индексация файлов...");
        File[] file = new File(pdfsDir.toURI()).listFiles();
        for (int i = 0; i < file.length; i++) {
            var pdf = new File(file[i].toURI());
            var doc = new PdfDocument(new PdfReader(pdf));

            System.out.println("Название файла " + pdf.getName());
            System.out.println("Количество страниц " + doc.getNumberOfPages());

            for (int y = 1; y <= doc.getNumberOfPages(); y++) {
                var text = PdfTextExtractor.getTextFromPage(doc.getPage(y));
                var words = text.split("\\P{IsAlphabetic}+");
                Map<String, Integer> freqs = new HashMap<>();
                for (var word : words) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    word = word.toLowerCase();
                    freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                }
                List<PageEntry> pageEntryList = null;
                for (var entry : freqs.entrySet()) {
                    if (mapEntry.containsKey(entry.getKey())) {
                        pageEntryList = mapEntry.get(entry.getKey());
                        if (pageEntryList == null)
                            pageEntryList = new ArrayList<>();
                        pageEntryList.add(new PageEntry(pdf.getName(), y, freqs.get(entry.getKey())));
                    } else {
                        pageEntryList = new ArrayList<>();
                        pageEntryList.add(new PageEntry(pdf.getName(), y, freqs.get(entry.getKey())));
                    }
                    mapEntry.put(entry.getKey(), pageEntryList);
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        List<PageEntry> pageEntryList = new ArrayList<>();
        for (var entry : mapEntry.entrySet()) {
            if (entry.getKey().contains(word) && entry.getKey().length() == word.length()) {
                pageEntryList.addAll(entry.getValue());
                System.out.println("На запрос = " + entry.getKey() + ", найдено совпадений = "
                        + pageEntryList);
            }
        }
        return pageEntryList.stream()
                .sorted(Comparator.comparing(PageEntry::getCount).reversed())
                .collect(Collectors.toList());
    }
}
