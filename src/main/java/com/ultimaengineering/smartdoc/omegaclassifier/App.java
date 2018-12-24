package com.ultimaengineering.smartdoc.omegaclassifier;

import com.ultimaengineering.smartdoc.classification.titles.impl.OmegaTitleClassifier;
import com.ultimaengineering.smartdoc.contract.domain.State;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
@Slf4j
public class App {

    OmegaTitleClassifier omegaTitleClassifier = new OmegaTitleClassifier();

    public static void main( String[] args ) {
        try {
            System.out.println("Hello World!");
            Path titles = Paths.get("F:", "titles");
            Set<Path> files = getFiles(titles);
        } catch (Exception e) {
            log.error("I don't even know man {}" , e);
        }
    }

    public State classifyImage(BufferedImage bufferedImage) {
        State state = omegaTitleClassifier.classifyImage(bufferedImage);
        return state;
    }

    public void splitPdf(Path file) {
        try(PDDocument document = PDDocument.load(file.toFile())) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            List<State> stateList = new ArrayList<>();
            for(int i = 0; i < document.getNumberOfPages(); i++) {
              log.info(classifyImage(pdfRenderer.renderImageWithDPI(i, 300));
            }
        } catch (Exception e) {

        }
    }


    public static Set<Path> getFiles(Path parentFolder) throws IOException {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.pdf");
        Set<Path> collect = Files.walk(parentFolder)
                .filter(Files::isRegularFile)
                .filter(matcher::matches)
                .collect(Collectors.toSet());
        log.info("collection count {}", collect.size());
        return collect;
    }
}
