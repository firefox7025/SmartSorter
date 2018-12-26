package com.ultimaengineering.smartdoc.omegaclassifier;

import com.ultimaengineering.smartdoc.classification.titles.impl.OmegaTitleClassifier;
import com.ultimaengineering.smartdoc.contract.domain.State;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.DefaultResourceCache;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
@Slf4j
public class App {

    private static final OmegaTitleClassifier omegaTitleClassifier = new OmegaTitleClassifier();

    public static void main( String[] args ) {
        try {
            System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
           log.info("Beginning pdf search");
            Path titles = Paths.get("Z:", "titles", "NGC");
            List<Path> files = getFiles(titles);
            Collections.shuffle(files);
            files.parallelStream().forEach(App::splitPdf);
        } catch (Exception e) {
            log.error("I don't even know man {}" , e);
        }
    }

    public static State classifyImage(BufferedImage bufferedImage) {
        State state = omegaTitleClassifier.classifyImage(bufferedImage);
        return state;
    }

    public static void splitPdf(Path file) {
        Path titles = Paths.get("Z:", "titles", "Unsorted");
        try(PDDocument document = PDDocument.load(file.toFile())) {
            document.setResourceCache(new VoidResourceCache());
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            List<State> stateList = new ArrayList<>();
            for(int i = 0; i < document.getNumberOfPages(); i++) {
                //State state = classifyImage(pdfRenderer.renderImageWithDPI(i, 300));
                Path newImage = Paths.get(titles.toString(), UUID.randomUUID().toString()+".jpg");
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(i, 300);
                ImageIO.write(bufferedImage, "jpg", newImage.toFile());
            }
        } catch (Exception e) {

        }
    }


    public static List<Path> getFiles(Path parentFolder) throws IOException {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.pdf");
        List<Path> collect = Files.walk(parentFolder)
                .filter(Files::isRegularFile)
                .filter(matcher::matches)
                .collect(Collectors.toList());
        log.info("collection count {}", collect.size());
        return collect;
    }

    private static class VoidResourceCache extends DefaultResourceCache
    {
        @Override
        public void put(COSObject indirect, PDXObject xobject) throws IOException
        {
        }
    }
}
