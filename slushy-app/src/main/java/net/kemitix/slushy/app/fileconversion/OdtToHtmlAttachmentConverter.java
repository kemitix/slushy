package net.kemitix.slushy.app.fileconversion;

import lombok.extern.java.Log;
import net.kemitix.slushy.app.Attachment;
import net.kemitix.slushy.app.LocalAttachment;
import org.odftoolkit.odfdom.converter.xhtml.XHTMLConverter;
import org.odftoolkit.odfdom.converter.xhtml.XHTMLOptions;
import org.odftoolkit.odfdom.doc.OdfTextDocument;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.stream.Stream;

@Log
@ApplicationScoped
public class OdtToHtmlAttachmentConverter
        implements AttachmentConverter {

    @Inject XHTMLConverter xhtmlConverter;

    @Override
    public boolean canHandle(Attachment attachment) {
        return attachment.getOriginalFilename()
                .getName()
                .toLowerCase()
                .endsWith(".odt");
    }

    @Override
    public Optional<LocalAttachment> convert(File sourceFile, File targetFile) {
        try (
                InputStream in = new FileInputStream(sourceFile);
                OutputStream out = new FileOutputStream(targetFile);
        ) {
            OdfTextDocument document = OdfTextDocument.loadDocument(in);
            xhtmlConverter.convert(document, out, XHTMLOptions.create());
            if (targetFile.exists()) {
                return Optional.of(new LocalAttachment(targetFile, sourceFile));
            } else {
                throw new FileNotFoundException(targetFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Stream<String> canConvertFrom() {
        return Stream.of("odt");
    }

}
